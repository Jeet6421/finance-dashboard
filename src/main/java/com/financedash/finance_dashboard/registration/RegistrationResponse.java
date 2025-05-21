package com.financedash.finance_dashboard.registration;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Represents the response for registration-related operations.
 * This immutable record encapsulates the result of registration operations
 * such as user registration and email confirmation.
 */
public record RegistrationResponse(
        String message,
        HttpStatus status,
        Instant timestamp
) {

    @Builder
    public RegistrationResponse {
        // Constructor validation for message and status
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        // Ensure timestamp is set, default to now if null
        timestamp = (timestamp != null) ? timestamp : Instant.now();
    }

    /**
     * Creates a successful registration response.
     *
     * @param message the result message of the registration operation
     * @return a new RegistrationResponse instance
     */
    public static RegistrationResponse success(String message) {
        return new RegistrationResponse(message, HttpStatus.OK, Instant.now());
    }

    /**
     * Creates a failed registration response.
     *
     * @param message the error message
     * @param status  the HTTP status code
     * @return a new RegistrationResponse instance
     */
    public static RegistrationResponse error(String message, HttpStatus status) {
        return new RegistrationResponse(message, status, Instant.now());
    }

    /**
     * Default constructor to handle a successful registration operation.
     *
     * @return a successful RegistrationResponse
     */
    public static RegistrationResponse defaultSuccess() {
        return new RegistrationResponse("Registration successful", HttpStatus.CREATED, Instant.now());
    }
}
