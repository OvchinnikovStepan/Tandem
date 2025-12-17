package com.tandem.auth_service.api.dto.response;

import java.util.List;
import java.util.Map;

import com.tandem.auth_service.api.dto.PasswordStrength;

public record CheckPasswordStrengthResponse(
        PasswordStrength strength,
        int score,
        List<String> feedback,
        Map<String, Object> requirements
) {}
