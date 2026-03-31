package com.example.demo.entity;

import com.example.demo.enums.DeliveryStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingNumber;
    private Long customerId;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private String serviceType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sender_address_id")
    private Address senderAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "receiver_address_id")
    private Address receiverAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "package_id")
    private Package packageDetails;

    private Double charges;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledPickup;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = DeliveryStatus.DRAFT;
        this.trackingNumber = "SC" + System.currentTimeMillis();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Delivery() {}

    public Delivery(Long id, String trackingNumber, Long customerId,
                    DeliveryStatus status, String serviceType,
                    Address senderAddress, Address receiverAddress,
                    Package packageDetails, Double charges,
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

    public Address getSenderAddress() {
        return senderAddress;
    }

    public Address getReceiverAddress() {
        return receiverAddress;
    }

    public Package getPackageDetails() {
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

    public void setSenderAddress(Address senderAddress) {
        this.senderAddress = senderAddress;
    }

    public void setReceiverAddress(Address receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public void setPackageDetails(Package packageDetails) {
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