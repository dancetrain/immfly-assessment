package com.immfly.order.management.platform.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Order(
        UUID id,
        String seatLetter,
        int seatNumber,
        OrderStatus status,
        String buyerEmail,
        List<UUID> productIds,
        double totalPrice,
        String paymentStatus,
        LocalDateTime paymentDate,
        String paymentGateway
) {
}
