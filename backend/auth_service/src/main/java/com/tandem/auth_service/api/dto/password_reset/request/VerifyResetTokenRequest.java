package com.tandem.auth_service.api.dto.password_reset.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyResetTokenRequest(

        @NotBlank
        String resetToken

) {}