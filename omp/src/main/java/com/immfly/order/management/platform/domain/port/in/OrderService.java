package com.immfly.order.management.platform.domain.port.in;

import com.immfly.order.management.platform.domain.model.Order;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderService {
    Order createOrder(String seatLetter, int seatNumber);
    Order updateOrder(UUID orderId, String buyerEmail, Map<UUID, Integer> productIds);
    Order finishOrder(UUID orderId, String cardToken, String paymentGateway);
    void cancelOrder(UUID orderId);
    Order getOrder(UUID orderId);
}