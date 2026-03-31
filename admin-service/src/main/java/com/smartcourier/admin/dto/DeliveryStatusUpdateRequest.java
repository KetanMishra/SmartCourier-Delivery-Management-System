package com.smartcourier.admin.dto;

public record DeliveryStatusUpdateRequest(
        String status,
        String note
) {
}
