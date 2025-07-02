package com.immfly.order.management.platform.domain.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Category {
    private UUID id;
    private String name;
    private UUID parentCategoryId;
}