package com.tandem.auth_service.api.client;

import java.util.Map;

public class AuthClient {

    private final BaseApiClient base;

    public AuthClient(BaseApiClient base) {
        this.base = base;
    }

    public BaseApiClient.ApiResponse login(String email, String password) {
        return base.send("POST", "/login", Map.of("email", email, "password", password), null);
    }

    public BaseApiClient.ApiResponse refresh(String refreshToken) {
        return base.send("POST", "/refresh", Map.of("refreshToken", refreshToken), null);
    }

    public BaseApiClient.ApiResponse me(String accessToken) {
        return base.send("GET", "/me", null, accessToken);
    }

    public BaseApiClient.ApiResponse logout(String accessToken) {
        return base.send("POST", "/logout", null, accessToken);
    }
}
