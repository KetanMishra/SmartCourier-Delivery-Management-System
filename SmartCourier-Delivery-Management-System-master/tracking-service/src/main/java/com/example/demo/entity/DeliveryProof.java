package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_proofs")
public class DeliveryProof {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingNumber;
    private Long deliveryId;
    private String receiverName;
    private String signature;
    private String photoPath;
    private LocalDateTime deliveredAt;

    @PrePersist
    public void prePersist() {
        this.deliveredAt = LocalDateTime.now();
    }

    public DeliveryProof() {}

    public Long getId() {
        return id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getSignature() {
        return signature;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
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

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
}
