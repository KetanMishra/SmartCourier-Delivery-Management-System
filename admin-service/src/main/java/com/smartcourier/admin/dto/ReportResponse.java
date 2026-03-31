package com.smartcourier.admin.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ReportResponse(
        String title,
        long totalDeliveries,
        long deliveredCount,
        long delayedCount,
        BigDecimal totalRevenue,
        Instant generatedAt
) {
}
