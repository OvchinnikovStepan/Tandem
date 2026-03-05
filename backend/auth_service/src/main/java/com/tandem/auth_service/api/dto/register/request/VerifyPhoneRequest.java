package com.tandem.auth_service.api.dto.register.request;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifyPhoneRequest(
        @NotNull UUID verificationId,

        @NotBlank
        String code
) {}
