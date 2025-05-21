package com.financedash.finance_dashboard.appUser;

import com.financedash.finance_dashboard.email.EmailSender;
import com.financedash.finance_dashboard.registration.token.ConfirmationToken;
import com.financedash.finance_dashboard.registration.token.ConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MSG = "User with email %s not found";
    private static final Logger logger = LoggerFactory.getLogger(AppUserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;



    // ✅ This method is used by Spring Security to authenticate the user by email (email = username)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User with email {} not found", email);
                    return new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email));
                });
    }

    public Optional<AppUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public AppUser getUserByEmail(String email) {
        logger.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User with email {} not found", email);
                    return new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email));
                });
    }

    public String signUp(AppUser appUser) {
        final String email = appUser.getEmail();

        // Validate email format
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }

        // Check if a user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        // Password validation
        if (appUser.getPassword() == null || appUser.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        // Encrypt password and save the user
        appUser.setPassword(bCryptPasswordEncoder.encode(appUser.getPassword()));
        userRepository.save(appUser);
        logger.info("User registered: {}", email);

        // Generate and save a confirmation token
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        logger.info("Confirmation token generated for: {}", email);

        return token;
    }

    public int enableAppUser(String email) {
        return userRepository.enableAppUser(email);
    }
}
