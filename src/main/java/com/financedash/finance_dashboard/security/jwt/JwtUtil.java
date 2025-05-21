package com.financedash.finance_dashboard.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private static final long ACCESS_TOKEN_EXPIRATION_MS = 1000 * 60 * 60; // 1 hour
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 1000 * 60 * 60 * 24 * 7; // 7 days

    // ✅ Generate a short-lived access token
    public String generateAccessToken(String email) {
        long expirationMs = ACCESS_TOKEN_EXPIRATION_MS;
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMs))
                .sign(getAlgorithm());
    }

    // ✅ Generate long-lived refresh token
    public String generateRefreshToken(String email) {
        long expirationMs = REFRESH_TOKEN_EXPIRATION_MS;
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMs))
                .sign(getAlgorithm());
    }

    // ✅ Common JWT generation with custom expiration
    public String generateJwtToken(String email) {
        // Validate email input
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }

        long expirationMs = REFRESH_TOKEN_EXPIRATION_MS;
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMs))
                .sign(getAlgorithm());
    }

    // ✅ Extract email (subject) from JWT token
    public String extractEmail(String token) {
        try {
            return JWT.require(getAlgorithm())
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException ex) {
            return null;
        }
    }

    // ✅ Validate token
    public boolean validateToken(String token) {
        try {
            JWT.require(getAlgorithm())
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    // ✅ Optional: match token with email
    public boolean isTokenValid(String token, String email) {
        String extractedEmail = extractEmail(token);
        return extractedEmail != null && extractedEmail.equals(email);
    }

    // ✅ Get signing algorithm
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }
}
