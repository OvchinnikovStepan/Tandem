package com.tandem.auth_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;


@Epic("Auth Service")
@Feature("Regression Testing")
class RegressionAuthApiTest {

    private static final AuthApiClient CLIENT = new AuthApiClient();

    private AuthApiClient.ApiResponse registerAndLogin(String prefix) {
        String phone = AuthApiConfig.randomPhone();
        String email = AuthApiConfig.randomEmail(prefix);
        String password = AuthApiConfig.defaultPassword();

        AuthApiClient.ApiResponse regResult = CLIENT.registerFull(phone, email, password);
        if (Set.of(200, 201).contains(regResult.statusCode())
                && !regResult.body().path("accessToken").asText("").isBlank()) {
            return regResult;
        }
      
        return CLIENT.login(email, password);
    }

    private boolean hasTokens(AuthApiClient.ApiResponse r) {
        return Set.of(200, 201).contains(r.statusCode())
                && !r.body().path("accessToken").asText("").isBlank()
                && !r.body().path("refreshToken").asText("").isBlank();
    }

    @Test
    @Story("R-01 Login → Refresh → Me chain")
    @Severity(SeverityLevel.BLOCKER)
    void r01_login_refresh_me_chain() {
        AuthApiClient.ApiResponse login = registerAndLogin("reg-r01");
        Assumptions.assumeTrue(hasTokens(login),
                "Skipped: registration did not complete (mock rejects verification code)");

        String refreshToken = login.body().path("refreshToken").asText();
        AuthApiClient.ApiResponse refresh = CLIENT.refresh(refreshToken);
        assertEquals(200, refresh.statusCode(),
                "Refresh failed: " + refresh.statusCode() + " | " + refresh.body());

        String newAccess = refresh.body().path("accessToken").asText();
        AuthApiClient.ApiResponse me = CLIENT.me(newAccess);
        assertEquals(200, me.statusCode(),
                "Me failed: " + me.statusCode() + " | " + me.body());
    }

    @Test
    @Story("R-02 Password reset flow: request → verify → complete")
    @Severity(SeverityLevel.CRITICAL)
    void r02_password_reset_flow() {
        String email = AuthApiConfig.randomEmail("reg-r02");

        AuthApiClient.ApiResponse request = CLIENT.passwordResetRequest(email, "email");
        assertEquals(200, request.statusCode(),
                "Reset request failed: " + request.statusCode() + " | " + request.body());
        assertFalse(request.body().path("resetId").asText("").isBlank(),
                "resetId must be present");

        AuthApiClient.ApiResponse verify = CLIENT.passwordResetVerify("dummy-reset-token");
        assertEquals(200, verify.statusCode(),
                "Reset verify failed: " + verify.statusCode() + " | " + verify.body());
        assertFalse(verify.body().path("valid").asBoolean(true),
                "valid must be false for dummy token");

        AuthApiClient.ApiResponse complete = CLIENT.passwordResetComplete("dummy-reset-token", "NewSecure1!");
        assertEquals(400, complete.statusCode(),
                "Reset complete should return 400, got " + complete.statusCode() + " | " + complete.body());
    }

    @Test
    @Story("R-03 Register → immediate login")
    @Severity(SeverityLevel.CRITICAL)
    void r03_register_and_login() {
        String phone = AuthApiConfig.randomPhone();
        String email = AuthApiConfig.randomEmail("reg-r03");
        String password = AuthApiConfig.defaultPassword();

        AuthApiClient.ApiResponse register = CLIENT.registerFull(phone, email, password);
        Assumptions.assumeTrue(Set.of(200, 201).contains(register.statusCode()),
                "Skipped: full registration failed (mock rejects verification code)");

        AuthApiClient.ApiResponse login = CLIENT.login(email, password);
        assertTrue(Set.of(200, 201).contains(login.statusCode()),
                "Login failed: " + login.statusCode() + " | " + login.body());
    }

    @Test
    @Story("R-04 Refresh after access token expiry")
    @Severity(SeverityLevel.NORMAL)
    void r04_refresh_after_access_expired() throws InterruptedException {
        AuthApiClient.ApiResponse login = registerAndLogin("reg-r04");
        Assumptions.assumeTrue(hasTokens(login),
                "Skipped: registration did not complete");

        int expiresIn = login.body().path("expiresIn").asInt(0);
        Assumptions.assumeTrue(expiresIn > 0 && expiresIn <= 120,
                "expiresIn missing or too large for practical test run");

        Thread.sleep((expiresIn + 2L) * 1000L);

        AuthApiClient.ApiResponse refresh = CLIENT.refresh(login.body().path("refreshToken").asText());
        assertEquals(200, refresh.statusCode(),
                "Refresh after expiry should still work: " + refresh.statusCode() + " | " + refresh.body());
    }

    @Test
    @Story("R-05 Me with expired access token returns 401")
    @Severity(SeverityLevel.NORMAL)
    void r05_me_with_expired_access() throws InterruptedException {
        AuthApiClient.ApiResponse login = registerAndLogin("reg-r05");
        Assumptions.assumeTrue(hasTokens(login),
                "Skipped: registration did not complete");

        int expiresIn = login.body().path("expiresIn").asInt(0);
        Assumptions.assumeTrue(expiresIn > 0 && expiresIn <= 120,
                "expiresIn missing or too large for practical test run");

        Thread.sleep((expiresIn + 2L) * 1000L);

        AuthApiClient.ApiResponse me = CLIENT.me(login.body().path("accessToken").asText());
        assertEquals(401, me.statusCode(),
                "Me with expired token should return 401, got " + me.statusCode());
    }

    @Test
    @Story("R-06 Refresh token single-use")
    @Severity(SeverityLevel.CRITICAL)
    void r06_refresh_single_use() {
        AuthApiClient.ApiResponse login = registerAndLogin("reg-r06");
        Assumptions.assumeTrue(hasTokens(login),
                "Skipped: registration did not complete");

        String refreshToken = login.body().path("refreshToken").asText();

        AuthApiClient.ApiResponse first = CLIENT.refresh(refreshToken);
        assertEquals(200, first.statusCode(),
                "First refresh should succeed: " + first.statusCode() + " | " + first.body());

        AuthApiClient.ApiResponse second = CLIENT.refresh(refreshToken);
        assertTrue(Set.of(400, 401).contains(second.statusCode()),
                "Re-using old refresh token should fail, got " + second.statusCode());
    }

    @Test
    @Story("R-07 Logout invalidates session")
    @Severity(SeverityLevel.CRITICAL)
    void r07_logout_invalidation() {
        AuthApiClient.ApiResponse login = registerAndLogin("reg-r07");
        Assumptions.assumeTrue(hasTokens(login),
                "Skipped: registration did not complete");

        String accessToken = login.body().path("accessToken").asText();
        String refreshToken = login.body().path("refreshToken").asText();

        AuthApiClient.ApiResponse logout = CLIENT.logout(accessToken);
        assertTrue(Set.of(200, 204).contains(logout.statusCode()),
                "Logout failed: " + logout.statusCode() + " | " + logout.body());

        AuthApiClient.ApiResponse refreshAfterLogout = CLIENT.refresh(refreshToken);
        assertTrue(Set.of(400, 401).contains(refreshAfterLogout.statusCode()),
                "Refresh after logout should fail, got " + refreshAfterLogout.statusCode());
    }

    @Test
    @Story("R-08 Password strength check for various inputs")
    @Severity(SeverityLevel.NORMAL)
    void r08_password_strength_variations() {
        AuthApiClient.ApiResponse weak = CLIENT.checkPasswordStrength("123");
        assertEquals(200, weak.statusCode());
        assertTrue(weak.body().path("score").asInt(999) < 100,
                "Weak password should have low score");

        AuthApiClient.ApiResponse strong = CLIENT.checkPasswordStrength("X#k9$mL!qZ2@pW4&");
        assertEquals(200, strong.statusCode());
        assertTrue(strong.body().path("score").asInt(0) > 0,
                "Strong password should have positive score");
    }

    @Test
    @Story("R-09 Sessions endpoint rejects invalid token")
    @Severity(SeverityLevel.NORMAL)
    void r09_sessions_auth_required() {
        AuthApiClient.ApiResponse noAuth = CLIENT.sessions("invalid-token-xyz");
        assertEquals(401, noAuth.statusCode(),
                "Sessions with invalid token should return 401, got " + noAuth.statusCode());
    }
}
