package com.smartcourier.admin.integration;

import com.smartcourier.admin.dto.TrackingEventSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "tracking-service")
public interface TrackingClient {

    @GetMapping("/api/tracking/internal/recent")
    List<TrackingEventSummary> getRecentTrackingEvents();
}
