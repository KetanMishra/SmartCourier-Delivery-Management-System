package com.example.demo.entity;

import com.example.demo.enums.TrackingStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_events")
public class TrackingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingNumber;
    private Long deliveryId;

    @Enumerated(EnumType.STRING)
    private TrackingStatus status;

    private String location;
    private String description;
    private LocalDateTime eventTime;

    @PrePersist
    public void prePersist() {
        this.eventTime = LocalDateTime.now();
    }

    public TrackingEvent() {}

    public Long getId() {
        return id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public TrackingStatus getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public void setStatus(TrackingStatus status) {
        this.status = status;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }
}
