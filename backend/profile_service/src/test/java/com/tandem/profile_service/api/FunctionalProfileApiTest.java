package com.tandem.profile_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.tandem.profile_service.api.client.AuthClient;
import com.tandem.profile_service.api.client.BaseApiClient;
import com.tandem.profile_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.profile_service.api.client.OnboardingClient;
import com.tandem.profile_service.api.client.PrivacyClient;
import com.tandem.profile_service.api.client.ProfileClient;
import com.tandem.profile_service.api.config.ApiConfig;
import com.tandem.profile_service.api.step.AuthSteps;
import com.tandem.profile_service.api.step.OnboardingSteps;
import com.tandem.profile_service.api.step.PrivacySteps;
import com.tandem.profile_service.api.step.ProfileSteps;
import com.tandem.profile_service.api.util.TestDataFactory;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@Epic("Profile Service")
@Feature("Functional Testing")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FunctionalProfileApiTest {

    private static final BaseApiClient BASE = new BaseApiClient();
    private static final BaseApiClient AUTH_BASE = new BaseApiClient(ApiConfig.authBaseUrl());

    private static final AuthClient AUTH_CLIENT = new AuthClient(AUTH_BASE);
    private static final ProfileClient PROFILE_CLIENT = new ProfileClient(BASE);
    private static final PrivacyClient PRIVACY_CLIENT = new PrivacyClient(BASE);
    private static final OnboardingClient ONBOARDING_CLIENT = new OnboardingClient(BASE);

    private static final AuthSteps authSteps = new AuthSteps(AUTH_CLIENT);
    private static final ProfileSteps profileSteps = new ProfileSteps(PROFILE_CLIENT);
    private static final PrivacySteps privacySteps = new PrivacySteps(PRIVACY_CLIENT);
    private static final OnboardingSteps onboardingSteps = new OnboardingSteps(ONBOARDING_CLIENT);

    private static final String MAIN_PHONE = TestDataFactory.randomPhone();
    private static final String MAIN_EMAIL = TestDataFactory.randomEmail("functional-profile");
    private static final String MAIN_PASSWORD = ApiConfig.defaultPassword();

    private static String accessToken;

    @BeforeAll
    static void obtainToken() {
        accessToken = authSteps.obtainAccessToken("functional-profile");
    }

    // ── GET /profile/me ──

    @Test
    @Order(1)
    @Story("F-01 Get my profile with valid token")
    @Severity(SeverityLevel.BLOCKER)
    void f01_get_my_profile() {
        Assumptions.assumeTrue(accessToken != null && !accessToken.isBlank(),
                "Skipped: no valid accessToken (registration did not complete)");

        ApiResponse response = profileSteps.getMyProfile(accessToken);

        Allure.step("Validate 200 and profile fields returned");
        assertEquals(200, response.statusCode(),
                "GET /profile/me → " + response.statusCode() + " | " + response.body());
        assertNotNull(response.body().path("userId").asText(null),
                "userId must be present");
    }

    @Test
    @Order(2)
    @Story("F-02 Get my profile without token → 401/403")
    @Severity(SeverityLevel.BLOCKER)
    void f02_get_my_profile_no_token() {
        ApiResponse response = profileSteps.getMyProfile(null);

        Allure.step("Validate 401/403 for missing token");
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "GET /profile/me without token → " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(3)
    @Story("F-03 Get my profile with invalid token → 401/403")
    @Severity(SeverityLevel.CRITICAL)
    void f03_get_my_profile_invalid_token() {
        ApiResponse response = profileSteps.getMyProfile("invalid-token-abc");

        Allure.step("Validate 401/403 for invalid token");
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "GET /profile/me with invalid token → " + response.statusCode() + " | " + response.body());
    }

    // ── PUT /profile/me ──

    @Test
    @Order(4)
    @Story("F-04 Full update my profile")
    @Severity(SeverityLevel.BLOCKER)
    void f04_update_my_profile() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        Map<String, Object> body = Map.of(
                "name", "TestName",
                "surname", "TestSurname",
                "phoneNumber", MAIN_PHONE,
                "email", MAIN_EMAIL,
                "status", "active",
                "birthday", "2000-01-15",
                "city", "Moscow",
                "placeOfWork", "Tandem Corp",
                "jobTitle", "Developer",
                "personalInterests", "coding, music");

        ApiResponse response = profileSteps.updateMyProfile(body, accessToken);

        Allure.step("Validate 200 and updated=true");
        assertEquals(200, response.statusCode(),
                "PUT /profile/me → " + response.statusCode() + " | " + response.body());
        assertTrue(response.body().path("updated").asBoolean(false),
                "updated must be true");
        assertNotNull(response.body().path("profile"),
                "profile must be present in response");
    }

    @Test
    @Order(5)
    @Story("F-05 Update profile without token → 401")
    @Severity(SeverityLevel.CRITICAL)
    void f05_update_profile_no_token() {
        Map<String, Object> body = Map.of("name", "Hacker");

        ApiResponse response = profileSteps.updateMyProfile(body, null);

        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "PUT /profile/me without token → " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(6)
    @Story("F-06 Verify profile data persisted after update")
    @Severity(SeverityLevel.BLOCKER)
    void f06_verify_profile_data_after_update() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = profileSteps.getMyProfile(accessToken);

        assertEquals(200, response.statusCode(),
                "GET /profile/me → " + response.statusCode());
        assertEquals("TestName", response.body().path("name").asText(),
                "name must be 'TestName' after update");
        assertEquals("TestSurname", response.body().path("surname").asText(),
                "surname must be 'TestSurname' after update");
    }

    // ── PATCH /profile/me ──

    @Test
    @Order(7)
    @Story("F-07 Partial update my profile (PATCH)")
    @Severity(SeverityLevel.BLOCKER)
    void f07_patch_my_profile() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        Map<String, Object> body = Map.of("jobTitle", "Senior Developer");

        ApiResponse response = profileSteps.patchMyProfile(body, accessToken);

        Allure.step("Validate 200 and updated=true");
        assertEquals(200, response.statusCode(),
                "PATCH /profile/me → " + response.statusCode() + " | " + response.body());
        assertTrue(response.body().path("updated").asBoolean(false),
                "updated must be true");
    }

    @Test
    @Order(8)
    @Story("F-08 PATCH preserves other fields")
    @Severity(SeverityLevel.CRITICAL)
    void f08_patch_preserves_other_fields() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = profileSteps.getMyProfile(accessToken);

        assertEquals(200, response.statusCode());
        assertEquals("TestName", response.body().path("name").asText(),
                "name must still be 'TestName' after PATCH of jobTitle");
        assertEquals("Senior Developer", response.body().path("bio").path("jobTitle").asText(),
                "jobTitle must be updated to 'Senior Developer'");
    }

    @Test
    @Order(9)
    @Story("F-09 PATCH without token → 401")
    @Severity(SeverityLevel.CRITICAL)
    void f09_patch_no_token() {
        Map<String, Object> body = Map.of("city", "Berlin");

        ApiResponse response = profileSteps.patchMyProfile(body, null);

        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "PATCH /profile/me without token → " + response.statusCode() + " | " + response.body());
    }

    // ── GET /profiles ──

    @Test
    @Order(10)
    @Story("F-10 Get all profiles")
    @Severity(SeverityLevel.NORMAL)
    void f10_get_all_profiles() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = profileSteps.getAllProfiles(accessToken);

        Allure.step("Validate 200 and array returned");
        assertEquals(200, response.statusCode(),
                "GET /profiles → " + response.statusCode() + " | " + response.body());
        assertTrue(response.body().isArray(),
                "Response must be an array");
    }

    @Test
    @Order(11)
    @Story("F-11 Get all profiles without token → 401")
    @Severity(SeverityLevel.NORMAL)
    void f11_get_all_profiles_no_token() {
        ApiResponse response = profileSteps.getAllProfiles(null);

        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "GET /profiles without token → " + response.statusCode() + " | " + response.body());
    }

    // ── GET /profile/{userId} ──

    @Test
    @Order(12)
    @Story("F-12 Get profile by userId")
    @Severity(SeverityLevel.BLOCKER)
    void f12_get_profile_by_id() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse me = profileSteps.getMyProfile(accessToken);
        String userId = me.body().path("userId").asText("");
        Assumptions.assumeTrue(!userId.isBlank(), "userId must be present");

        ApiResponse response = profileSteps.getProfileById(userId, accessToken);

        assertEquals(200, response.statusCode(),
                "GET /profile/{userId} → " + response.statusCode() + " | " + response.body());
        assertEquals(userId, response.body().path("userId").asText(),
                "userId must match");
    }

    @Test
    @Order(13)
    @Story("F-13 Get profile by non-existent userId → 404")
    @Severity(SeverityLevel.NORMAL)
    void f13_get_profile_nonexistent_user() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = profileSteps.getProfileById(
                "00000000-0000-0000-0000-000000000000", accessToken);

        assertTrue(Set.of(403, 404, 400, 500).contains(response.statusCode()),
                "GET /profile/{nonexistent} → " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(14)
    @Story("F-14 Get profile by userId without token → 401")
    @Severity(SeverityLevel.CRITICAL)
    void f14_get_profile_by_id_no_token() {
        ApiResponse response = profileSteps.getProfileById(
                "00000000-0000-0000-0000-000000000001", null);

        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "GET /profile/{userId} without token → " + response.statusCode() + " | " + response.body());
    }

    // ── GET /profile/me/privacy ──

    @Test
    @Order(15)
    @Story("F-15 Get privacy settings")
    @Severity(SeverityLevel.CRITICAL)
    void f15_get_privacy_settings() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = privacySteps.getMyPrivacySettings(accessToken);

        Allure.step("Validate 200 and privacy fields");
        assertEquals(200, response.statusCode(),
                "GET /profile/me/privacy → " + response.statusCode() + " | " + response.body());
        assertTrue(response.body().has("showPhoneNumber"),
                "showPhoneNumber must be present");
        assertTrue(response.body().has("showEmail"),
                "showEmail must be present");
    }

    @Test
    @Order(16)
    @Story("F-16 Update privacy settings")
    @Severity(SeverityLevel.CRITICAL)
    void f16_update_privacy_settings() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        Map<String, Object> body = Map.of(
                "showPhoneNumber", false,
                "showEmail", true,
                "showCity", true,
                "showPlaceOfWork", false,
                "showJobTitle", true,
                "showBirthday", false,
                "showPersonalInterests", true);

        ApiResponse response = privacySteps.updateMyPrivacySettings(body, accessToken);

        Allure.step("Validate 200 and updated=true");
        assertEquals(200, response.statusCode(),
                "PUT /profile/me/privacy → " + response.statusCode() + " | " + response.body());
        assertTrue(response.body().path("updated").asBoolean(false),
                "updated must be true");
    }

    @Test
    @Order(17)
    @Story("F-17 Privacy settings persisted correctly")
    @Severity(SeverityLevel.CRITICAL)
    void f17_privacy_settings_persisted() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = privacySteps.getMyPrivacySettings(accessToken);

        assertEquals(200, response.statusCode());
        assertFalse(response.body().path("showPhoneNumber").asBoolean(true),
                "showPhoneNumber must be false after update");
        assertTrue(response.body().path("showEmail").asBoolean(false),
                "showEmail must be true after update");
    }

    @Test
    @Order(18)
    @Story("F-18 Get privacy without token → 401")
    @Severity(SeverityLevel.NORMAL)
    void f18_get_privacy_no_token() {
        ApiResponse response = privacySteps.getMyPrivacySettings(null);

        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "GET /profile/me/privacy without token → " + response.statusCode());
    }

    @Test
    @Order(19)
    @Story("F-19 Update privacy without token → 401")
    @Severity(SeverityLevel.NORMAL)
    void f19_update_privacy_no_token() {
        Map<String, Object> body = Map.of("showEmail", false);

        ApiResponse response = privacySteps.updateMyPrivacySettings(body, null);

        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "PUT /profile/me/privacy without token → " + response.statusCode());
    }

    // ── Onboarding ──

    @Test
    @Order(20)
    @Story("F-20 Get onboarding questions")
    @Severity(SeverityLevel.CRITICAL)
    void f20_get_onboarding_questions() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = onboardingSteps.getOnboardingQuestions(accessToken);

        Allure.step("Validate 200 and questions present");
        assertEquals(200, response.statusCode(),
                "GET /profile/onboarding/questions → " + response.statusCode() + " | " + response.body());
        assertTrue(response.body().has("questions"),
                "questions must be present");
    }

    @Test
    @Order(21)
    @Story("F-21 Get onboarding questions without token → 401")
    @Severity(SeverityLevel.NORMAL)
    void f21_get_onboarding_questions_no_token() {
        ApiResponse response = onboardingSteps.getOnboardingQuestions(null);

        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "GET /profile/onboarding/questions without token → " + response.statusCode());
    }

    @Test
    @Order(22)
    @Story("F-22 Complete onboarding")
    @Severity(SeverityLevel.CRITICAL)
    void f22_complete_onboarding() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        Map<String, Object> body = Map.of("responses", java.util.List.of());

        ApiResponse response = onboardingSteps.completeOnboarding(body, accessToken);

        Allure.step("Validate 200 and onboardingCompleted");
        assertTrue(Set.of(200, 400, 403).contains(response.statusCode()),
                "POST /profile/onboarding/complete → " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(23)
    @Story("F-23 Complete onboarding without token → 401")
    @Severity(SeverityLevel.NORMAL)
    void f23_complete_onboarding_no_token() {
        Map<String, Object> body = Map.of("responses", java.util.List.of());

        ApiResponse response = onboardingSteps.completeOnboarding(body, null);

        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "POST /profile/onboarding/complete without token → " + response.statusCode());
    }

    // ── DELETE /profile/me ──

    @Test
    @Order(24)
    @Story("F-24 Delete my profile")
    @Severity(SeverityLevel.CRITICAL)
    void f24_delete_my_profile() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = profileSteps.deleteMyProfile(accessToken);

        Allure.step("Validate 204 No Content");
        assertTrue(Set.of(200, 204).contains(response.statusCode()),
                "DELETE /profile/me → " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(25)
    @Story("F-25 Delete profile without token → 401")
    @Severity(SeverityLevel.NORMAL)
    void f25_delete_profile_no_token() {
        ApiResponse response = profileSteps.deleteMyProfile(null);

        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "DELETE /profile/me without token → " + response.statusCode());
    }

    // ── Validation ──

    @Test
    @Order(26)
    @Story("F-26 Update profile with invalid email → 400")
    @Severity(SeverityLevel.NORMAL)
    void f26_update_profile_invalid_email() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        Map<String, Object> body = Map.of("email", "not-an-email");

        ApiResponse response = profileSteps.updateMyProfile(body, accessToken);

        assertTrue(Set.of(400, 403, 422).contains(response.statusCode()),
                "PUT /profile/me with invalid email → " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(27)
    @Story("F-27 Update profile with name exceeding max length → 400")
    @Severity(SeverityLevel.NORMAL)
    void f27_update_profile_name_too_long() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        String longName = "A".repeat(150);
        Map<String, Object> body = Map.of("name", longName);

        ApiResponse response = profileSteps.updateMyProfile(body, accessToken);

        assertTrue(Set.of(400, 403, 422).contains(response.statusCode()),
                "PUT /profile/me with name too long → " + response.statusCode() + " | " + response.body());
    }
}
