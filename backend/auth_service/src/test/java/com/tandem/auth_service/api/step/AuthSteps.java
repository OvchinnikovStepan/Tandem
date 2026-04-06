package com.tandem.auth_service.api.step;

import java.util.Set;

import com.tandem.auth_service.api.client.AuthClient;
import com.tandem.auth_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.auth_service.api.config.ApiConfig;
import com.tandem.auth_service.api.util.TestDataFactory;

import io.qameta.allure.Step;

public class AuthSteps {

    private final AuthClient authClient;
    private final RegistrationSteps registrationSteps;

    public AuthSteps(AuthClient authClient, RegistrationSteps registrationSteps) {
        this.authClient = authClient;
        this.registrationSteps = registrationSteps;
    }

    @Step("Login with email: {email}")
    public ApiResponse login(String email, String password) {
        return authClient.login(email, password);
    }

    @Step("Register new user and login (prefix: {prefix})")
    public ApiResponse registerAndLogin(String prefix) {
        String phone = TestDataFactory.randomPhone();
        String email = TestDataFactory.randomEmail(prefix);
        String password = ApiConfig.defaultPassword();

        ApiResponse regResult = registrationSteps.registerFull(phone, email, password);
        if (Set.of(200, 201).contains(regResult.statusCode())
                && !regResult.body().path("accessToken").asText("").isBlank()) {
            return regResult;
        }

        return authClient.login(email, password);
    }

    @Step("Refresh access token")
    public ApiResponse refresh(String refreshToken) {
        return authClient.refresh(refreshToken);
    }

    @Step("Get current user info")
    public ApiResponse me(String accessToken) {
        return authClient.me(accessToken);
    }

    @Step("Logout")
    public ApiResponse logout(String accessToken) {
        return authClient.logout(accessToken);
    }

    public boolean hasTokens(ApiResponse response) {
        return Set.of(200, 201).contains(response.statusCode())
                && !response.body().path("accessToken").asText("").isBlank()
                && !response.body().path("refreshToken").asText("").isBlank();
    }
}
