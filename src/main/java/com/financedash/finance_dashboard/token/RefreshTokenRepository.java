package com.financedash.finance_dashboard.token;

import com.financedash.finance_dashboard.appUser.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(AppUser user);
    Optional<RefreshToken> findByUser(AppUser user);

}

