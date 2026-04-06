package com.tandem.auth_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.tandem.auth_service.api.client.AuthClient;
import com.tandem.auth_service.api.client.BaseApiClient;
import com.tandem.auth_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.auth_service.api.client.PasswordClient;
import com.tandem.auth_service.api.client.RegistrationClient;
import com.tandem.auth_service.api.client.SessionClient;
import com.tandem.auth_service.api.config.ApiConfig;
import com.tandem.auth_service.api.step.AuthSteps;
import com.tandem.auth_service.api.step.PasswordSteps;
import com.tandem.auth_service.api.step.RegistrationSteps;
import com.tandem.auth_service.api.step.SessionSteps;
import com.tandem.auth_service.api.util.TestDataFactory;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@Epic("Auth Service")
@Feature("Functional Testing")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FunctionalAuthApiTest {

    private static final BaseApiClient BASE = new BaseApiClient();
    private static final RegistrationClient REG_CLIENT = new RegistrationClient(BASE);
    private static final AuthClient AUTH_CLIENT = new AuthClient(BASE);
    private static final PasswordClient PWD_CLIENT = new PasswordClient(BASE);
    private static final SessionClient SESSION_CLIENT = new SessionClient(BASE);

    private static final RegistrationSteps registrationSteps = new RegistrationSteps(REG_CLIENT);
    private static final AuthSteps authSteps = new AuthSteps(AUTH_CLIENT, registrationSteps);
    private static final PasswordSteps passwordSteps = new PasswordSteps(PWD_CLIENT);
    private static final SessionSteps sessionSteps = new SessionSteps(SESSION_CLIENT);

    private static String verificationId;
    private static boolean phoneVerified = false;
    private static String accessToken;
    private static String refreshToken;

    private static final String MAIN_PHONE = TestDataFactory.randomPhone();
    private static final String MAIN_EMAIL = TestDataFactory.randomEmail("functional-main");
    private static final String MAIN_PASSWORD = ApiConfig.defaultPassword();

    @Test
    @Order(1)
    @Story("F-01 Register phone — start registration")
    @Severity(SeverityLevel.BLOCKER)
    void f01_register_phone() {
        ApiResponse response = registrationSteps.registerPhone(MAIN_PHONE);

        Allure.step("Validate 200 and verificationId returned");
        assertEquals(200, response.statusCode(),
                "POST /register/phone → " + response.statusCode() + " | " + response.body());
        assertFalse(response.body().path("verificationId").asText("").isBlank(),
                "verificationId must not be blank");

        verificationId = response.body().path("verificationId").asText();
    }

    @Test
    @Order(2)
    @Story("F-02 Verify phone — mock returns INVALID_CODE or 200")
    @Severity(SeverityLevel.BLOCKER)
    void f02_verify_phone() {
        assertNotNull(verificationId, "verificationId must be set from F-01");

        ApiResponse response = registrationSteps.verifyPhone(
                verificationId, ApiConfig.verificationCode());

        Allure.step("Accept either 200 (verified) or 400 INVALID_CODE from mock");
        assertTrue(Set.of(200, 400).contains(response.statusCode()),
                "Expected 200 or 400, got " + response.statusCode() + " | " + response.body());

        if (response.statusCode() == 200 && response.body().path("verified").asBoolean(false)) {
            phoneVerified = true;
        }
    }

    @Test
    @Order(3)
    @Story("F-03 Register email — needs verified phone")
    @Severity(SeverityLevel.BLOCKER)
    void f03_register_email() {
        assertNotNull(verificationId, "verificationId must be set from F-01");

        ApiResponse response = registrationSteps.registerEmail(
                verificationId, MAIN_EMAIL, MAIN_PASSWORD);

        if (phoneVerified) {
            assertTrue(Set.of(200, 201).contains(response.statusCode()),
                    "Expected 200/201, got " + response.statusCode() + " | " + response.body());
            accessToken = response.body().path("accessToken").asText();
            refreshToken = response.body().path("refreshToken").asText();
        } else {
            assertEquals(400, response.statusCode(),
                    "Without verification, expected 400, got " + response.statusCode() + " | " + response.body());
            assertEquals("INVALID_VERIFICATION",
                    response.body().path("error").asText(),
                    "Error code must be INVALID_VERIFICATION");
        }
    }

    @Test
    @Order(4)
    @Story("F-04 Login (mock returns 401 INVALID_CREDENTIALS for unknown user)")
    @Severity(SeverityLevel.BLOCKER)
    void f04_login() {
        ApiResponse response = authSteps.login(MAIN_EMAIL, MAIN_PASSWORD);

        if (phoneVerified) {
            assertTrue(Set.of(200, 201).contains(response.statusCode()),
                    "Expected 200/201, got " + response.statusCode() + " | " + response.body());
            accessToken = response.body().path("accessToken").asText();
            refreshToken = response.body().path("refreshToken").asText();
        } else {
            assertTrue(Set.of(400, 401).contains(response.statusCode()),
                    "Expected 400/401, got " + response.statusCode() + " | " + response.body());
        }
    }

    @Test
    @Order(5)
    @Story("F-05 Login with wrong password → 401")
    @Severity(SeverityLevel.CRITICAL)
    void f05_wrong_password_login() {
        ApiResponse response = authSteps.login(MAIN_EMAIL, "WrongPass999");

        assertTrue(Set.of(400, 401, 403).contains(response.statusCode()),
                "Expected 4xx, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(6)
    @Story("F-06 Login with non-existent email → 401")
    @Severity(SeverityLevel.CRITICAL)
    void f06_unknown_email_login() {
        ApiResponse response = authSteps.login(
                TestDataFactory.randomEmail("functional-missing"), MAIN_PASSWORD);

        assertTrue(Set.of(400, 401, 403).contains(response.statusCode()),
                "Expected 4xx, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(7)
    @Story("F-07 /me with access token")
    @Severity(SeverityLevel.BLOCKER)
    void f07_me_after_login() {
        Assumptions.assumeTrue(accessToken != null && !accessToken.isBlank(),
                "Skipped: no valid accessToken (registration did not complete)");

        ApiResponse response = authSteps.me(accessToken);
        assertEquals(200, response.statusCode(),
                "Expected 200, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(8)
    @Story("F-08 /me with invalid token → 401")
    @Severity(SeverityLevel.CRITICAL)
    void f08_me_invalid_token() {
        ApiResponse response = authSteps.me("invalid-token-abc");

        assertEquals(401, response.statusCode(),
                "Expected 401, got " + response.statusCode() + " | " + response.body());
    
    @Test
    @Order(9)
    @Story("F-09 Refresh token rotation")
    @Severity(SeverityLevel.BLOCKER)
    void f09_refresh_rotation() {
        Assumptions.assumeTrue(refreshToken != null && !refreshToken.isBlank(),
                "Skipped: no valid refreshToken (registration did not complete)");

        ApiResponse firstRefresh = authSteps.refresh(refreshToken);
        assertEquals(200, firstRefresh.statusCode(),
                "Expected 200, got " + firstRefresh.statusCode() + " | " + firstRefresh.body());

        ApiResponse reuse = authSteps.refresh(refreshToken);
        assertTrue(Set.of(400, 401).contains(reuse.statusCode()),
                "Re-using old refresh token should fail, got " + reuse.statusCode());

        accessToken = firstRefresh.body().path("accessToken").asText();
        refreshToken = firstRefresh.body().path("refreshToken").asText();
    }

    @Test
    @Order(10)
    @Story("F-10 Refresh with invalid token → 401")
    @Severity(SeverityLevel.CRITICAL)
    void f10_refresh_invalid_token() {
        ApiResponse response = authSteps.refresh("definitely-not-a-valid-token");

        assertTrue(Set.of(400, 401).contains(response.statusCode()),
                "Expected 400/401, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(11)
    @Story("F-11 Check password strength")
    @Severity(SeverityLevel.NORMAL)
    void f11_check_password_strength() {
        ApiResponse response = passwordSteps.checkStrength("Qwerty123");

        assertEquals(200, response.statusCode(),
                "Expected 200, got " + response.statusCode() + " | " + response.body());
        assertFalse(response.body().path("strength").asText("").isBlank(),
                "strength must be present");
        assertTrue(response.body().path("score").asInt(-1) >= 0,
                "score must be >= 0");
        assertTrue(response.body().has("requirements"),
                "requirements must be present");
    }

    @Test
    @Order(12)
    @Story("F-12 Password reset request (not implemented \u2192 401)")
    @Story("F-12 Password reset request (not implemented \u2192 401)")
    @Severity(SeverityLevel.CRITICAL)
    void f12_password_reset_request() {
        ApiResponse response = passwordSteps.requestReset(MAIN_EMAIL, "email");

        assertTrue(Set.of(200, 401, 404).contains(response.statusCode()),
                "Expected 200/401/404, got " + response.statusCode() + " | " + response.body());d() {
        ApiResponse response = passwordSteps.verifyReset("invalid-token");

        assertTrue(Set.of(200, 401, 404).contains(response.statusCode()),
                "Expected 200/401/404, got " + response.statusCode() + " | " + response.body());
    @Story("F-13 Password reset verify with invalid token (not implemented \u2192 401)")
    @Severity(SeverityLevel.NORMAL)
    void f13_password_reset_verify_invalid() {
        ApiResponse response = passwordSteps.verifyReset("invalid-token");

        assertTrue(Set.of(200, 401, 404).contains(response.statusCode()),
                "Expected 200/401/404, got " + response.statusCode() + " | " + response.body());
        assertTrue(Set.of(400, 401, 404).contains(response.statusCode()),
                "Expected 400/401/404, got " + response.statusCode() + " | " + response.body());
    }

    @Story("F-14 Password reset complete with invalid token (not implemented \u2192 401)")
    @Severity(SeverityLevel.NORMAL)
    void f14_password_reset_complete_invalid() {
        ApiResponse response = passwordSteps.completeReset("invalid-token", "NewPass123!");

        assertTrue(Set.of(400, 401, 404).contains(response.statusCode()),
                "Expected 400/401/404, got " + response.statusCode() + " | " + response.body());
        assertTrue(Set.of(200, 400, 422, 500).contains(response.statusCode()),
                "Expected 200/400/422/500, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(16)
    @Story("F-16 Register verify with invalid verificationId → 400")
    @Severity(SeverityLevel.NORMAL)
    void f16_verify_invalid_verification_id() {
        ApiResponse response = registrationSteps.verifyPhone(
                "nonexistent-id", ApiConfig.verificationCode());

        assertTrue(Set.of(400, 401, 404).contains(response.statusCode()),
                "Expected 400/401/404, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(17)
    @Story("F-17 Register email without phone verification → 400")
    @Severity(SeverityLevel.NORMAL)
    void f17_register_email_without_verification() {
        String phone = TestDataFa1, 404).contains(response.statusCode()),
                "Expected 400/401 = registrationSteps.registerPhone(phone);
        String vid = phoneResp.body().path("verificationId").asText("");

        ApiResponse response = registrationSteps.registerEmail(
                vid, TestDataFactory.randomEmail("f17"), MAIN_PASSWORD);

        assertEquals(400, response.statusCode(),
                "Expected 400 (phone not verified), got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(18)
    @Story("F-18 Logout with valid token")
    @Severity(SeverityLevel.CRITICAL)
    void f18_logout() {
        Assumptions.assumeTrue(accessToken != null && !accessToken.isBlank(),
                "Skipped: no valid accessToken (registration did not complete)");

        ApiResponse response = authSteps.logout(accessToken);
        assertTrue(Set.of(200, 204).contains(response.statusCode()),
                "Expected 200/204, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(19)
    @Story("F-19 Logout with fake token → 401/500")
    @Severity(SeverityLevel.NORMAL)
    void f19_logout_fake_token() {
        ApiResponse response = authSteps.logout("fake-token-xyz");

        assertTrue(Set.of(401, 500).contains(response.statusCode()),
                "Expected 401/500, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(20)
    @Story("F-20 Sessions with invalid token → 401")
    @Severity(SeverityLevel.NORMAL)
    void f20_sessions_invalid_token() {
        ApiResponse response = sessionSteps.getSessions("bad-token");

        assertEquals(401, response.statusCode(),
                "Expected 401, got " + response.statusCode() + " | " + response.body());
    }
}
