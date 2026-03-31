package com.smartcourier.auth.dto;

import java.time.Instant;
import java.util.Set;

public record AuthResponse(
        Long userId,
        String fullName,
        String email,
        Set<String> roles,
        String token,
        Instant expiresAt
) {
}
