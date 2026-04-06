package com.tandem.auth_service.api.step;

import com.tandem.auth_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.auth_service.api.client.PasswordClient;

import io.qameta.allure.Step;

public class PasswordSteps {

    private final PasswordClient passwordClient;

    public PasswordSteps(PasswordClient passwordClient) {
        this.passwordClient = passwordClient;
    }

    @Step("Check password strength")
    public ApiResponse checkStrength(String password) {
        return passwordClient.checkStrength(password);
    }

    @Step("Request password reset for: {identifier}")
    public ApiResponse requestReset(String identifier, String method) {
        return passwordClient.resetRequest(identifier, method);
    }

    @Step("Verify password reset token")
    public ApiResponse verifyReset(String resetToken) {
        return passwordClient.resetVerify(resetToken);
    }

    @Step("Complete password reset")
    public ApiResponse completeReset(String resetToken, String newPassword) {
        return passwordClient.resetComplete(resetToken, newPassword);
    }
}
