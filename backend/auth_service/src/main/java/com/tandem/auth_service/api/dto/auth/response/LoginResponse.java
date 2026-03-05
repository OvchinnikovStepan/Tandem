package com.tandem.auth_service.api.dto.auth.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
