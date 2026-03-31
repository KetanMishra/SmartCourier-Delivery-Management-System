package com.smartcourier.tracking.messaging;

import java.math.BigDecimal;
import java.time.Instant;

public record DeliveryLifecycleEvent(
        Long deliveryId,
        String trackingNumber,
        Long customerId,
        String customerEmail,
        String status,
        String note,
        BigDecimal chargeAmount,
        Instant happenedAt
) {
}
