package com.tandem.profile_service.api.step;

import com.tandem.profile_service.api.client.AuthClient;
import com.tandem.profile_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.profile_service.api.config.ApiConfig;
import com.tandem.profile_service.api.util.KafkaHelper;
import com.tandem.profile_service.api.util.TestDataFactory;

import io.qameta.allure.Step;

public class AuthSteps {

    private final AuthClient authClient;

    public AuthSteps(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Step("Register phone number: {phone}")
    public ApiResponse registerPhone(String phone) {
        return authClient.registerPhone(phone);
    }

    @Step("Verify phone with code: {code}")
    public ApiResponse verifyPhone(String verificationId, String code) {
        return authClient.verifyPhone(verificationId, code);
    }

    @Step("Register email: {email}")
    public ApiResponse registerEmail(String verificationId, String email, String password) {
        return authClient.registerEmail(verificationId, email, password);
    }

    @Step("Login with email: {email}")
    public ApiResponse login(String email, String password) {
        return authClient.login(email, password);
    }

    @Step("Full registration and login (prefix: {prefix})")
    public String obtainAccessToken(String prefix) {
        String phone = TestDataFactory.randomPhone();
        String email = TestDataFactory.randomEmail(prefix);
        String password = ApiConfig.defaultPassword();

        ApiResponse phoneResp = authClient.registerPhone(phone);
        String verificationId = phoneResp.body().path("verificationId").asText("");
        if (verificationId.isBlank()) return null;

        authClient.verifyPhone(verificationId, ApiConfig.verificationCode());

        ApiResponse regEmail = authClient.registerEmail(verificationId, email, password);
        String token = regEmail.body().path("accessToken").asText("");
        String userId = regEmail.body().path("userId").asText("");

        if (token.isBlank()) {
            ApiResponse login = authClient.login(email, password);
            token = login.body().path("accessToken").asText(null);
            userId = login.body().path("userId").asText("");
        }

        if (token != null && !userId.isBlank()) {
            KafkaHelper.publishUserRegistered(userId, phone, email);
            waitForProfileCreation(token);
        }

        return token;
    }

    private void waitForProfileCreation(String token) {
        var profileBase = new com.tandem.profile_service.api.client.BaseApiClient();
        for (int i = 0; i < 20; i++) {
            var resp = profileBase.send("GET", "/profile/me", null, token);
            if (resp.statusCode() == 200) return;
            try { Thread.sleep(250); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }
    }
}
