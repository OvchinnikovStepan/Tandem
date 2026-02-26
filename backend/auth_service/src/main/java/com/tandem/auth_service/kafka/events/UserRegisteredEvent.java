package com.tandem.auth_service.kafka.events;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
        String eventType,
        UUID userId,
        Instant timestamp,
        Metadata metadata
) {
    public static UserRegisteredEvent of(UUID userId, String maskedEmail, String maskedPhone) {
        return new UserRegisteredEvent(
                "user.registered",
                userId,
                Instant.now(),
                new Metadata(maskedEmail, maskedPhone)
        );
    }

    public record Metadata(
            String email,
            String phoneNumber
    ) {}
}