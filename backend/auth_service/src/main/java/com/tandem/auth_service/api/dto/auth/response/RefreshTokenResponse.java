package com.tandem.auth_service.api.dto.auth.response;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}

