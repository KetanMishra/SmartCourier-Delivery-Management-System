package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TrackingEventRequest {

    @NotBlank
    private String trackingNumber;

    @NotNull
    private Long deliveryId;

    @NotBlank
    private String status;

    private String location;
    private String description;

    public TrackingEventRequest() {}

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
