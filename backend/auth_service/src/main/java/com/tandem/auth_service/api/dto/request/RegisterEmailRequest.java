package com.tandem.auth_service.api.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterEmailRequest(

        @NotNull
        UUID verificationId,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {}
