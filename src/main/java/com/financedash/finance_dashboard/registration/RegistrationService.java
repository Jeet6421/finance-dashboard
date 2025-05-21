package com.financedash.finance_dashboard.registration;

import com.financedash.finance_dashboard.appUser.AppUser;
import com.financedash.finance_dashboard.appUser.AppUserRole;
import com.financedash.finance_dashboard.appUser.AppUserService;
import com.financedash.finance_dashboard.email.EmailSender;
import com.financedash.finance_dashboard.registration.token.ConfirmationToken;
import com.financedash.finance_dashboard.registration.token.ConfirmationTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;

    public String register(RegistrationRequest request) {
        String email = request.getEmail();

        if (!emailValidator.test(email)) {
            throw new IllegalArgumentException("Provided email is not valid");
        }

        Optional<AppUser> existingUserOpt = appUserService.findByEmail(email);

        if (existingUserOpt.isPresent()) {
            AppUser user = existingUserOpt.get();

            if (user.isEnabled()) {
                return "Email is already confirmed. Please login.";
            }

            String token = confirmationTokenService
                    .getTokenByUser(user)
                    .map(ConfirmationToken::getToken)
                    .orElseGet(() -> confirmationTokenService.createTokenForUser(user));

            sendConfirmationEmail(user.getFirstName(), email, token);
            return "User already registered but not confirmed. Confirmation email resent.";
        }

        AppUser newUser = new AppUser(
                request.getFirstName(),
                request.getLastName(),
                email,
                request.getPassword(),
                AppUserRole.USER
        );

        String token = appUserService.signUp(newUser);
        sendConfirmationEmail(request.getFirstName(), email, token);

        return "User registered successfully! Please check your email to confirm.";
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("Confirmation token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email is already confirmed");
        }

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token has expired. Please register again.");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(confirmationToken.getAppUser().getEmail());

        return "Email confirmed successfully!";
    }

    private void sendConfirmationEmail(String name, String email, String token) {
        String link = "http://localhost:8080/api/v1/auth/register/confirm?token=" + token;
        String emailContent = buildEmail(name, link);
        emailSender.send(email, emailContent);
    }

    private String buildEmail(String name, String link) {
        return String.format("""
                <div style="font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c">
                    <h2>Hi %s,</h2>
                    <p>Thank you for registering. Please click the link below to activate your account:</p>
                    <a href="%s">Activate Now</a>
                    <p>This link will expire in 15 minutes.</p>
                    <p>See you soon!</p>
                </div>
                """, escapeHtml(name), link);
    }

    private String escapeHtml(String input) {
        return input == null ? "" : input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
