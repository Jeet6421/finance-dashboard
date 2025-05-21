package com.financedash.finance_dashboard.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object containing authentication tokens.
 * Used for returning JWT access and refresh tokens after successful authentication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    @NotBlank(message = "Access token cannot be blank")
    private String accessToken;

    @NotBlank(message = "Refresh token cannot be blank")
    private String refreshToken;
    
    /**
     * Validates that both tokens are present
     * @throws IllegalArgumentException if either token is null or empty
     */
    public void validateTokens() {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Refresh token cannot be null or empty");
        }
    }
}