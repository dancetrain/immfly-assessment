package com.immfly.order.management.platform.application;

import com.immfly.order.management.platform.domain.model.Order;
import com.immfly.order.management.platform.domain.model.OrderStatus;
import com.immfly.order.management.platform.domain.model.Product;
import com.immfly.order.management.platform.domain.port.in.OrderService;
import com.immfly.order.management.platform.domain.port.out.OrderRepository;
import com.immfly.order.management.platform.domain.port.out.PaymentGateway;
import com.immfly.order.management.platform.domain.port.out.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentGateway paymentGateway;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            PaymentGateway paymentGateway) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.paymentGateway = paymentGateway;
    }

    @Override
    @Transactional
    public Order createOrder(String seatLetter, int seatNumber) {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(
                orderId,
                seatLetter,
                seatNumber,
                OrderStatus.OPEN,
                null,
                Map.of(),
                0.0,
                null,
                null,
                null
        );
        orderRepository.save(order);
        return order;
    }

    @Override
    @Transactional
    public Order updateOrder(UUID orderId, String buyerEmail, Map<UUID, Integer> productsQty) {
        Order order = findOrThrow(orderId);

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("Order is not open for updates.");
        }

        // Validate stock & calculate total
        double total = 0.0;
        if (productsQty != null) {
            for (Map.Entry<UUID, Integer> item : productsQty.entrySet()) {
                Product product = productRepository.findById(item.getKey())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getKey()));
                if (product.getStock() < item.getValue()) {
                    throw new IllegalStateException("Product out of stock: " + product.getName());
                }
                total += item.getValue() * product.getPrice().doubleValue();
            }
        }

        order.setBuyerEmail(buyerEmail);
        order.setProductsQty(productsQty);
        order.setTotalPrice(total);

        orderRepository.update(order);
        return order;
    }

    @Override
    @Transactional
    public Order finishOrder(UUID orderId, String cardToken, String paymentGatewayName) {
        Order order = findOrThrow(orderId);

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("Order is not open for finishing.");
        }

        String paymentResult = paymentGateway.processPayment(cardToken, order.getTotalPrice(), paymentGatewayName);

        order.setStatus(OrderStatus.FINISHED);
        order.setPaymentStatus(paymentResult);
        order.setPaymentDate(LocalDateTime.now());
        order.setPaymentGateway(paymentGatewayName);

        orderRepository.update(order);
        return order;
    }

    @Override
    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = findOrThrow(orderId);
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.update(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrder(UUID orderId) {
        return findOrThrow(orderId);
    }

    private Order findOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }
}