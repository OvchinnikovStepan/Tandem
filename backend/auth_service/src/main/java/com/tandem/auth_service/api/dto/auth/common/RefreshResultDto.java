package com.tandem.auth_service.api.dto.auth.common;

public record RefreshResultDto(
    String accessToken,
    String refreshToken
)
{}
