package com.tandem.auth_service.api.dto.password_reset.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(

        @NotBlank
        String identifier,

        @Pattern(regexp = "email|sms")
        String method,

        @Pattern(regexp = "ru|en")
        String locale
) {}