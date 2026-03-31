package com.smartcourier.delivery.dto;

import java.math.BigDecimal;

public record PaymentAuthorizationRequest(
        String deliveryReference,
        BigDecimal amount,
        String customerEmail
) {
}
