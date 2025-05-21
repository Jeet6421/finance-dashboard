package com.financedash.finance_dashboard.security.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@ConfigurationProperties(prefix = "security")
@Validated
@Getter
@Setter
@Slf4j
public class SecurityConfigProperties {

    // Default configuration values
    private static final List<String> DEFAULT_ALLOWED_ORIGINS = List.of("http://localhost:5174");
    private static final List<String> DEFAULT_PUBLIC_ENDPOINTS = List.of(
            "/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/**"
    );
    private static final List<String> DEFAULT_ALLOWED_METHODS = List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
    );
    private static final List<String> DEFAULT_ALLOWED_HEADERS = List.of(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "Refresh-Token"
    );

    // Configuration properties with validation and defaults
    @NotEmpty(message = "At least one allowed origin must be defined")
    private List<String> allowedOrigins = DEFAULT_ALLOWED_ORIGINS;

    @NotEmpty(message = "At least one public endpoint must be defined")
    private List<String> publicEndpoints = DEFAULT_PUBLIC_ENDPOINTS;

    @NotEmpty(message = "At least one allowed HTTP method must be defined")
    private List<String> allowedMethods = DEFAULT_ALLOWED_METHODS;

    @NotEmpty(message = "At least one allowed header must be defined")
    private List<String> allowedHeaders = DEFAULT_ALLOWED_HEADERS;

    private String corsMapping = "/**";
    private Long maxAgeSeconds = 3600L;

    // Configurable context path (can be set via properties)
    private String contextPath = "/api/v1";

    /**
     * Gets the frontend URL (first origin from allowedOrigins list).
     *
     * @return the first allowed origin or an empty string if the list is empty.
     */
    public String getFrontendUrl() {
        return getFirstValueFromList(allowedOrigins);
    }

    /**
     * Utility method to retrieve the first value of a list or empty string if the list is empty.
     *
     * @param list the list of strings.
     * @return the first value or an empty string.
     */
    private String getFirstValueFromList(List<String> list) {
        return (list == null || list.isEmpty()) ? "" : list.get(0);
    }

    /**
     * Prepares public endpoints by adding the context path to each endpoint.
     */
    @PostConstruct
    public void prependContextPathToEndpoints() {
        this.publicEndpoints = publicEndpoints.stream()
                .map(endpoint -> contextPath + endpoint)
                .toList();

        log.debug("Public Endpoints after context prepended: {}", this.publicEndpoints);
    }
}
