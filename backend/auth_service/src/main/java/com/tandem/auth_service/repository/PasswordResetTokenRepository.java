package com.tandem.auth_service.repository;

import java.time.Duration;
import java.util.Optional;

import com.tandem.auth_service.model.PasswordResetTokenData;

public interface PasswordResetTokenRepository {

    void save(String tokenHash, PasswordResetTokenData data, Duration ttl);

    Optional<PasswordResetTokenData> find(String tokenHash);

    void delete(String tokenHash);

    long incrementUserRate(String userId, Duration ttl);

    long incrementIpRate(String ip, Duration ttl);
}