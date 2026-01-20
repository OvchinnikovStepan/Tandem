package com.tandem.auth_service.service.registration;

import java.util.UUID;

import com.tandem.auth_service.api.dto.response.RegisterEmailResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface RegistrationService {

    UUID startPhoneRegistration(String phoneNumber);

    void verifyPhone(UUID verificationId, String code);

    RegisterEmailResponse completeRegistration(HttpServletRequest httpRequest,UUID userId, String email, String rawPassword);
}
