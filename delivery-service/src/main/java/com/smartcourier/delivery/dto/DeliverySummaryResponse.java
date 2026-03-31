package com.smartcourier.delivery.dto;

import java.util.Map;

public record DeliverySummaryResponse(
        long totalDeliveries,
        Map<String, Long> byStatus
) {
}
