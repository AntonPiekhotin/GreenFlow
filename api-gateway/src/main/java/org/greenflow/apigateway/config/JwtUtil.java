package org.greenflow.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUserId(String token) {
        return extractAllClaimsFromToken(token).get("userId", String.class);
    }

    public String extractRole(String token) {
        return extractAllClaimsFromToken(token).get("role", String.class);
    }

    public String extractEmail(String token) {
        return extractAllClaimsFromToken(token).get("email", String.class);
    }

    public boolean isInvalid(String token) {
        return this.isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return this.extractAllClaimsFromToken(token).getExpiration().before(new Date());
    }

    @SuppressWarnings("deprecation")
    public Claims extractAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}