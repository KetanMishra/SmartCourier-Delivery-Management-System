package com.smartcourier.admin.dto;

import java.util.List;
import java.util.Map;

public record DashboardResponse(
        long totalDeliveries,
        Map<String, Long> deliveryStatusCounts,
        long totalHubs,
        long totalUsers,
        List<TrackingEventSummary> recentTrackingEvents
) {
}
