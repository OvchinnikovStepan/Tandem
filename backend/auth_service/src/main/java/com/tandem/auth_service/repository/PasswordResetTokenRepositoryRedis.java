package com.tandem.auth_service.repository;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.tandem.auth_service.model.PasswordResetTokenData;

@Repository
public class PasswordResetTokenRepositoryRedis implements PasswordResetTokenRepository {

    private final RedisTemplate<String, Object> redis;

    public PasswordResetTokenRepositoryRedis(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    private String tokenKey(String hash) {
        return "password_reset:token:" + hash;
    }

    private String userRateKey(String userId) {
        return "password_reset:rate:user:" + userId;
    }

    private String ipRateKey(String ip) {
        return "password_reset:rate:ip:" + ip;
    }

    @SuppressWarnings("null")
    @Override
    public void save(String tokenHash, PasswordResetTokenData data, Duration ttl) {
        redis.opsForValue().set(tokenKey(tokenHash), data, ttl);
    }

    @Override
    public Optional<PasswordResetTokenData> find(String tokenHash) {
        @SuppressWarnings("null")
        Object value = redis.opsForValue().get(tokenKey(tokenHash));

        if (value == null)
            return Optional.empty();

        return Optional.of((PasswordResetTokenData) value);
    }

    @SuppressWarnings("null")
    @Override
    public void delete(String tokenHash) {
        redis.delete(tokenKey(tokenHash));
    }

    @SuppressWarnings("null")
    @Override
    public long incrementUserRate(String userId, Duration ttl) {

        String key = userRateKey(userId);

        @SuppressWarnings("null")
        Long value = redis.opsForValue().increment(key);

        if (value != null && value == 1) {
            redis.expire(key, ttl);
        }

        return value == null ? 0 : value;
    }

    @SuppressWarnings("null")
    @Override
    public long incrementIpRate(String ip, Duration ttl) {

        String key = ipRateKey(ip);

        @SuppressWarnings("null")
        Long value = redis.opsForValue().increment(key);

        if (value != null && value == 1) {
            redis.expire(key, ttl);
        }

        return value == null ? 0 : value;
    }
}