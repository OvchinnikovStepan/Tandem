package com.tandem.auth_service.api.dto.register.request;
import jakarta.validation.constraints.NotBlank;

public record RegisterPhoneRequest(
        @NotBlank
        String phoneNumber
) {}
