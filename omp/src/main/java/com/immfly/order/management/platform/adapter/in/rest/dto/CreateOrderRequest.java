package com.immfly.order.management.platform.adapter.in.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private String seatLetter;
    private int seatNumber;
}