package com.tandem.auth_service.api.client;

import java.util.Map;

public class PasswordClient {

    private final BaseApiClient base;

    public PasswordClient(BaseApiClient base) {
        this.base = base;
    }

    public BaseApiClient.ApiResponse checkStrength(String password) {
        return base.send("POST", "/password/check-strength", Map.of("password", password), null);
    }

    public BaseApiClient.ApiResponse resetRequest(String identifier, String method) {
        return base.send("POST", "/password/reset/request",
                Map.of("identifier", identifier, "method", method), null);
    }

    public BaseApiClient.ApiResponse resetVerify(String resetToken) {
        return base.send("POST", "/password/reset/verify", Map.of("resetToken", resetToken), null);
    }

    public BaseApiClient.ApiResponse resetComplete(String resetToken, String newPassword) {
        return base.send("POST", "/password/reset/complete",
                Map.of("resetToken", resetToken, "newPassword", newPassword), null);
    }
}
