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
        return new Session(
            record.get(SESSIONS.ID),
            record.get(SESSIONS.USER_ID),
            record.get(SESSIONS.ACCESS_TOKEN_HASH),
            record.get(SESSIONS.REFRESH_TOKEN_HASH),
            record.get(SESSIONS.DEVICE_INFO),
            record.get(SESSIONS.IP_ADDRESS),
            record.get(SESSIONS.CREATED_AT),
            record.get(SESSIONS.EXPIRES_AT),
            record.get(SESSIONS.REVOKED_AT)
        );
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
}
