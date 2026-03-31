package com.smartcourier.delivery.dto;

import com.smartcourier.delivery.entity.ServiceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateDeliveryRequest(
        @Valid @NotNull AddressRequest senderAddress,
        @Valid @NotNull AddressRequest receiverAddress,
        @Valid @NotNull PackageRequest packageDetails,
        @NotNull ServiceType serviceType,
        @Future LocalDate scheduledPickupDate,
        BigDecimal declaredValue
) {
}
