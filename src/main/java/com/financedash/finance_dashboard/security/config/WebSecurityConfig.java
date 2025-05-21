package com.financedash.finance_dashboard.security.config;

import com.financedash.finance_dashboard.appUser.AppUserService;
import com.financedash.finance_dashboard.config.ApiEndpoints;
import com.financedash.finance_dashboard.security.jwt.JwtAuthFilter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(SecurityConfigProperties.class)
public class WebSecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);

    private final SecurityConfigProperties securityProperties;
    private final AppUserService appUserService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .xssProtection(HeadersConfigurer.XXssConfig::disable)
                        .contentSecurityPolicy(csp -> csp.policyDirectives(SecurityConstants.DEFAULT_CSP))
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, SecurityConstants.ALL_PATHS_PATTERN).permitAll()
                        .requestMatchers(SecurityConstants.PUBLIC_AUTH_PATHS).permitAll() // Ensure register and login are public
                        .requestMatchers(securityProperties.getPublicEndpoints().toArray(new String[0])).permitAll()
                        .requestMatchers(ApiEndpoints.PublicEndpoints.PATHS).permitAll()
                        .requestMatchers(ApiEndpoints.AdminPaths.BASE_PATH + "/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Check if this filter is blocking public endpoints
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService); // Set custom user service
        provider.setPasswordEncoder(passwordEncoder); // Use BCrypt password encoder
        provider.setHideUserNotFoundExceptions(true); // Hide user not found exceptions for security
        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(securityProperties.getAllowedOrigins()); // Set allowed origins
        config.setAllowedMethods(securityProperties.getAllowedMethods()); // Set allowed HTTP methods
        config.setAllowedHeaders(securityProperties.getAllowedHeaders()); // Set allowed headers
        config.setExposedHeaders(List.of(SecurityConstants.EXPOSED_HEADERS)); // Expose custom headers
        config.setAllowCredentials(true); // Allow credentials (cookies, etc.)
        config.setMaxAge(securityProperties.getMaxAgeSeconds()); // Set CORS max age

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(SecurityConstants.ALL_PATHS_PATTERN, config); // Apply CORS config to all paths
        return source;
    }

    @PostConstruct
    public void logPublicEndpoints() {
        log.info("🔓 Public endpoints from SecurityConfigProperties: {}", securityProperties.getPublicEndpoints());
        log.info("🔓 Public endpoints from ApiEndpoints: {}", String.join(", ", ApiEndpoints.PublicEndpoints.PATHS));
    }

    private static class SecurityConstants {
        static final String ALL_PATHS_PATTERN = "/**"; // Wildcard for all paths
        static final String DEFAULT_CSP = """
            default-src 'self'; 
            script-src 'self' 'unsafe-inline' 'unsafe-eval'; 
            style-src 'self' 'unsafe-inline'; 
            img-src 'self' data: https:;
            """.replaceAll("\n", " ").trim();
        static final String[] EXPOSED_HEADERS = {"Authorization", "Refresh-Token"}; // Exposed headers for CORS
        static final String[] PUBLIC_AUTH_PATHS = {"/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/auth/refresh", "/api/v1/auth/logout"}; // Public authentication paths
    }
}
