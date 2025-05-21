package com.financedash.finance_dashboard.security.jwt;

import com.financedash.finance_dashboard.appUser.AppUserService;
import com.financedash.finance_dashboard.exception.AuthenticationException;
import com.financedash.finance_dashboard.security.config.SecurityConfigProperties;
import com.financedash.finance_dashboard.token.RefreshTokenService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final AppUserService userService;
    private final RefreshTokenService refreshTokenService;
    private final SecurityConfigProperties securityProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        log.debug("Processing request path: {}", path);

        if (isPublicEndpoint(path)) {
            log.debug("Public endpoint detected, allowing access: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = extractJwtFromRequest(request);
        if (jwt == null) {
            log.debug("Missing JWT token, checking for public endpoint access.");
            handleMissingToken(response);
            return;
        }

        try {
            processJwtAuthentication(request, response, filterChain, jwt);
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            handleAuthenticationError(response, e);
        }
    }

    private boolean isPublicEndpoint(String path) {
        String contextPath = securityProperties.getContextPath(); // Use configured context path
        String normalizedPath = path.startsWith(contextPath) ? path : contextPath + path;

        boolean isPublic = securityProperties.getPublicEndpoints()
                .stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, normalizedPath));

        log.debug("Endpoint '{}': normalizedPath='{}', isPublic={}", path, normalizedPath, isPublic);
        return isPublic;
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void processJwtAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String jwt)
            throws ServletException, IOException {
        String email = jwtUtil.extractEmail(jwt);
        log.debug("Extracted email from JWT: {}", email);

        if (email != null && isAuthenticationRequired()) {
            log.debug("Authentication is required for user: {}", email);
            UserDetails userDetails = userService.getUserByEmail(email);

            log.debug("Fetched user details for email: {}", email);

            if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {
                setAuthentication(request, userDetails);
                log.debug("JWT token validated successfully for user: {}", email);
            } else {
                log.warn("Invalid or expired JWT token for user: {}", email);
                throw new AuthenticationException("Invalid or expired token.");
            }
        } else {
            log.debug("No JWT token found or token is not valid.");
        }
        filterChain.doFilter(request, response);
    }


    private boolean isAuthenticationRequired() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void handleMissingToken(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\": \"Missing authentication token\"}");
    }

    private void handleAuthenticationError(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(String.format("{\"error\": \"%s\"}", e.getMessage()));
    }

    @PostConstruct
    private void logPublicEndpoints() {
        log.debug("Configured public endpoints: {}", String.join(", ", securityProperties.getPublicEndpoints()));
    }
}
