package com.immfly.order.management.platform.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Order {
    private UUID id;
    private String seatLetter;
    private int seatNumber;
    private OrderStatus status;
    private String buyerEmail;
    private Map<UUID, Integer> productsQty; // Map of product ID to quantity
    private double totalPrice;
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private String paymentGateway;
}
