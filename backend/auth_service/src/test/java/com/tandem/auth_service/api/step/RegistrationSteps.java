package com.tandem.auth_service.api.step;

import com.tandem.auth_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.auth_service.api.client.RegistrationClient;
import com.tandem.auth_service.api.config.ApiConfig;

import io.qameta.allure.Step;

public class RegistrationSteps {

    private final RegistrationClient registrationClient;

    public RegistrationSteps(RegistrationClient registrationClient) {
        this.registrationClient = registrationClient;
    }

    @Step("Register phone number: {phone}")
    public ApiResponse registerPhone(String phone) {
        return registrationClient.registerPhone(phone);
    }

    @Step("Verify phone with code: {code}")
    public ApiResponse verifyPhone(String verificationId, String code) {
        return registrationClient.verifyPhone(verificationId, code);
    }

    @Step("Complete registration with email: {email}")
    public ApiResponse registerEmail(String verificationId, String email, String password) {
        return registrationClient.registerEmail(verificationId, email, password);
    }

    @Step("Full registration: phone → verify → email")
    public ApiResponse registerFull(String phone, String email, String password) {
        ApiResponse phoneResp = registrationClient.registerPhone(phone);
        String verificationId = phoneResp.body().path("verificationId").asText("");
        if (verificationId.isBlank()) return phoneResp;

        ApiResponse verifyResp = registrationClient.verifyPhone(verificationId, ApiConfig.verificationCode());
        if (verifyResp.statusCode() != 200) return verifyResp;

        return registrationClient.registerEmail(verificationId, email, password);
    }
}
