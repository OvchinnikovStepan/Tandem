package com.tandem.auth_service.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterEmailRequest(

        @NotBlank
        String verificationId,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {}
