package com.tandem.auth_service.api.dto.response;

import com.tandem.auth_service.api.dto.PasswordStrength;

public record RegisterEmailResponse(
        String userId,
        String accessToken,
        String refreshToken,
        PasswordStrength passwordStrength
) {}
