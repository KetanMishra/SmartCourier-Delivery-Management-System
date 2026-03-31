package com.smartcourier.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    public String generateToken(String username, Long userId, Set<String> roles) {
        Instant expiry = Instant.now().plus(12, ChronoUnit.HOURS);
        return Jwts.builder()
                .subject(username)
                .claims(Map.of("userId", userId, "roles", roles))
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey())
                .compact();
    }

    public Instant extractExpiration(String token) {
        return extractAllClaims(token).getExpiration().toInstant();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && extractExpiration(token).isAfter(Instant.now());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
