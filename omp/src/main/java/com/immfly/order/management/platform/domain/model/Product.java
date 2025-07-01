package com.immfly.order.management.platform.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record Product(
        UUID id,
        String name,
        BigDecimal price,
        UUID categoryId,
        String image,
        int stock
) {
}