package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeliveryRequest {

    @NotNull
    private Long customerId;

    @NotBlank
    private String serviceType;

    @NotNull
    private AddressDto senderAddress;

    @NotNull
    private AddressDto receiverAddress;

    @NotNull
    private PackageDto packageDetails;

    public DeliveryRequest() {}

    public DeliveryRequest(Long customerId, String serviceType, AddressDto senderAddress,
                           AddressDto receiverAddress, PackageDto packageDetails) {
        this.customerId = customerId;
        this.serviceType = serviceType;
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
        this.packageDetails = packageDetails;
    }

    public Long getCustomerId() {
        return customerId;
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

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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
}