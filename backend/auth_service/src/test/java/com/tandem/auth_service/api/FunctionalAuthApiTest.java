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

    private static final AuthApiClient CLIENT = new AuthApiClient();

    private static String verificationId;
    private static boolean phoneVerified = false;
    private static String accessToken;
    private static String refreshToken;

    private static final String MAIN_PHONE = AuthApiConfig.randomPhone();
    private static final String MAIN_EMAIL = AuthApiConfig.randomEmail("functional-main");
    private static final String MAIN_PASSWORD = AuthApiConfig.defaultPassword();

    @Test
    @Order(1)
    @Story("F-01 Register phone — start registration")
    @Severity(SeverityLevel.BLOCKER)
    void f01_register_phone() {
        AuthApiClient.ApiResponse response = CLIENT.registerPhone(MAIN_PHONE);

        Allure.step("Validate 200 and verificationId returned");
        assertEquals(200, response.statusCode(),
                "POST /register/phone → " + response.statusCode() + " | " + response.body());
        assertFalse(response.body().path("verificationId").asText("").isBlank(),
                "verificationId must not be blank");
        assertTrue(response.body().path("expiresIn").asInt(0) > 0,
                "expiresIn must be > 0");
        assertTrue(response.body().path("attemptsRemaining").asInt(0) > 0,
                "attemptsRemaining must be > 0");

        verificationId = response.body().path("verificationId").asText();
    }

    @Test
    @Order(2)
    @Story("F-02 Verify phone — mock returns INVALID_CODE or 200")
    @Severity(SeverityLevel.BLOCKER)
    void f02_verify_phone() {
        assertNotNull(verificationId, "verificationId must be set from F-01");

        AuthApiClient.ApiResponse response = CLIENT.registerVerify(
                verificationId, AuthApiConfig.verificationCode());

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

        AuthApiClient.ApiResponse response = CLIENT.registerEmail(
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
        AuthApiClient.ApiResponse response = CLIENT.login(MAIN_EMAIL, MAIN_PASSWORD);

        if (phoneVerified) {
            assertTrue(Set.of(200, 201).contains(response.statusCode()),
                    "Expected 200/201, got " + response.statusCode() + " | " + response.body());
            accessToken = response.body().path("accessToken").asText();
            refreshToken = response.body().path("refreshToken").asText();
        } else {
            assertEquals(401, response.statusCode(),
                    "Expected 401, got " + response.statusCode() + " | " + response.body());
            assertEquals("INVALID_CREDENTIALS",
                    response.body().path("error").asText());
        }
    }

    @Test
    @Order(5)
    @Story("F-05 Login with wrong password → 401")
    @Severity(SeverityLevel.CRITICAL)
    void f05_wrong_password_login() {
        AuthApiClient.ApiResponse response = CLIENT.login(MAIN_EMAIL, "WrongPass999");

        assertTrue(Set.of(400, 401, 403).contains(response.statusCode()),
                "Expected 4xx, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(6)
    @Story("F-06 Login with non-existent email → 401")
    @Severity(SeverityLevel.CRITICAL)
    void f06_unknown_email_login() {
        AuthApiClient.ApiResponse response = CLIENT.login(
                AuthApiConfig.randomEmail("functional-missing"), MAIN_PASSWORD);

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

        AuthApiClient.ApiResponse response = CLIENT.me(accessToken);
        assertEquals(200, response.statusCode(),
                "Expected 200, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(8)
    @Story("F-08 /me with invalid token → 401")
    @Severity(SeverityLevel.CRITICAL)
    void f08_me_invalid_token() {
        AuthApiClient.ApiResponse response = CLIENT.me("invalid-token-abc");

        assertEquals(401, response.statusCode(),
                "Expected 401, got " + response.statusCode() + " | " + response.body());
        assertEquals("INVALID_TOKEN", response.body().path("error").asText());
    }

    @Test
    @Order(9)
    @Story("F-09 Refresh token rotation")
    @Severity(SeverityLevel.BLOCKER)
    void f09_refresh_rotation() {
        Assumptions.assumeTrue(refreshToken != null && !refreshToken.isBlank(),
                "Skipped: no valid refreshToken (registration did not complete)");

        AuthApiClient.ApiResponse firstRefresh = CLIENT.refresh(refreshToken);
        assertEquals(200, firstRefresh.statusCode(),
                "Expected 200, got " + firstRefresh.statusCode() + " | " + firstRefresh.body());

        AuthApiClient.ApiResponse reuse = CLIENT.refresh(refreshToken);
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
        AuthApiClient.ApiResponse response = CLIENT.refresh("definitely-not-a-valid-token");

        assertEquals(401, response.statusCode(),
                "Expected 401, got " + response.statusCode() + " | " + response.body());
        assertEquals("INVALID_REFRESH_TOKEN", response.body().path("error").asText());
    }

    @Test
    @Order(11)
    @Story("F-11 Check password strength")
    @Severity(SeverityLevel.NORMAL)
    void f11_check_password_strength() {
        AuthApiClient.ApiResponse response = CLIENT.checkPasswordStrength("Qwerty123");

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
    @Story("F-12 Password reset request → 200")
    @Severity(SeverityLevel.CRITICAL)
    void f12_password_reset_request() {
        AuthApiClient.ApiResponse response = CLIENT.passwordResetRequest(MAIN_EMAIL, "email");

        assertEquals(200, response.statusCode(),
                "Expected 200, got " + response.statusCode() + " | " + response.body());
        assertFalse(response.body().path("resetId").asText("").isBlank(),
                "resetId must be present");
        assertEquals("email", response.body().path("method").asText(),
                "method must be 'email'");
        assertTrue(response.body().path("expiresIn").asInt(0) > 0,
                "expiresIn must be > 0");
    }

    @Test
    @Order(13)
    @Story("F-13 Password reset verify with invalid token → valid=false")
    @Severity(SeverityLevel.NORMAL)
    void f13_password_reset_verify_invalid() {
        AuthApiClient.ApiResponse response = CLIENT.passwordResetVerify("invalid-token");

        assertEquals(200, response.statusCode(),
                "Expected 200, got " + response.statusCode() + " | " + response.body());
        assertFalse(response.body().path("valid").asBoolean(true),
                "valid must be false for invalid token");
    }

    @Test
    @Order(14)
    @Story("F-14 Password reset complete with invalid token → 400")
    @Severity(SeverityLevel.NORMAL)
    void f14_password_reset_complete_invalid() {
        AuthApiClient.ApiResponse response = CLIENT.passwordResetComplete("invalid-token", "NewPass123!");

        assertEquals(400, response.statusCode(),
                "Expected 400, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(15)
    @Story("F-15 Register phone with invalid format")
    @Severity(SeverityLevel.NORMAL)
    void f15_register_phone_invalid_format() {
        AuthApiClient.ApiResponse response = CLIENT.registerPhone("not-a-phone");

        assertTrue(Set.of(200, 400, 422, 500).contains(response.statusCode()),
                "Expected 200/400/422/500, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(16)
    @Story("F-16 Register verify with invalid verificationId → 400")
    @Severity(SeverityLevel.NORMAL)
    void f16_verify_invalid_verification_id() {
        AuthApiClient.ApiResponse response = CLIENT.registerVerify(
                "nonexistent-id", AuthApiConfig.verificationCode());

        assertTrue(Set.of(400, 404).contains(response.statusCode()),
                "Expected 400/404, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(17)
    @Story("F-17 Register email without phone verification → 400")
    @Severity(SeverityLevel.NORMAL)
    void f17_register_email_without_verification() {
        String phone = AuthApiConfig.randomPhone();
        AuthApiClient.ApiResponse phoneResp = CLIENT.registerPhone(phone);
        String vid = phoneResp.body().path("verificationId").asText("");

        AuthApiClient.ApiResponse response = CLIENT.registerEmail(
                vid, AuthApiConfig.randomEmail("f17"), MAIN_PASSWORD);

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

        AuthApiClient.ApiResponse response = CLIENT.logout(accessToken);
        assertTrue(Set.of(200, 204).contains(response.statusCode()),
                "Expected 200/204, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(19)
    @Story("F-19 Logout with fake token → 401/500")
    @Severity(SeverityLevel.NORMAL)
    void f19_logout_fake_token() {
        AuthApiClient.ApiResponse response = CLIENT.logout("fake-token-xyz");

        assertTrue(Set.of(401, 500).contains(response.statusCode()),
                "Expected 401/500, got " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(20)
    @Story("F-20 Sessions with invalid token → 401")
    @Severity(SeverityLevel.NORMAL)
    void f20_sessions_invalid_token() {
        AuthApiClient.ApiResponse response = CLIENT.sessions("bad-token");

        assertEquals(401, response.statusCode(),
                "Expected 401, got " + response.statusCode() + " | " + response.body());
    }
}
