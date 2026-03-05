package com.tandem.auth_service.api.dto.response;

import java.util.UUID;

import com.tandem.auth_service.api.dto.password.PasswordStrength;

public record RegisterEmailResponse(
        UUID userId,
        String accessToken,
        String refreshToken,
        PasswordStrength passwordStrength
) {}
