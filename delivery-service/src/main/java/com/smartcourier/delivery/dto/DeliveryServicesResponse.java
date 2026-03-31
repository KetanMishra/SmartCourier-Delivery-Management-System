package com.smartcourier.delivery.dto;

import java.util.List;

public record DeliveryServicesResponse(
        List<String> supportedServices,
        List<String> lifecycle,
        List<String> exceptionStates
) {
}
