package com.financedash.finance_dashboard.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public record ApiResponse<T>(
        @Schema(description = "Indicates if the request was successful") boolean success,
        @Schema(description = "Response message") String message,
        @Schema(description = "Response payload") T data,
        @Schema(description = "HTTP status code") HttpStatus status,
        @Schema(description = "Timestamp of the response")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
        Instant timestamp
) {
    // ============================
    // Validation in Compact Constructor
    // ============================
    public ApiResponse {
        if (message == null || message.trim().isEmpty()) throw new IllegalArgumentException("Message cannot be null or empty");
        if (status == null) throw new IllegalArgumentException("HTTP status cannot be null");
    }

    // ============================
    // Static Factory Methods
    // ============================

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, HttpStatus.OK, Instant.now());
    }

    // Add this method if not already present
    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, message, null, HttpStatus.OK, Instant.now());
    }


    public static <T> ApiResponse<T> created(T data, String message) {
        return new ApiResponse<>(true, message, data, HttpStatus.CREATED, Instant.now());
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return new ApiResponse<>(false, message, null, status, Instant.now());
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(message, HttpStatus.UNAUTHORIZED);
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return error(message, HttpStatus.FORBIDDEN);
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }

    // ============================
    // Optional & Conversion
    // ============================

    public Optional<T> getDataOptional() {
        return Optional.ofNullable(data);
    }

    public ResponseEntity<ApiResponse<T>> toResponseEntity() {
        return ResponseEntity.status(status).body(this);
    }

    // ============================
    // Builder (Optional)
    // ============================

    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }

    public static class ApiResponseBuilder<T> {
        private boolean success;
        private String message;
        private T data;
        private HttpStatus status;

        public ApiResponseBuilder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public ApiResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ApiResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponseBuilder<T> status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(success, message, data, status, Instant.now());
        }
    }
}
