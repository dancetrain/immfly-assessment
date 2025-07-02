package com.immfly.order.management.platform.adapter.in.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private String seatLetter;
    private int seatNumber;
    private String status;
    private String buyerEmail;
    private Map<UUID, Integer> productIds;
    private double totalPrice;
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private String paymentGateway;
}