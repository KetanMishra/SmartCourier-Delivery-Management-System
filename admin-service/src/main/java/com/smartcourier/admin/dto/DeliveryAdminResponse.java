package com.smartcourier.admin.dto;

import java.math.BigDecimal;

public record DeliveryAdminResponse(
        Long id,
        String trackingNumber,
        Long customerId,
        String customerEmail,
        String serviceType,
        String status,
        BigDecimal chargeAmount,
        String paymentReference
) {
}
