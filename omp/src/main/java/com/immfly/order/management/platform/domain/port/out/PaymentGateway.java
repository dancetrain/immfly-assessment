package com.immfly.order.management.platform.domain.port.out;

public interface PaymentGateway {
    String processPayment(String cardToken, double amount, String paymentGateway);
}