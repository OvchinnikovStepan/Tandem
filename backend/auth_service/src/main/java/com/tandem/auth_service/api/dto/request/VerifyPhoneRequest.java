package com.tandem.auth_service.api.dto.request;
import jakarta.validation.constraints.NotBlank;

public record VerifyPhoneRequest(
        @NotBlank
        String verificationId,

        @NotBlank
        String code
) {}
