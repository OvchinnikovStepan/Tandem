package com.tandem.auth_service.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String phoneNumber,
        boolean emailVerified,
        boolean phoneVerified,
        String status,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt
) {}
