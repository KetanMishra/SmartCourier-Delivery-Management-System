package com.smartcourier.tracking.controller;

import com.smartcourier.tracking.dto.DeliveryProofResponse;
import com.smartcourier.tracking.dto.DocumentUploadRequest;
import com.smartcourier.tracking.dto.TrackingEventResponse;
import com.smartcourier.tracking.dto.TrackingTimelineResponse;
import com.smartcourier.tracking.entity.DocumentRecord;
import com.smartcourier.tracking.service.TrackingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @PostMapping("/documents/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentRecord uploadDocument(@Valid @RequestBody DocumentUploadRequest request) {
        return trackingService.uploadDocument(request);
    }

    @GetMapping("/{trackingNumber}")
    public TrackingTimelineResponse track(@PathVariable String trackingNumber) {
        return trackingService.getTimeline(trackingNumber);
    }

    @GetMapping("/{deliveryId}/proof")
    public DeliveryProofResponse proof(@PathVariable Long deliveryId) {
        return trackingService.getProof(deliveryId);
    }

    @GetMapping("/internal/recent")
    public List<TrackingEventResponse> recent() {
        return trackingService.getRecentEvents();
    }
}
