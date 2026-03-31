package com.smartcourier.delivery.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PackageRequest(
        @NotBlank String description,
        @NotNull @DecimalMin("0.1") BigDecimal weightKg,
        @NotNull @DecimalMin("1.0") BigDecimal lengthCm,
        @NotNull @DecimalMin("1.0") BigDecimal widthCm,
        @NotNull @DecimalMin("1.0") BigDecimal heightCm,
        boolean fragile
) {
}
