package com.tandem.auth_service.api.dto.password_strength.request;

import jakarta.validation.constraints.NotBlank;

public record CheckPasswordStrengthRequest(
        @NotBlank
        String password
) {}
