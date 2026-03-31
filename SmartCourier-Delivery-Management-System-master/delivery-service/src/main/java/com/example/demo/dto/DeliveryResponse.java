package com.example.demo.dto;

import com.example.demo.enums.DeliveryStatus;
import java.time.LocalDateTime;

public class DeliveryResponse {

    private Long id;
    private String trackingNumber;
    private Long customerId;
    private DeliveryStatus status;
    private String serviceType;
    private AddressDto senderAddress;
    private AddressDto receiverAddress;
    private PackageDto packageDetails;
    private Double charges;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledPickup;

    public DeliveryResponse() {}

    public DeliveryResponse(Long id, String trackingNumber, Long customerId,
                            DeliveryStatus status, String serviceType,
                            AddressDto senderAddress, AddressDto receiverAddress,
                            PackageDto packageDetails, Double charges,
                            LocalDateTime createdAt, LocalDateTime updatedAt,
                            LocalDateTime scheduledPickup) {
        this.id = id;
        this.trackingNumber = trackingNumber;
        this.customerId = customerId;
        this.status = status;
        this.serviceType = serviceType;
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
        this.packageDetails = packageDetails;
        this.charges = charges;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.scheduledPickup = scheduledPickup;
    }

    public Long getId() {
        return id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public String getServiceType() {
        return serviceType;
    }

    public AddressDto getSenderAddress() {
        return senderAddress;
    }

    public AddressDto getReceiverAddress() {
        return receiverAddress;
    }

    public PackageDto getPackageDetails() {
        return packageDetails;
    }

    public Double getCharges() {
        return charges;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getScheduledPickup() {
        return scheduledPickup;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setSenderAddress(AddressDto senderAddress) {
        this.senderAddress = senderAddress;
    }

    public void setReceiverAddress(AddressDto receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public void setPackageDetails(PackageDto packageDetails) {
        this.packageDetails = packageDetails;
    }

    public void setCharges(Double charges) {
        this.charges = charges;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setScheduledPickup(LocalDateTime scheduledPickup) {
        this.scheduledPickup = scheduledPickup;
    }
}