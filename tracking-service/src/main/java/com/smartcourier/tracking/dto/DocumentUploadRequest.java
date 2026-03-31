package com.smartcourier.tracking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DocumentUploadRequest(
        @NotNull Long deliveryId,
        @NotBlank String trackingNumber,
        @NotBlank String documentType,
        @NotBlank String fileName,
        @NotBlank String contentType,
        @NotBlank String storageUrl
) {
}
