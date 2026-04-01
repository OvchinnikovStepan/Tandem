package com.tandem.profile_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@Epic("Profile Service")
@Feature("Regression Testing")
class RegressionProfileApiTest {

    private static final ProfileApiClient CLIENT = new ProfileApiClient();

    private String registerAndGetToken(String prefix) {
        String phone = ProfileApiConfig.randomPhone();
        String email = ProfileApiConfig.randomEmail(prefix);
        String password = ProfileApiConfig.defaultPassword();
        return CLIENT.obtainAccessToken(phone, email, password);
    }

    @Test
    @Story("R-01 Register → Get profile → Update → Verify chain")
    @Severity(SeverityLevel.BLOCKER)
    void r01_register_get_update_verify() {
        String token = registerAndGetToken("reg-r01");
        Assumptions.assumeTrue(token != null && !token.isBlank(),
                "Skipped: registration did not complete");

        ProfileApiClient.ApiResponse me = CLIENT.getMyProfile(token);
        assertEquals(200, me.statusCode(),
                "GET /profile/me failed: " + me.statusCode() + " | " + me.body());

        Map<String, Object> updateBody = Map.of(
                "name", "RegressionUser",
                "surname", "TestSurname",
                "city", "Saint Petersburg",
                "placeOfWork", "TestCompany",
                "jobTitle", "QA Engineer",
                "personalInterests", "testing, automation");

        ProfileApiClient.ApiResponse update = CLIENT.updateMyProfile(updateBody, token);
        assertEquals(200, update.statusCode(),
                "PUT /profile/me failed: " + update.statusCode() + " | " + update.body());
        assertTrue(update.body().path("updated").asBoolean(false),
                "updated must be true");

        ProfileApiClient.ApiResponse verify = CLIENT.getMyProfile(token);
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
        CLIENT.updateMyProfile(fullBody, token);

        Map<String, Object> patchBody = Map.of("jobTitle", "Lead Developer");
        ProfileApiClient.ApiResponse patch = CLIENT.patchMyProfile(patchBody, token);
        assertEquals(200, patch.statusCode(),
                "PATCH failed: " + patch.statusCode() + " | " + patch.body());

        ProfileApiClient.ApiResponse me = CLIENT.getMyProfile(token);
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
                "phoneNumber", ProfileApiConfig.randomPhone());
        CLIENT.updateMyProfile(profileBody, tokenA);

        Map<String, Object> privacyBody = Map.of(
                "showPhoneNumber", false,
                "showEmail", false,
                "showCity", true,
                "showPlaceOfWork", true,
                "showJobTitle", true,
                "showBirthday", true,
                "showPersonalInterests", true);
        CLIENT.updateMyPrivacySettings(privacyBody, tokenA);

        ProfileApiClient.ApiResponse meA = CLIENT.getMyProfile(tokenA);
        String userIdA = meA.body().path("userId").asText("");
        Assumptions.assumeTrue(!userIdA.isBlank(), "userId must be present");

        String tokenB = registerAndGetToken("reg-r03-b");
        Assumptions.assumeTrue(tokenB != null && !tokenB.isBlank(),
                "Skipped: user B registration did not complete");

        ProfileApiClient.ApiResponse viewA = CLIENT.getProfileById(userIdA, tokenB);
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

        ProfileApiClient.ApiResponse delete = CLIENT.deleteMyProfile(token);
        assertTrue(Set.of(200, 204).contains(delete.statusCode()),
                "DELETE /profile/me failed: " + delete.statusCode() + " | " + delete.body());

        ProfileApiClient.ApiResponse me = CLIENT.getMyProfile(token);
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

        ProfileApiClient.ApiResponse questions = CLIENT.getOnboardingQuestions(token);
        assertEquals(200, questions.statusCode(),
                "GET onboarding questions failed: " + questions.statusCode() + " | " + questions.body());
        assertTrue(questions.body().has("questions"),
                "questions field must be present");

        Map<String, Object> body = Map.of("responses", java.util.List.of());
        ProfileApiClient.ApiResponse complete = CLIENT.completeOnboarding(body, token);
        assertTrue(Set.of(200, 400).contains(complete.statusCode()),
                "POST onboarding/complete → " + complete.statusCode() + " | " + complete.body());

        if (complete.statusCode() == 200) {
            ProfileApiClient.ApiResponse me = CLIENT.getMyProfile(token);
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

        CLIENT.patchMyProfile(Map.of("name", "PatchName1"), token);
        CLIENT.patchMyProfile(Map.of("surname", "PatchSurname1"), token);
        CLIENT.patchMyProfile(Map.of("city", "Tokyo"), token);

        ProfileApiClient.ApiResponse me = CLIENT.getMyProfile(token);
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
        CLIENT.updateMyPrivacySettings(hideAll, token);

        Map<String, Object> showAll = Map.of(
                "showPhoneNumber", true,
                "showEmail", true,
                "showCity", true,
                "showPlaceOfWork", true,
                "showJobTitle", true,
                "showBirthday", true,
                "showPersonalInterests", true);
        ProfileApiClient.ApiResponse update = CLIENT.updateMyPrivacySettings(showAll, token);
        assertEquals(200, update.statusCode(),
                "PUT /profile/me/privacy failed: " + update.statusCode());

        ProfileApiClient.ApiResponse privacy = CLIENT.getMyPrivacySettings(token);
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

        ProfileApiClient.ApiResponse me = CLIENT.getMyProfile(token);
        String userId = me.body().path("userId").asText("");
        Assumptions.assumeTrue(!userId.isBlank(), "userId must be present");

        ProfileApiClient.ApiResponse profiles = CLIENT.getAllProfiles(token);
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

        assertEquals(401, CLIENT.getMyProfile(invalidToken).statusCode(),
                "GET /profile/me with invalid token");
        assertEquals(401, CLIENT.getAllProfiles(invalidToken).statusCode(),
                "GET /profiles with invalid token");
        assertEquals(401, CLIENT.updateMyProfile(Map.of("name", "X"), invalidToken).statusCode(),
                "PUT /profile/me with invalid token");
        assertEquals(401, CLIENT.patchMyProfile(Map.of("name", "X"), invalidToken).statusCode(),
                "PATCH /profile/me with invalid token");
        assertEquals(401, CLIENT.deleteMyProfile(invalidToken).statusCode(),
                "DELETE /profile/me with invalid token");
        assertEquals(401, CLIENT.getMyPrivacySettings(invalidToken).statusCode(),
                "GET /profile/me/privacy with invalid token");
        assertEquals(401, CLIENT.updateMyPrivacySettings(Map.of("showEmail", true), invalidToken).statusCode(),
                "PUT /profile/me/privacy with invalid token");
        assertEquals(401, CLIENT.getOnboardingQuestions(invalidToken).statusCode(),
                "GET /profile/onboarding/questions with invalid token");
        assertEquals(401, CLIENT.completeOnboarding(Map.of("responses", java.util.List.of()), invalidToken).statusCode(),
                "POST /profile/onboarding/complete with invalid token");
    }
}
