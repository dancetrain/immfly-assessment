package com.immfly.order.management.platform.adapter.out.payment;

import com.immfly.order.management.platform.domain.port.out.PaymentGateway;
import org.springframework.stereotype.Component;

@Component
public class MockPaymentGateway implements PaymentGateway {

    static final String PAID = "Paid";
    static final String FAILED = "PaymentFailed";
    static final String OFFLINE = "OfflinePayment";

    @Override
    public String processPayment(String cardToken, double amount, String paymentGateway) {
        if ("mock-success".equalsIgnoreCase(paymentGateway)) return PAID;
        if ("mock-fail".equalsIgnoreCase(paymentGateway)) return FAILED;
        if ("mock-offline".equalsIgnoreCase(paymentGateway)) return OFFLINE;
        return PAID; // default
    }
}