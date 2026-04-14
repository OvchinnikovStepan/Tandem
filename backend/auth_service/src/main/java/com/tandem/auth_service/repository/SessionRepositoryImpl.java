package com.tandem.auth_service.repository;

import static com.tandem.jooq.tables.Sessions.SESSIONS;

import com.tandem.auth_service.model.Session;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SessionRepositoryImpl implements SessionRepository {

    private final DSLContext dsl;

    public SessionRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public void save(Session session) {
        dsl.insertInto(SESSIONS)
            .set(SESSIONS.ID, session.getId())
            .set(SESSIONS.USER_ID, session.getUserId())
            .set(SESSIONS.ACCESS_TOKEN_HASH, session.getAccessTokenHash())
            .set(SESSIONS.REFRESH_TOKEN_HASH, session.getRefreshTokenHash())
            .set(SESSIONS.DEVICE_INFO, session.getDeviceInfo())
            .set(SESSIONS.IP_ADDRESS, session.getIpAddress())
            .set(SESSIONS.CREATED_AT, session.getCreatedAt())
            .set(SESSIONS.EXPIRES_AT, session.getExpiresAt())
            .set(SESSIONS.REVOKED_AT, session.getRevokedAt())
            .execute();
    }

    @Override
    public Optional<Session> findByRefreshTokenHash(String refreshTokenHash) {
        return dsl.selectFrom(SESSIONS)
            .where(SESSIONS.REFRESH_TOKEN_HASH.eq(refreshTokenHash))
            .fetchOptional()
            .map(this::mapToSession);
    }

    @Override
    public List<Session> findActiveByUserId(UUID userId) {
        return dsl.selectFrom(SESSIONS)
            .where(SESSIONS.USER_ID.eq(userId))
            .and(SESSIONS.REVOKED_AT.isNull())
            .and(SESSIONS.EXPIRES_AT.gt(LocalDateTime.now()))
            .fetch(this::mapToSession);
    }

    @Override
    public void revoke(UUID sessionId, LocalDateTime revokedAt) {
        dsl.update(SESSIONS)
            .set(SESSIONS.REVOKED_AT, revokedAt)
            .where(SESSIONS.ID.eq(sessionId))
            .execute();
    }

    private Session mapToSession(org.jooq.Record record) {
        
        return Session.builder()
            .id(record.get(SESSIONS.ID))
            .userId(record.get(SESSIONS.USER_ID))
            .accessTokenHash(record.get(SESSIONS.ACCESS_TOKEN_HASH))
            .refreshTokenHash(record.get(SESSIONS.REFRESH_TOKEN_HASH))
            .deviceInfo(record.get(SESSIONS.DEVICE_INFO))
            .ipAddress(record.get(SESSIONS.IP_ADDRESS))
            .createdAt(record.get(SESSIONS.CREATED_AT))
            .expiresAt(record.get(SESSIONS.EXPIRES_AT))
            .revokedAt(record.get(SESSIONS.REVOKED_AT))
            .build();
    }

    @Override
    public void updateTokensHash(UUID sessionId, String accessTokenHash, String refreshTokenHash) {
        dsl.update(SESSIONS)
        .set(SESSIONS.ACCESS_TOKEN_HASH, accessTokenHash)
        .set(SESSIONS.REFRESH_TOKEN_HASH,refreshTokenHash)
        .where(SESSIONS.ID.eq(sessionId))
        .execute();
}

    @Override
    public Session findSessionById(UUID sessionId) {
        return dsl.selectFrom(SESSIONS)
            .where(SESSIONS.ID.eq(sessionId))
            .fetch(this::mapToSession)
            .getFirst();
    }

    @Override
    public int deleteExpired() {
        return dsl.deleteFrom(SESSIONS)
        .where(
            SESSIONS.EXPIRES_AT.lt(LocalDateTime.now())
            .or(SESSIONS.REVOKED_AT.isNotNull())
        )
        .execute();
    }

    @Override
    public int deleteUserSessions(UUID userId) {
        return dsl.deleteFrom(SESSIONS)
        .where(
            SESSIONS.USER_ID.eq(userId)
        )
        .execute();
    }
}
