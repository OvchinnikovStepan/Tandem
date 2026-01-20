package com.tandem.auth_service.api.dto;

public record RefreshResultDto(
    String accessToken,
    String refreshToken
)
{}
