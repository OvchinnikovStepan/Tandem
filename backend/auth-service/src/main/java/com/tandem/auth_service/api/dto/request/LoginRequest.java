package com.tandem.auth_service.api.dto.request;

public record LoginRequest(
        String email,
        String password
) {}

