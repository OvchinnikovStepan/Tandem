package com.tandem.interest_service.api.step;

import com.tandem.interest_service.api.client.AuthClient;
import com.tandem.interest_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.interest_service.api.config.ApiConfig;
import com.tandem.interest_service.api.util.TestDataFactory;
import io.qameta.allure.Step;

/** Шаги получения JWT через auth_service. */
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

    /**
     * Полный сценарий регистрации нового пользователя в auth_service.
     * Возвращает access token (или null, если регистрация не завершилась).
     */
    @Step("Register new user and obtain access token (prefix: {prefix})")
    public AuthResult registerAndObtainToken(String prefix) {
        String phone = TestDataFactory.randomPhone();
        String email = TestDataFactory.randomEmail(prefix);
        String password = ApiConfig.defaultPassword();

        ApiResponse phoneResp = authClient.registerPhone(phone);
        String verificationId = phoneResp.body().path("verificationId").asText("");
        if (verificationId.isBlank()) {
            return AuthResult.empty();
        }

        authClient.verifyPhone(verificationId, ApiConfig.verificationCode());

        ApiResponse regEmail = authClient.registerEmail(verificationId, email, password);
        String token = regEmail.body().path("accessToken").asText("");
        String userId = regEmail.body().path("userId").asText("");

        if (token.isBlank()) {
            ApiResponse loginResp = authClient.login(email, password);
            token = loginResp.body().path("accessToken").asText("");
            if (userId.isBlank()) {
                userId = loginResp.body().path("userId").asText("");
            }
        }

        if (token.isBlank()) {
            return AuthResult.empty();
        }
        return new AuthResult(token, userId, email, phone);
    }

    /** Шорткат, когда нужен только токен. */
    @Step("Obtain access token (prefix: {prefix})")
    public String obtainAccessToken(String prefix) {
        AuthResult result = registerAndObtainToken(prefix);
        return result.accessToken();
    }

    public record AuthResult(String accessToken, String userId, String email, String phone) {
        public static AuthResult empty() {
            return new AuthResult(null, null, null, null);
        }
        public boolean isPresent() {
            return accessToken != null && !accessToken.isBlank();
        }
    }
}
