package com.tandem.auth_service.api.dto.password_reset.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CompleteResetPasswordRequest(

        @NotBlank
        String resetToken,

        @NotBlank
        String newPassword,

        @Pattern(regexp = "ru|en")
        String locale
) {}