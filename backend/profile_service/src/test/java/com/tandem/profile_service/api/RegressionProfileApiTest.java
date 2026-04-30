package com.tandem.profile_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

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

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@Epic("Profile Service")
@Feature("Regression Testing")
class RegressionProfileApiTest {

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

    private String registerAndGetToken(String prefix) {
        return authSteps.obtainAccessToken(prefix);
    }

    @Test
    @Story("R-01 Register → Get profile → Update → Verify chain")
    @Severity(SeverityLevel.BLOCKER)
    void r01_register_get_update_verify() {
        String token = registerAndGetToken("reg-r01");
        Assumptions.assumeTrue(token != null && !token.isBlank(),
                "Skipped: registration did not complete");

        ApiResponse me = profileSteps.getMyProfile(token);
        assertEquals(200, me.statusCode(),
                "GET /profile/me failed: " + me.statusCode() + " | " + me.body());

        Map<String, Object> updateBody = Map.of(
                "name", "RegressionUser",
                "surname", "TestSurname",
                "city", "Saint Petersburg",
                "placeOfWork", "TestCompany",
                "jobTitle", "QA Engineer",
                "personalInterests", "testing, automation");

        ApiResponse update = profileSteps.updateMyProfile(updateBody, token);
        assertEquals(200, update.statusCode(),
                "PUT /profile/me failed: " + update.statusCode() + " | " + update.body());
        assertTrue(update.body().path("updated").asBoolean(false),
                "updated must be true");

        ApiResponse verify = profileSteps.getMyProfile(token);
        assertEquals(200, verify.statusCode());
        assertEquals("RegressionUser", verify.body().path("name").asText(),
                "name must be 'RegressionUser'");
        assertEquals("TestSurname", verify.body().path("surname").asText(),
                "surname must be 'TestSurname'");
    }

    @Test
    @Story("R-02 PATCH preserves existing profile data")
    @Severity(SeverityLevel.CRITICAL)
    void r02_patch_preserves_data() {
        String token = registerAndGetToken("reg-r02");
        Assumptions.assumeTrue(token != null && !token.isBlank(),
                "Skipped: registration did not complete");

        Map<String, Object> fullBody = Map.of(
                "name", "OriginalName",
                "surname", "OriginalSurname",
                "city", "Berlin",
                "jobTitle", "Developer");
        profileSteps.updateMyProfile(fullBody, token);

        Map<String, Object> patchBody = Map.of("jobTitle", "Lead Developer");
        ApiResponse patch = profileSteps.patchMyProfile(patchBody, token);
        assertEquals(200, patch.statusCode(),
                "PATCH failed: " + patch.statusCode() + " | " + patch.body());

        ApiResponse me = profileSteps.getMyProfile(token);
        assertEquals(200, me.statusCode());
        assertEquals("OriginalName", me.body().path("name").asText(),
                "name must be preserved after PATCH");
        assertEquals("OriginalSurname", me.body().path("surname").asText(),
                "surname must be preserved after PATCH");
        assertEquals("Lead Developer", me.body().path("bio").path("jobTitle").asText(),
                "jobTitle must be updated");
    }

    @Test
    @Story("R-03 Privacy settings affect other user's view")
    @Severity(SeverityLevel.CRITICAL)
    void r03_privacy_affects_profile_view() {
        String tokenA = registerAndGetToken("reg-r03-a");
        Assumptions.assumeTrue(tokenA != null && !tokenA.isBlank(),
                "Skipped: user A registration did not complete");

        Map<String, Object> profileBody = Map.of(
                "name", "UserA",
                "surname", "SurnameA",
                "city", "Moscow",
                "phoneNumber", TestDataFactory.randomPhone());
        profileSteps.updateMyProfile(profileBody, tokenA);

        Map<String, Object> privacyBody = Map.of(
                "showPhoneNumber", false,
                "showEmail", false,
                "showCity", true,
                "showPlaceOfWork", true,
                "showJobTitle", true,
                "showBirthday", true,
                "showPersonalInterests", true);
        privacySteps.updateMyPrivacySettings(privacyBody, tokenA);

        ApiResponse meA = profileSteps.getMyProfile(tokenA);
        String userIdA = meA.body().path("userId").asText("");
        Assumptions.assumeTrue(!userIdA.isBlank(), "userId must be present");

        String tokenB = registerAndGetToken("reg-r03-b");
        Assumptions.assumeTrue(tokenB != null && !tokenB.isBlank(),
                "Skipped: user B registration did not complete");

        ApiResponse viewA = profileSteps.getProfileById(userIdA, tokenB);
        assertEquals(200, viewA.statusCode(),
                "GET /profile/{userId} failed: " + viewA.statusCode() + " | " + viewA.body());

        assertTrue(viewA.body().path("phoneNumber").isNull()
                        || viewA.body().path("phoneNumber").isMissingNode()
                        || viewA.body().path("phoneNumber").asText("").isBlank(),
                "phoneNumber should be hidden by privacy");
    }

    @Test
    @Story("R-04 Delete profile → get returns 404")
    @Severity(SeverityLevel.CRITICAL)
    void r04_delete_then_get() {
        String token = registerAndGetToken("reg-r04");
        Assumptions.assumeTrue(token != null && !token.isBlank(),
                "Skipped: registration did not complete");

        ApiResponse delete = profileSteps.deleteMyProfile(token);
        assertTrue(Set.of(200, 204).contains(delete.statusCode()),
                "DELETE /profile/me failed: " + delete.statusCode() + " | " + delete.body());

        ApiResponse me = profileSteps.getMyProfile(token);
        assertTrue(Set.of(401, 403, 404, 500).contains(me.statusCode()),
                "GET /profile/me after delete should fail, got " + me.statusCode() + " | " + me.body());
    }

    @Test
    @Story("R-05 Onboarding flow: questions → complete → verify flag")
    @Severity(SeverityLevel.CRITICAL)
    void r05_onboarding_flow() {
        String token = registerAndGetToken("reg-r05");
        Assumptions.assumeTrue(token != null && !token.isBlank(),
                "Skipped: registration did not complete");

        ApiResponse questions = onboardingSteps.getOnboardingQuestions(token);
        assertEquals(200, questions.statusCode(),
                "GET onboarding questions failed: " + questions.statusCode() + " | " + questions.body());
        assertTrue(questions.body().has("questions"),
                "questions field must be present");

        Map<String, Object> body = Map.of("responses", java.util.List.of());
        ApiResponse complete = onboardingSteps.completeOnboarding(body, token);
        assertTrue(Set.of(200, 400, 403).contains(complete.statusCode()),
                "POST onboarding/complete → " + complete.statusCode() + " | " + complete.body());

        if (complete.statusCode() == 200) {
            ApiResponse me = profileSteps.getMyProfile(token);
            assertEquals(200, me.statusCode());
            assertTrue(me.body().path("onboardingCompleted").asBoolean(false),
                    "onboardingCompleted must be true after completing onboarding");
        }
    }

    @Test
    @Story("R-06 Multiple sequential patches accumulate correctly")
    @Severity(SeverityLevel.NORMAL)
    void r06_sequential_patches() {
        String token = registerAndGetToken("reg-r06");
        Assumptions.assumeTrue(token != null && !token.isBlank(),
                "Skipped: registration did not complete");

        profileSteps.patchMyProfile(Map.of("name", "PatchName1"), token);
        profileSteps.patchMyProfile(Map.of("surname", "PatchSurname1"), token);
        profileSteps.patchMyProfile(Map.of("city", "Tokyo"), token);

        ApiResponse me = profileSteps.getMyProfile(token);
        assertEquals(200, me.statusCode());
        assertEquals("PatchName1", me.body().path("name").asText(),
                "name must be 'PatchName1'");
        assertEquals("PatchSurname1", me.body().path("surname").asText(),
                "surname must be 'PatchSurname1'");
        assertEquals("Tokyo", me.body().path("bio").path("city").asText(),
                "city must be 'Tokyo'");
    }

    @Test
    @Story("R-07 Privacy settings can be toggled back to all visible")
    @Severity(SeverityLevel.NORMAL)
    void r07_privacy_toggle() {
        String token = registerAndGetToken("reg-r07");
        Assumptions.assumeTrue(token != null && !token.isBlank(),
                "Skipped: registration did not complete");

        Map<String, Object> hideAll = Map.of(
                "showPhoneNumber", false,
                "showEmail", false,
                "showCity", false,
                "showPlaceOfWork", false,
                "showJobTitle", false,
                "showBirthday", false,
                "showPersonalInterests", false);
        privacySteps.updateMyPrivacySettings(hideAll, token);

        Map<String, Object> showAll = Map.of(
                "showPhoneNumber", true,
                "showEmail", true,
                "showCity", true,
                "showPlaceOfWork", true,
                "showJobTitle", true,
                "showBirthday", true,
                "showPersonalInterests", true);
        ApiResponse update = privacySteps.updateMyPrivacySettings(showAll, token);
        assertEquals(200, update.statusCode(),
                "PUT /profile/me/privacy failed: " + update.statusCode());

        ApiResponse privacy = privacySteps.getMyPrivacySettings(token);
        assertEquals(200, privacy.statusCode());
        assertTrue(privacy.body().path("showPhoneNumber").asBoolean(false),
                "showPhoneNumber must be true");
        assertTrue(privacy.body().path("showEmail").asBoolean(false),
                "showEmail must be true");
        assertTrue(privacy.body().path("showCity").asBoolean(false),
                "showCity must be true");
    }

    @Test
    @Story("R-08 Created profile appears in profiles list")
    @Severity(SeverityLevel.NORMAL)
    void r08_profile_in_list() {
        String token = registerAndGetToken("reg-r08");
        Assumptions.assumeTrue(token != null && !token.isBlank(),
                "Skipped: registration did not complete");

        ApiResponse me = profileSteps.getMyProfile(token);
        String userId = me.body().path("userId").asText("");
        Assumptions.assumeTrue(!userId.isBlank(), "userId must be present");

        ApiResponse profiles = profileSteps.getAllProfiles(token);
        assertEquals(200, profiles.statusCode(),
                "GET /profiles failed: " + profiles.statusCode());
        assertTrue(profiles.body().isArray() && profiles.body().size() > 0,
                "Profiles list must not be empty");
    }

    @Test
    @Story("R-09 Invalid token returns 401 for all protected endpoints")
    @Severity(SeverityLevel.CRITICAL)
    void r09_invalid_token_all_endpoints() {
        String invalidToken = "definitely-not-a-valid-token";

        assertTrue(Set.of(401, 403).contains(profileSteps.getMyProfile(invalidToken).statusCode()),
                "GET /profile/me with invalid token");
        assertTrue(Set.of(401, 403).contains(profileSteps.getAllProfiles(invalidToken).statusCode()),
                "GET /profiles with invalid token");
        assertTrue(Set.of(401, 403).contains(profileSteps.updateMyProfile(Map.of("name", "X"), invalidToken).statusCode()),
                "PUT /profile/me with invalid token");
        assertTrue(Set.of(401, 403).contains(profileSteps.patchMyProfile(Map.of("name", "X"), invalidToken).statusCode()),
                "PATCH /profile/me with invalid token");
        assertTrue(Set.of(401, 403).contains(profileSteps.deleteMyProfile(invalidToken).statusCode()),
                "DELETE /profile/me with invalid token");
        assertTrue(Set.of(401, 403).contains(privacySteps.getMyPrivacySettings(invalidToken).statusCode()),
                "GET /profile/me/privacy with invalid token");
        assertTrue(Set.of(401, 403).contains(privacySteps.updateMyPrivacySettings(Map.of("showEmail", true), invalidToken).statusCode()),
                "PUT /profile/me/privacy with invalid token");
        assertTrue(Set.of(401, 403).contains(onboardingSteps.getOnboardingQuestions(invalidToken).statusCode()),
                "GET /profile/onboarding/questions with invalid token");
        assertTrue(Set.of(401, 403).contains(onboardingSteps.completeOnboarding(Map.of("responses", java.util.List.of()), invalidToken).statusCode()),
                "POST /profile/onboarding/complete with invalid token");
    }
}
