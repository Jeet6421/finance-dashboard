package com.financedash.finance_dashboard.config;

/**
 * Centralizes API endpoint definitions for consistent URL mapping.
 * This interface defines all API paths to avoid hardcoding strings throughout the application.
 */
public interface ApiEndpoints {

    /**
     * API versioning.
     */
    interface ApiVersion {
        String CURRENT = "v1"; // Current API version
        String PREFIX = "v"; // Prefix for versioning
        String PATTERN = PREFIX + "{version:[0-9]+}"; // Version pattern
    }

    /**
     * Base paths for all API routes.
     */
    interface BasePaths {
        String API = "/api"; // Base path for API
        String FULL_PATH = API + "/" + ApiVersion.CURRENT; // Full path including version
    }

    /**
     * Authentication related paths.
     */
    interface AuthPaths {
        String AUTH = "/auth";
        String BASE_PATH = BasePaths.FULL_PATH + AUTH;

        String LOGIN = BASE_PATH + "/login";
        String REFRESH = BASE_PATH + "/refresh";
        String LOGOUT = BASE_PATH + "/logout";
        String REGISTER = BASE_PATH + "/register";
        String CONFIRM = REGISTER + "/confirm";
        String PASSWORD_RESET = BASE_PATH + "/password-reset";

        /**
         * Report-related paths under authentication.
         */
        interface ReportPaths {
            String BASE = BASE_PATH + "/reports";
            String MONTHLY = BASE + "/monthly";
            String CATEGORY = BASE + "/category";
        }
    }

    /**
     * User-related paths.
     */
    interface UserPaths {
        String USERS = "/users";
        String BASE_PATH = BasePaths.FULL_PATH + USERS;

        String PROFILE = BASE_PATH + "/profile";
        String SETTINGS = BASE_PATH + "/settings";
    }

    /**
     * Finance-related paths.
     */
    interface FinancePaths {
        String FINANCE = "/finance";
        String BASE_PATH = BasePaths.FULL_PATH + FINANCE;

        String INCOME = BASE_PATH + "/income";
        String EXPENSES = BASE_PATH + "/expenses";
        String INVESTMENTS = BASE_PATH + "/investments";

        /**
         * Analytics related paths under finance.
         */
        interface AnalyticsPaths {
            String BASE = BASE_PATH + "/analytics";
            String SUMMARY = BASE + "/summary";
            String TRENDS = BASE + "/trends";
        }
    }

    /**
     * Admin-related paths.
     */
    interface AdminPaths {
        String ADMIN = "/admin";
        String BASE_PATH = BasePaths.FULL_PATH + ADMIN;

        String USERS = BASE_PATH + "/users";
        String REPORTS = BASE_PATH + "/reports";
    }

    /**
     * Public endpoints that do not require authentication.
     */
    interface PublicEndpoints {
        String[] PATHS = {
                AuthPaths.LOGIN, // Login endpoint
                AuthPaths.REGISTER, // Register endpoint
                AuthPaths.CONFIRM, // Registration confirmation endpoint
                AuthPaths.PASSWORD_RESET, // Password reset endpoint
                "/swagger-ui/**", // Swagger UI paths
                "/v3/api-docs/**", // API documentation paths
                "/actuator/health" // Actuator health check path
        };
    }
}
