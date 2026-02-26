package com.tandem.auth_service.kafka.events;

import java.time.Instant;
import java.util.UUID;

public record UserLoggedInEvent(
        String eventType,
        UUID userId,
        Instant timestamp,
        Metadata metadata
) {
    public static UserLoggedInEvent of(UUID userId, String ipAddress, String deviceInfo) {
        return new UserLoggedInEvent(
                "user.logged_in",
                userId,
                Instant.now(),
                new Metadata(ipAddress, deviceInfo)
        );
    }

    public record Metadata(
            String ipAddress,
            String deviceInfo
    ) {}
}