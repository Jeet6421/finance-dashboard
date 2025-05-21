package com.financedash.finance_dashboard.token;

import com.financedash.finance_dashboard.appUser.AppUser;
import com.financedash.finance_dashboard.appUser.UserRepository;
import com.financedash.finance_dashboard.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;  // Assume you have a utility class for generating JWTs

    private static final long REFRESH_TOKEN_DURATION_MS = 7 * 24 * 60 * 60 * 1000; // 7 days

    // Create or update a refresh token for the user
    public RefreshToken createRefreshToken(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found: " + email));

        Optional<RefreshToken> existingTokenOpt = repository.findByUser(user);

        RefreshToken token;
        if (existingTokenOpt.isPresent()) {
            // Update the existing token
            token = existingTokenOpt.get();
            token.setToken(UUID.randomUUID().toString());
            token.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS));
        } else {
            // Create a new token if none exists
            token = RefreshToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS))
                    .build();
        }

        return repository.save(token);
    }

    // Find a refresh token by its value
    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    // Check if the refresh token has expired
    public boolean isExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    // Delete a refresh token by user
    public void deleteByUser(AppUser user) {
        repository.deleteByUser(user);
    }

    // Verify the expiration of a refresh token and throw an exception if expired
    public RefreshToken verifyExpiration(RefreshToken token) throws TokenRefreshException {
        if (isExpired(token)) {
            repository.delete(token);
            throw new TokenRefreshException("Refresh token expired. Please sign in again.");
        }
        return token;
    }

    // Handle the case when the refresh token is invalid or expired, and refresh the token
    public String refreshAccessToken(String refreshToken) throws TokenRefreshException {
        RefreshToken token = findByToken(refreshToken)
                .orElseThrow(() -> new TokenRefreshException("Invalid or expired refresh token"));

        verifyExpiration(token); // Ensure it's not expired

        // Generate a new access token (this assumes you have a JwtUtil to generate the token)
        return generateAccessToken(token.getUser().getEmail());
    }

    // Check if the provided refresh token is valid (not expired)
    public boolean isValidRefreshToken(String refreshToken) {
        Optional<RefreshToken> tokenOpt = findByToken(refreshToken);
        if (tokenOpt.isEmpty()) {
            return false;  // Token not found
        }

        RefreshToken token = tokenOpt.get();
        return !isExpired(token);  // Check if the token is expired
    }

    // Generate a new access token using the user's email
    public String generateAccessToken(String email) {
        // Assuming you have a method in JwtUtil to generate the token
        return jwtUtil.generateJwtToken(email); // Generate JWT token using email
    }

    public String rotateRefreshToken(RefreshToken refreshToken) throws TokenRefreshException {
        verifyExpiration(refreshToken);
        String newToken = UUID.randomUUID().toString();
        refreshToken.setToken(newToken);
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS));
        repository.save(refreshToken);
        return newToken;
    }

    public void deleteByToken(String refreshToken) {
        repository.findByToken(refreshToken)
                .ifPresent(repository::delete);
    }
}
