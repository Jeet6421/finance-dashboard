package com.financedash.finance_dashboard.service.auth;

import com.financedash.finance_dashboard.exception.AuthenticationException;
import com.financedash.finance_dashboard.appUser.AppUser;
import com.financedash.finance_dashboard.payload.AuthRequest;
import com.financedash.finance_dashboard.payload.AuthResponse;
import com.financedash.finance_dashboard.payload.RefreshTokenRequest;
import com.financedash.finance_dashboard.appUser.UserRepository;
import com.financedash.finance_dashboard.security.jwt.JwtUtil;
import com.financedash.finance_dashboard.token.RefreshTokenService;
import com.financedash.finance_dashboard.token.TokenRefreshException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    /**
     * Authenticates a user and generates access and refresh tokens
     *
     * @param request Login credentials
     * @return Authentication response containing tokens
     * @throws AuthenticationException if authentication fails
     */
    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("User not found"));

            String accessToken = jwtUtil.generateAccessToken(user.getEmail());
            String refreshToken = String.valueOf(refreshTokenService.createRefreshToken(user.getEmail()));

            return new AuthResponse(accessToken, refreshToken);
        } catch (Exception e) {
            throw new AuthenticationException("Invalid email or password");
        }
    }

    /**
     * Refreshes the access token using a valid refresh token
     *
     * @param request Refresh token request
     * @return New authentication response with fresh tokens
     * @throws AuthenticationException if the refresh token is invalid
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
            .map(refreshToken -> {
                try {
                    refreshTokenService.verifyExpiration(refreshToken);
                } catch (TokenRefreshException e) {
                    throw new RuntimeException(e);
                }
                String email = refreshToken.getUser().getEmail();
                String newAccessToken = jwtUtil.generateAccessToken(email);
                String newRefreshToken = null;
                try {
                    newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);
                } catch (TokenRefreshException e) {
                    throw new RuntimeException(e);
                }

                return new AuthResponse(newAccessToken, newRefreshToken);
            })
            .orElseThrow(() -> new AuthenticationException("Invalid refresh token"));
    }

    /**
     * Invalidates a refresh token, effectively logging out the user
     *
     * @param refreshToken Token to invalidate
     */
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.deleteByToken(refreshToken);
    }
}