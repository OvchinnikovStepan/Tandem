package com.tandem.auth_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.tandem.auth_service.model.Session;

public interface SessionRepository {

    void save(Session session);

    Optional<Session> findByRefreshTokenHash(String hash);

    List<Session> findActiveByUserId(UUID userId);

    void revoke(UUID sessionId, LocalDateTime revokedAt);

    Session findSessionById(UUID sessionId);

    void updateTokensHash(UUID sessionId, String accessTokenHash, String refreshTokenHash);

    int deleteExpired();

    int deleteUserSessions(UUID userId);
}
