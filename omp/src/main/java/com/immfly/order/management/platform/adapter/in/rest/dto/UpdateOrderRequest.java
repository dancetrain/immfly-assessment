package com.immfly.order.management.platform.adapter.in.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {
    private String buyerEmail;
    private Map<UUID, Integer> productsQty;
}