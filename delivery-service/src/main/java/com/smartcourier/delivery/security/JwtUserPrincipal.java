package com.smartcourier.delivery.security;

import java.util.List;

public record JwtUserPrincipal(
        Long userId,
        String email,
        List<String> roles
) {
}
