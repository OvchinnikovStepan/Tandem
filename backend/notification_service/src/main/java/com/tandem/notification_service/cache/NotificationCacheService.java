package com.tandem.notification_service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.notification_service.service.model.response.NotificationPreferencesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCacheService {

    private static final Duration UNREAD_TTL = Duration.ofMinutes(10);
    private static final Duration PREFERENCES_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public Optional<Long> getUnreadCount(UUID userId) {
        String value = redisTemplate.opsForValue().get(unreadKey(userId));
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(value));
        } catch (NumberFormatException e) {
            log.warn("Invalid unread count in cache userId={} value={}", userId, value);
            evictUnreadCount(userId);
            return Optional.empty();
        }
    }

    public void setUnreadCount(UUID userId, long count) {
        redisTemplate.opsForValue().set(unreadKey(userId), String.valueOf(Math.max(count, 0)), UNREAD_TTL);
    }

    public void incrementUnreadCount(UUID userId) {
        String key = unreadKey(userId);
        Long value = redisTemplate.opsForValue().increment(key);
        if (value != null && value == 1L) {
            redisTemplate.expire(key, UNREAD_TTL);
        }
    }

    public void evictUnreadCount(UUID userId) {
        redisTemplate.delete(unreadKey(userId));
    }

    public Optional<NotificationPreferencesResponse> getPreferences(UUID userId) {
        String value = redisTemplate.opsForValue().get(preferencesKey(userId));
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(value, NotificationPreferencesResponse.class));
        } catch (JsonProcessingException e) {
            log.warn("Invalid preferences in cache userId={}", userId, e);
            evictPreferences(userId);
            return Optional.empty();
        }
    }

    public void setPreferences(UUID userId, NotificationPreferencesResponse response) {
        try {
            String payload = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(preferencesKey(userId), payload, PREFERENCES_TTL);
        } catch (JsonProcessingException e) {
            log.error("Failed to cache preferences userId={}", userId, e);
        }
    }

    public void evictPreferences(UUID userId) {
        redisTemplate.delete(preferencesKey(userId));
    }

    private String unreadKey(UUID userId) {
        return "notifications:unread:" + userId;
    }

    private String preferencesKey(UUID userId) {
        return "notifications:preferences:" + userId;
    }
}
