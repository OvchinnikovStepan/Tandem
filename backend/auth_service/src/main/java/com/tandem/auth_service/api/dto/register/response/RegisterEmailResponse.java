package com.tandem.auth_service.api.dto.register.response;

import java.util.UUID;

import com.tandem.auth_service.api.dto.password_strength.common.PasswordStrength;

public record RegisterEmailResponse(
        UUID userId,
        String accessToken,
        String refreshToken,
        PasswordStrength passwordStrength
) {}
