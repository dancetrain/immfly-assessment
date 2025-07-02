package com.immfly.order.management.platform.adapter.in.rest;

import com.immfly.order.management.platform.adapter.in.rest.dto.*;
import com.immfly.order.management.platform.domain.model.Order;
import com.immfly.order.management.platform.domain.port.in.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    private final OrderService orderService;

    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request.getSeatLetter(), request.getSeatNumber());
        return ResponseEntity.ok(toResponse(order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable UUID id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(toResponse(order));
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getSeatLetter(),
                order.getSeatNumber(),
                order.getStatus().name(),
                order.getBuyerEmail(),
                order.getProductsQty(),
                order.getTotalPrice(),
                order.getPaymentStatus(),
                order.getPaymentDate(),
                order.getPaymentGateway()
        );
    }
}