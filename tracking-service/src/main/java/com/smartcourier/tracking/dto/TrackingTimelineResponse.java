package com.smartcourier.tracking.dto;

import java.util.List;

public record TrackingTimelineResponse(
        String trackingNumber,
        List<TrackingEventResponse> events
) {
}
