package com.tandem.auth_service.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionDto(
        UUID id,
        String deviceInfo,
        String ipAddress,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {}
