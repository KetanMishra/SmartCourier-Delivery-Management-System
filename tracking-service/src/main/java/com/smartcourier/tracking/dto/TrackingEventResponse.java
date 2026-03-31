package com.smartcourier.tracking.dto;

import java.time.Instant;

public record TrackingEventResponse(
        String trackingNumber,
        String status,
        String location,
        String note,
        Instant eventTime
) {
}
