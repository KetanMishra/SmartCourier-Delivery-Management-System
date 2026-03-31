package com.smartcourier.tracking.dto;

import java.time.Instant;

public record DeliveryProofResponse(
        Long deliveryId,
        String recipientName,
        String proofImageUrl,
        String note,
        Instant deliveredAt
) {
}
