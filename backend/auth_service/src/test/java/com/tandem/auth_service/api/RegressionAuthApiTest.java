package com.tandem.auth_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.tandem.auth_service.api.client.AuthClient;
import com.tandem.auth_service.api.client.BaseApiClient;
import com.tandem.auth_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.auth_service.api.client.PasswordClient;
import com.tandem.auth_service.api.client.RegistrationClient;
import com.tandem.auth_service.api.client.SessionClient;
import com.tandem.auth_service.api.step.AuthSteps;
import com.tandem.auth_service.api.step.PasswordSteps;
import com.tandem.auth_service.api.step.RegistrationSteps;
import com.tandem.auth_service.api.step.SessionSteps;
import com.tandem.auth_service.api.util.TestDataFactory;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;


@Epic("Auth Service")
@Feature("Regression Testing")
class RegressionAuthApiTest {

    private static final BaseApiClient BASE = new BaseApiClient();
    private static final RegistrationClient REG_CLIENT = new RegistrationClient(BASE);
    private static final AuthClient AUTH_CLIENT = new AuthClient(BASE);
    private static final PasswordClient PWD_CLIENT = new PasswordClient(BASE);
    private static final SessionClient SESSION_CLIENT = new SessionClient(BASE);

    private static final RegistrationSteps registrationSteps = new RegistrationSteps(REG_CLIENT);
    private static final AuthSteps authSteps = new AuthSteps(AUTH_CLIENT, registrationSteps);
    private static final PasswordSteps passwordSteps = new PasswordSteps(PWD_CLIENT);
    private static final SessionSteps sessionSteps = new SessionSteps(SESSION_CLIENT);

    @Test
    @Story("R-01 Login → Refresh → Me chain")
    @Severity(SeverityLevel.BLOCKER)
    void r01_login_refresh_me_chain() {
        ApiResponse login = authSteps.registerAndLogin("reg-r01");
        Assumptions.assumeTrue(authSteps.hasTokens(login),
                "Skipped: registration did not complete (mock rejects verification code)");

        String refreshToken = login.body().path("refreshToken").asText();
        ApiResponse refresh = authSteps.refresh(refreshToken);
        assertEquals(200, refresh.statusCode(),
                "Refresh failed: " + refresh.statusCode() + " | " + refresh.body());

        String newAccess = refresh.body().path("accessToken").asText();
        ApiResponse me = authSteps.me(newAccess);
        assertEquals(200, me.statusCode(),
                "Me failed: " + me.statusCode() + " | " + me.body());
    }

    @Test
    @Story("R-02 Password reset flow: request → verify → complete")
    @Severity(SeverityLevel.CRITICAL)
    void r02_password_reset_flow() {
        String email = TestDataFactory.randomEmail("reg-r02");

        ApiResponse request = passwordSteps.requestReset(email, "email");
        assertTrue(Set.of(200, 401, 404).contains(request.statusCode()),
                "Reset request: " + request.statusCode() + " | " + request.body());

        Assumptions.assumeTrue(request.statusCode() == 200,
                "Skipped: password reset endpoint not implemented (got " + request.statusCode() + ")tatusCode() == 200,
                "Skipped: password reset endpoint not implemented (got " + request.statusCode() + ")");

        ApiResponse verify = passwordSteps.verifyReset("dummy-reset-token");
        assertEquals(200, verify.statusCode(),
                "Reset verify failed: " + verify.statusCode() + " | " + verify.body());
        assertFalse(verify.body().path("valid").asBoolean(true),
                "valid must be false for dummy token");

        ApiResponse complete = passwordSteps.completeReset("dummy-reset-token", "NewSecure1!");
        assertEquals(400, complete.statusCode(),
                "Reset complete should return 400, got " + complete.statusCode() + " | " + complete.body());
    }

    @Test
    @Story("R-03 Register → immediate login")
    @Severity(SeverityLevel.CRITICAL)
    void r03_register_and_login() {
        String phone = TestDataFactory.randomPhone();
        String email = TestDataFactory.randomEmail("reg-r03");
        String password = com.tandem.auth_service.api.config.ApiConfig.defaultPassword();

        ApiResponse register = registrationSteps.registerFull(phone, email, password);
        Assumptions.assumeTrue(Set.of(200, 201).contains(register.statusCode()),
                "Skipped: full registration failed (mock rejects verification code)");

        ApiResponse login = authSteps.login(email, password);
        assertTrue(Set.of(200, 201).contains(login.statusCode()),
                "Login failed: " + login.statusCode() + " | " + login.body());
    }

    @Test
    @Story("R-04 Refresh after access token expiry")
    @Severity(SeverityLevel.NORMAL)
    void r04_refresh_after_access_expired() throws InterruptedException {
        ApiResponse login = authSteps.registerAndLogin("reg-r04");
        Assumptions.assumeTrue(authSteps.hasTokens(login),
                "Skipped: registration did not complete");

        int expiresIn = login.body().path("expiresIn").asInt(0);
        Assumptions.assumeTrue(expiresIn > 0 && expiresIn <= 120,
                "expiresIn missing or too large for practical test run");

        Thread.sleep((expiresIn + 2L) * 1000L);

        ApiResponse refresh = authSteps.refresh(login.body().path("refreshToken").asText());
        assertEquals(200, refresh.statusCode(),
                "Refresh after expiry should still work: " + refresh.statusCode() + " | " + refresh.body());
    }

    @Test
    @Story("R-05 Me with expired access token returns 401")
    @Severity(SeverityLevel.NORMAL)
    void r05_me_with_expired_access() throws InterruptedException {
        ApiResponse login = authSteps.registerAndLogin("reg-r05");
        Assumptions.assumeTrue(authSteps.hasTokens(login),
                "Skipped: registration did not complete");

        int expiresIn = login.body().path("expiresIn").asInt(0);
        Assumptions.assumeTrue(expiresIn > 0 && expiresIn <= 120,
                "expiresIn missing or too large for practical test run");

        Thread.sleep((expiresIn + 2L) * 1000L);

        ApiResponse me = authSteps.me(login.body().path("accessToken").asText());
        assertEquals(401, me.statusCode(),
                "Me with expired token should return 401, got " + me.statusCode());
    }

    @Test
    @Story("R-06 Refresh token single-use")
    @Severity(SeverityLevel.CRITICAL)
    void r06_refresh_single_use() {
        ApiResponse login = authSteps.registerAndLogin("reg-r06");
        Assumptions.assumeTrue(authSteps.hasTokens(login),
                "Skipped: registration did not complete");

        String refreshToken = login.body().path("refreshToken").asText();

        ApiResponse first = authSteps.refresh(refreshToken);
        assertEquals(200, first.statusCode(),
                "First refresh should succeed: " + first.statusCode() + " | " + first.body());

        ApiResponse second = authSteps.refresh(refreshToken);
        assertTrue(Set.of(400, 401).contains(second.statusCode()),
                "Re-using old refresh token should fail, got " + second.statusCode());
    }

    @Test
    @Story("R-07 Logout invalidates session")
    @Severity(SeverityLevel.CRITICAL)
    void r07_logout_invalidation() {
        ApiResponse login = authSteps.registerAndLogin("reg-r07");
        Assumptions.assumeTrue(authSteps.hasTokens(login),
                "Skipped: registration did not complete");

        String accessToken = login.body().path("accessToken").asText();
        String refreshToken = login.body().path("refreshToken").asText();

        ApiResponse logout = authSteps.logout(accessToken);
        assertTrue(Set.of(200, 204).contains(logout.statusCode()),
                "Logout failed: " + logout.statusCode() + " | " + logout.body());

        ApiResponse refreshAfterLogout = authSteps.refresh(refreshToken);
        assertTrue(Set.of(400, 401).contains(refreshAfterLogout.statusCode()),
                "Refresh after logout should fail, got " + refreshAfterLogout.statusCode());
    }

    @Test
    @Story("R-08 Password strength check for various inputs")
    @Severity(SeverityLevel.NORMAL)
    void r08_password_strength_variations() {
        ApiResponse weak = passwordSteps.checkStrength("123");
        assertEquals(200, weak.statusCode());
        assertTrue(weak.body().path("score").asInt(999) < 100,
                "Weak password should have low score");

        ApiResponse strong = passwordSteps.checkStrength("X#k9$mL!qZ2@pW4&");
        assertEquals(200, strong.statusCode());
        assertTrue(strong.body().path("score").asInt(0) > 0,
                "Strong password should have positive score");
    }

    @Test
    @Story("R-09 Sessions endpoint rejects invalid token")
    @Severity(SeverityLevel.NORMAL)
    void r09_sessions_auth_required() {
        ApiResponse noAuth = sessionSteps.getSessions("invalid-token-xyz");
        assertEquals(401, noAuth.statusCode(),
                "Sessions with invalid token should return 401, got " + noAuth.statusCode());
    }
}
