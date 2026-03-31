package com.smartcourier.delivery.dto;

import com.smartcourier.delivery.entity.DeliveryStatus;
import com.smartcourier.delivery.entity.ServiceType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record DeliveryResponse(
        Long id,
        String trackingNumber,
        Long customerId,
        String customerEmail,
        AddressRequest senderAddress,
        AddressRequest receiverAddress,
        PackageRequest packageDetails,
        ServiceType serviceType,
        DeliveryStatus status,
        BigDecimal chargeAmount,
        String paymentReference,
        LocalDate scheduledPickupDate,
        Instant createdAt
) {
}
