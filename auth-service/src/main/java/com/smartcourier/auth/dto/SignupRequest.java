package com.smartcourier.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record SignupRequest(
        @NotBlank String fullName,
        @Email String email,
        @Size(min = 8, max = 64) String password,
        String phoneNumber,
        Set<String> roles
) {
}
