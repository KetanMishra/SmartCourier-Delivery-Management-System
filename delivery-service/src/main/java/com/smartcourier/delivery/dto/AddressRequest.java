package com.smartcourier.delivery.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank String contactName,
        @NotBlank String phoneNumber,
        @NotBlank String line1,
        String line2,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String country,
        @NotBlank String postalCode
) {
}
