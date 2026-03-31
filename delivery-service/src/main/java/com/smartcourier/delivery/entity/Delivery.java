package com.smartcourier.delivery.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String trackingNumber;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String customerEmail;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "contactName", column = @Column(name = "sender_contact_name")),
            @AttributeOverride(name = "phoneNumber", column = @Column(name = "sender_phone_number")),
            @AttributeOverride(name = "line1", column = @Column(name = "sender_line1")),
            @AttributeOverride(name = "line2", column = @Column(name = "sender_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "sender_city")),
            @AttributeOverride(name = "state", column = @Column(name = "sender_state")),
            @AttributeOverride(name = "country", column = @Column(name = "sender_country")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "sender_postal_code"))
    })
    private AddressEmbeddable senderAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "contactName", column = @Column(name = "receiver_contact_name")),
            @AttributeOverride(name = "phoneNumber", column = @Column(name = "receiver_phone_number")),
            @AttributeOverride(name = "line1", column = @Column(name = "receiver_line1")),
            @AttributeOverride(name = "line2", column = @Column(name = "receiver_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "receiver_city")),
            @AttributeOverride(name = "state", column = @Column(name = "receiver_state")),
            @AttributeOverride(name = "country", column = @Column(name = "receiver_country")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "receiver_postal_code"))
    })
    private AddressEmbeddable receiverAddress;

    @Embedded
    private PackageEmbeddable packageDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(nullable = false)
    private BigDecimal chargeAmount;

    private BigDecimal declaredValue;
    private String paymentReference;
    private LocalDate scheduledPickupDate;
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public AddressEmbeddable getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(AddressEmbeddable senderAddress) {
        this.senderAddress = senderAddress;
    }

    public AddressEmbeddable getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(AddressEmbeddable receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public PackageEmbeddable getPackageDetails() {
        return packageDetails;
    }

    public void setPackageDetails(PackageEmbeddable packageDetails) {
        this.packageDetails = packageDetails;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getDeclaredValue() {
        return declaredValue;
    }

    public void setDeclaredValue(BigDecimal declaredValue) {
        this.declaredValue = declaredValue;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public LocalDate getScheduledPickupDate() {
        return scheduledPickupDate;
    }

    public void setScheduledPickupDate(LocalDate scheduledPickupDate) {
        this.scheduledPickupDate = scheduledPickupDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
