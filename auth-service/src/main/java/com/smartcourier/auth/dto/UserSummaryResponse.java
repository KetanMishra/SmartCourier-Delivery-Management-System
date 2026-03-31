package com.smartcourier.auth.dto;

import java.time.Instant;
import java.util.Set;

public record UserSummaryResponse(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        Set<String> roles,
        Instant createdAt
) {
}
