package com.smartcourier.delivery.dto;

public record PaymentAuthorizationResponse(
        boolean approved,
        String reference,
        String message
) {
}
