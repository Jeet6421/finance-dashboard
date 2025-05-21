package com.financedash.finance_dashboard.controller;

import com.financedash.finance_dashboard.config.ApiEndpoints;
import com.financedash.finance_dashboard.exception.AuthenticationException;
import com.financedash.finance_dashboard.payload.*;
import com.financedash.finance_dashboard.registration.RegistrationRequest;
import com.financedash.finance_dashboard.registration.RegistrationResponse;
import com.financedash.finance_dashboard.registration.RegistrationService;
import com.financedash.finance_dashboard.service.auth.AuthenticationService;
import com.financedash.finance_dashboard.token.TokenRefreshException;
import com.financedash.finance_dashboard.utils.EmailUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Handles authentication-related API endpoints including login, registration,
 * token refresh, and logout.
 */
@Slf4j
@RestController
@RequestMapping(ApiEndpoints.AuthPaths.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthenticationService authService;
    private final RegistrationService registrationService;

    /**
     * Login a user using email and password.
     */
    @PostMapping(value = ApiEndpoints.AuthPaths.LOGIN, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Login user", description = "Authenticates a user with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        log.debug("Login attempt for: {}", EmailUtils.maskEmail(request.getEmail()));
        AuthResponse response = authService.authenticate(request);
        return ApiResponse.success(response, AuthMessages.LOGIN_SUCCESS).toResponseEntity();
    }

    /**
     * Refresh the access token using a valid refresh token.
     */
    @PostMapping(value = ApiEndpoints.AuthPaths.REFRESH, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Refresh access token", description = "Issues a new access token using a valid refresh token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token successfully refreshed",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid refresh token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Refresh token expired")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("Token refresh attempt for: {}", EmailUtils.obfuscateToken(request.getRefreshToken()));
        AuthResponse response = authService.refreshToken(request);
        return ApiResponse.success(response, AuthMessages.TOKEN_REFRESH_SUCCESS).toResponseEntity();
    }

    /**
     * Logout a user by invalidating their refresh token.
     */
    @PostMapping(ApiEndpoints.AuthPaths.LOGOUT)
    @Operation(summary = "Logout user", description = "Invalidates the user's refresh token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully logged out"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid refresh token format"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Refresh token not found")
    })
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("Logout attempt for token: {}", EmailUtils.obfuscateToken(request.getRefreshToken()));
        authService.logout(request.getRefreshToken());
        return ApiResponse.success(AuthMessages.LOGOUT_SUCCESS).toResponseEntity();
    }

    /**
     * Register a new user account.
     */
    @PostMapping(ApiEndpoints.AuthPaths.REGISTER)
    @ResponseStatus(CREATED)
    @Operation(summary = "Register user", description = "Registers a new user account")
    public RegistrationResponse register(@Valid @RequestBody RegistrationRequest request) {
        log.debug("Registration request received for: {}", EmailUtils.maskEmail(request.getEmail()));
        String result = registrationService.register(request);
        log.debug("Registration successful: {}", result);
        return RegistrationResponse.success(result);
    }

    /**
     * Handle authentication exceptions (e.g., invalid credentials).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication error: {}", ex.getMessage());
        return ApiResponse.<Void>error(ex.getMessage(), UNAUTHORIZED).toResponseEntity();
    }

    /**
     * Handle token refresh exceptions (e.g., expired refresh token).
     */
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ApiResponse<Void>> handleTokenRefreshException(TokenRefreshException ex) {
        log.warn("Token refresh error: {}", ex.getMessage());
        return ApiResponse.<Void>error(ex.getMessage(), FORBIDDEN).toResponseEntity();
    }

    /**
     * Contains success messages for authentication-related operations.
     */
    private static final class AuthMessages {
        static final String LOGIN_SUCCESS = "Login successful";
        static final String TOKEN_REFRESH_SUCCESS = "Token refreshed successfully";
        static final String LOGOUT_SUCCESS = "Logout successful";
        private AuthMessages() {}
    }
}
