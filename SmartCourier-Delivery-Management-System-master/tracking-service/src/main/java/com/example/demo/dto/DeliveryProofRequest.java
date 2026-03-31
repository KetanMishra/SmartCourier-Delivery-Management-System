package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeliveryProofRequest {

    @NotBlank
    private String trackingNumber;

    @NotNull
    private Long deliveryId;

    @NotBlank
    private String receiverName;

    private String signature;

    public DeliveryProofRequest() {}

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
}
