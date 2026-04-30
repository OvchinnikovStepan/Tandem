package com.tandem.profile_service.api.client;

import java.util.Map;

public class AuthClient {

    private final BaseApiClient base;

    public AuthClient(BaseApiClient base) {
        this.base = base;
    }

    public BaseApiClient.ApiResponse registerPhone(String phoneNumber) {
        return base.send("POST", "/register/phone", Map.of("phoneNumber", phoneNumber), null);
    }

    public BaseApiClient.ApiResponse verifyPhone(String verificationId, String code) {
        return base.send("POST", "/register/verify",
                Map.of("verificationId", verificationId, "code", code), null);
    }

    public BaseApiClient.ApiResponse registerEmail(String verificationId, String email, String password) {
        return base.send("POST", "/register/email",
                Map.of("verificationId", verificationId, "email", email, "password", password), null);
    }

    public BaseApiClient.ApiResponse login(String email, String password) {
        return base.send("POST", "/login", Map.of("email", email, "password", password), null);
    }
}
