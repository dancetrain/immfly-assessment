package com.immfly.order.management.platform.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CategoryRequest {
    private String name;
    private UUID parentCategoryId;
}
