package com.smartcourier.admin.dto;

import java.time.Instant;

public record TrackingEventSummary(
        String trackingNumber,
        String status,
        String location,
        String note,
        Instant eventTime
) {
}
