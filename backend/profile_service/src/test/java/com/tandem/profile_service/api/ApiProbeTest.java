package com.tandem.profile_service.api;

import org.junit.jupiter.api.Test;
import io.qameta.allure.Epic;

import java.util.Map;
import java.util.List;
import java.util.UUID;

@Epic("API Probe")
class ApiProbeTest {

    private static final ProfileApiClient CLIENT = new ProfileApiClient();

    @Test
    void probe_all_profile_endpoints() {
        System.out.println("=== PROBING ALL PROFILE ENDPOINTS ===\n");

        String phone = ProfileApiConfig.randomPhone();
        String email = ProfileApiConfig.randomEmail("probe");
        String password = ProfileApiConfig.defaultPassword();

        var regPhone = CLIENT.authRegisterPhone(phone);
        System.out.printf("POST /auth/register/phone -> %d | %s%n%n", regPhone.statusCode(), regPhone.body());

        String verificationId = regPhone.body().path("verificationId").asText("");
        if (!verificationId.isBlank()) {
            var regVerify = CLIENT.authRegisterVerify(verificationId, ProfileApiConfig.verificationCode());
            System.out.printf("POST /auth/register/verify -> %d | %s%n%n", regVerify.statusCode(), regVerify.body());

            var regEmail = CLIENT.authRegisterEmail(verificationId, email, password);
            System.out.printf("POST /auth/register/email -> %d | %s%n%n", regEmail.statusCode(), regEmail.body());
        }

        var login = CLIENT.authLogin(email, password);
        System.out.printf("POST /auth/login -> %d | %s%n%n", login.statusCode(), login.body());

        String accessToken = login.body().path("accessToken").asText("");
        if (accessToken.isBlank()) {
            accessToken = "fake-bearer-token";
        }

        var profiles = CLIENT.getAllProfiles(accessToken);
        System.out.printf("GET /profiles -> %d | %s%n%n", profiles.statusCode(), profiles.body());

        var me = CLIENT.getMyProfile(accessToken);
        System.out.printf("GET /profile/me -> %d | %s%n%n", me.statusCode(), me.body());

        var updateBody = Map.of(
                "name", "ProbeUser",
                "surname", "TestSurname",
                "city", "Moscow",
                "status", "active");
        var update = CLIENT.updateMyProfile(updateBody, accessToken);
        System.out.printf("PUT /profile/me -> %d | %s%n%n", update.statusCode(), update.body());

        var patchBody = Map.of("jobTitle", "Engineer");
        var patch = CLIENT.patchMyProfile(patchBody, accessToken);
        System.out.printf("PATCH /profile/me -> %d | %s%n%n", patch.statusCode(), patch.body());

        var privacy = CLIENT.getMyPrivacySettings(accessToken);
        System.out.printf("GET /profile/me/privacy -> %d | %s%n%n", privacy.statusCode(), privacy.body());

        var privacyBody = Map.of(
                "showPhoneNumber", false,
                "showEmail", true,
                "showCity", true,
                "showPlaceOfWork", false,
                "showJobTitle", true,
                "showBirthday", false,
                "showPersonalInterests", true);
        var privacyUpdate = CLIENT.updateMyPrivacySettings(privacyBody, accessToken);
        System.out.printf("PUT /profile/me/privacy -> %d | %s%n%n", privacyUpdate.statusCode(), privacyUpdate.body());

        var otherProfile = CLIENT.getProfileById(UUID.randomUUID().toString(), accessToken);
        System.out.printf("GET /profile/{userId} (random) -> %d | %s%n%n", otherProfile.statusCode(), otherProfile.body());

        var questions = CLIENT.getOnboardingQuestions(accessToken);
        System.out.printf("GET /profile/onboarding/questions -> %d | %s%n%n", questions.statusCode(), questions.body());

        var completeBody = Map.of("responses", List.of());
        var complete = CLIENT.completeOnboarding(completeBody, accessToken);
        System.out.printf("POST /profile/onboarding/complete -> %d | %s%n%n", complete.statusCode(), complete.body());

        var meNoToken = CLIENT.getMyProfile(null);
        System.out.printf("GET /profile/me (no token) -> %d | %s%n%n", meNoToken.statusCode(), meNoToken.body());

        var meFakeToken = CLIENT.getMyProfile("invalid-token-abc");
        System.out.printf("GET /profile/me (fake token) -> %d | %s%n%n", meFakeToken.statusCode(), meFakeToken.body());

        var deleteProfile = CLIENT.deleteMyProfile(accessToken);
        System.out.printf("DELETE /profile/me -> %d | %s%n%n", deleteProfile.statusCode(), deleteProfile.body());

        System.out.println("=== PROBE FINISHED ===");
    }
}
