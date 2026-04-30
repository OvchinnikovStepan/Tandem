package com.tandem.profile_service.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.tandem.profile_service.api.client.AuthClient;
import com.tandem.profile_service.api.client.BaseApiClient;
import com.tandem.profile_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.profile_service.api.config.ApiConfig;

import io.qameta.allure.Epic;

@Epic("API Probe")
class ApiProbeTest {

    private static final BaseApiClient BASE = new BaseApiClient();
    private static final BaseApiClient AUTH_BASE = new BaseApiClient(ApiConfig.authBaseUrl());
    private static final AuthClient AUTH_CLIENT = new AuthClient(AUTH_BASE);

    @Test
    void probe_all_profile_endpoints() {
        System.out.println("=== PROBING ALL PROFILE ENDPOINTS ===\n");

        String phone = "+1" + ((long) (Math.random() * 9_000_000_000L) + 1_000_000_000L);
        String email = "probe-" + UUID.randomUUID() + "@test.local";
        String password = ApiConfig.defaultPassword();

        ApiResponse regPhone = AUTH_CLIENT.registerPhone(phone);
        System.out.printf("POST /auth/register/phone -> %d | %s%n%n", regPhone.statusCode(), regPhone.body());

        String verificationId = regPhone.body().path("verificationId").asText("");
        if (!verificationId.isBlank()) {
            ApiResponse regVerify = AUTH_CLIENT.verifyPhone(verificationId, ApiConfig.verificationCode());
            System.out.printf("POST /auth/register/verify -> %d | %s%n%n", regVerify.statusCode(), regVerify.body());

            ApiResponse regEmail = AUTH_CLIENT.registerEmail(verificationId, email, password);
            System.out.printf("POST /auth/register/email -> %d | %s%n%n", regEmail.statusCode(), regEmail.body());
        }

        ApiResponse login = AUTH_CLIENT.login(email, password);
        System.out.printf("POST /auth/login -> %d | %s%n%n", login.statusCode(), login.body());

        String accessToken = login.body().path("accessToken").asText("");
        if (accessToken.isBlank()) {
            accessToken = "fake-bearer-token";
        }

        ApiResponse profiles = BASE.send("GET", "/profiles", null, accessToken);
        System.out.printf("GET /profiles -> %d | %s%n%n", profiles.statusCode(), profiles.body());

        ApiResponse me = BASE.send("GET", "/profile/me", null, accessToken);
        System.out.printf("GET /profile/me -> %d | %s%n%n", me.statusCode(), me.body());

        var updateBody = Map.of(
                "name", "ProbeUser",
                "surname", "TestSurname",
                "city", "Moscow",
                "status", "active");
        ApiResponse update = BASE.send("PUT", "/profile/me", updateBody, accessToken);
        System.out.printf("PUT /profile/me -> %d | %s%n%n", update.statusCode(), update.body());

        var patchBody = Map.of("jobTitle", "Engineer");
        ApiResponse patch = BASE.send("PATCH", "/profile/me", patchBody, accessToken);
        System.out.printf("PATCH /profile/me -> %d | %s%n%n", patch.statusCode(), patch.body());

        ApiResponse privacy = BASE.send("GET", "/profile/me/privacy", null, accessToken);
        System.out.printf("GET /profile/me/privacy -> %d | %s%n%n", privacy.statusCode(), privacy.body());

        var privacyBody = Map.of(
                "showPhoneNumber", false,
                "showEmail", true,
                "showCity", true,
                "showPlaceOfWork", false,
                "showJobTitle", true,
                "showBirthday", false,
                "showPersonalInterests", true);
        ApiResponse privacyUpdate = BASE.send("PUT", "/profile/me/privacy", privacyBody, accessToken);
        System.out.printf("PUT /profile/me/privacy -> %d | %s%n%n", privacyUpdate.statusCode(), privacyUpdate.body());

        ApiResponse otherProfile = BASE.send("GET", "/profile/" + UUID.randomUUID(), null, accessToken);
        System.out.printf("GET /profile/{userId} (random) -> %d | %s%n%n", otherProfile.statusCode(), otherProfile.body());

        ApiResponse questions = BASE.send("GET", "/profile/onboarding/questions", null, accessToken);
        System.out.printf("GET /profile/onboarding/questions -> %d | %s%n%n", questions.statusCode(), questions.body());

        var completeBody = Map.of("responses", List.of());
        ApiResponse complete = BASE.send("POST", "/profile/onboarding/complete", completeBody, accessToken);
        System.out.printf("POST /profile/onboarding/complete -> %d | %s%n%n", complete.statusCode(), complete.body());

        ApiResponse meNoToken = BASE.send("GET", "/profile/me", null, null);
        System.out.printf("GET /profile/me (no token) -> %d | %s%n%n", meNoToken.statusCode(), meNoToken.body());

        ApiResponse meFakeToken = BASE.send("GET", "/profile/me", null, "invalid-token-abc");
        System.out.printf("GET /profile/me (fake token) -> %d | %s%n%n", meFakeToken.statusCode(), meFakeToken.body());

        ApiResponse deleteProfile = BASE.send("DELETE", "/profile/me", null, accessToken);
        System.out.printf("DELETE /profile/me -> %d | %s%n%n", deleteProfile.statusCode(), deleteProfile.body());

        System.out.println("=== PROBE FINISHED ===");
    }
}
