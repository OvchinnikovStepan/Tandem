package com.tandem.auth_service.api.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
