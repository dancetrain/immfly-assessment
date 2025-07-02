package com.immfly.order.management.platform.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Order {
    private UUID id;
    private String seatLetter;
    private int seatNumber;
    private OrderStatus status;
    private String buyerEmail;
    private List<UUID> productIds;
    private double totalPrice;
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private String paymentGateway;
}
