package com.smartcourier.tracking.security;

import java.util.List;

public record JwtUserPrincipal(
        Long userId,
        String email,
        List<String> roles
) {
}
