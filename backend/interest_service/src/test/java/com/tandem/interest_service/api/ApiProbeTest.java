package com.tandem.interest_service.api;

import com.tandem.interest_service.api.client.AuthClient;
import com.tandem.interest_service.api.client.BaseApiClient;
import com.tandem.interest_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.interest_service.api.config.ApiConfig;
import com.tandem.interest_service.api.step.AuthSteps;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Smoke-проба всех endpoint-ов interest_service.
 * Не падает при отсутствии части ответов: задача — увидеть статус-коды
 * и форматы тел в одном прогоне.
 */
@Epic("API Probe")
class ApiProbeTest {

    private static final BaseApiClient BASE = new BaseApiClient();
    private static final BaseApiClient AUTH_BASE = new BaseApiClient(ApiConfig.authBaseUrl());
    private static final AuthClient AUTH_CLIENT = new AuthClient(AUTH_BASE);
    private static final AuthSteps AUTH_STEPS = new AuthSteps(AUTH_CLIENT);

    @Test
    void probe_all_interest_endpoints() {
        System.out.println("=== PROBING ALL INTEREST ENDPOINTS ===\n");

        AuthSteps.AuthResult auth = AUTH_STEPS.registerAndObtainToken("probe-interest");
        String accessToken = auth.accessToken();
        if (accessToken == null || accessToken.isBlank()) {
            accessToken = "fake-bearer-token";
            System.out.println("[WARN] Could not obtain real access token, using fake.\n");
        } else {
            System.out.printf("[OK] Got access token for userId=%s%n%n", auth.userId());
        }

        // ---- TagApi ----
        ApiResponse allTags = BASE.send("GET", "/interests/tags", null, accessToken);
        System.out.printf("GET /interests/tags -> %d | %s%n%n",
                allTags.statusCode(), allTags.body());

        ApiResponse defaultTags = BASE.send("GET", "/interests/tags/default", null, accessToken);
        System.out.printf("GET /interests/tags/default -> %d | %s%n%n",
                defaultTags.statusCode(), defaultTags.body());

        ApiResponse search = BASE.send("GET", "/interests/tags/search?search=t&limit=5", null, accessToken);
        System.out.printf("GET /interests/tags/search?search=t&limit=5 -> %d | %s%n%n",
                search.statusCode(), search.body());

        ApiResponse createTag = BASE.send("POST", "/interests/tag",
                Map.of("name", "probe-" + UUID.randomUUID()), accessToken);
        System.out.printf("POST /interests/tag -> %d | %s%n%n",
                createTag.statusCode(), createTag.body());

        String createdTagId = createTag.body().path("id").asText(null);
        if (createdTagId != null && !createdTagId.isBlank()) {
            ApiResponse getTag = BASE.send("GET", "/interests/tag/" + createdTagId, null, accessToken);
            System.out.printf("GET /interests/tag/{id} -> %d | %s%n%n",
                    getTag.statusCode(), getTag.body());

            ApiResponse updateTag = BASE.send("PUT", "/interests/tag/" + createdTagId,
                    Map.of("name", "probe-renamed-" + UUID.randomUUID()), accessToken);
            System.out.printf("PUT /interests/tag/{id} -> %d | %s%n%n",
                    updateTag.statusCode(), updateTag.body());

            // ---- UserInterestApi (POST/GET/DELETE /me) ----
            ApiResponse addMy = BASE.send("POST", "/interests/me",
                    Map.of("tagIds", List.of(createdTagId)), accessToken);
            System.out.printf("POST /interests/me -> %d | %s%n%n",
                    addMy.statusCode(), addMy.body());

            ApiResponse getMy = BASE.send("GET", "/interests/me", null, accessToken);
            System.out.printf("GET /interests/me -> %d | %s%n%n",
                    getMy.statusCode(), getMy.body());

            ApiResponse delMy = BASE.send("DELETE", "/interests/me",
                    Map.of("tagId", createdTagId), accessToken);
            System.out.printf("DELETE /interests/me -> %d | %s%n%n",
                    delMy.statusCode(), delMy.rawBody());

            ApiResponse delTag = BASE.send("DELETE", "/interests/tag/" + createdTagId, null, accessToken);
            System.out.printf("DELETE /interests/tag/{id} -> %d | %s%n%n",
                    delTag.statusCode(), delTag.rawBody());
        } else {
            System.out.println("[WARN] No tagId returned from POST /interests/tag, skipping dependent calls.\n");
        }

        // ---- MatchingApi ----
        ApiResponse matching = BASE.send("GET",
                "/interests/matching-users?limit=5&minMatchCount=1", null, accessToken);
        System.out.printf("GET /interests/matching-users -> %d | %s%n%n",
                matching.statusCode(), matching.body());

        // ---- Negative ----
        ApiResponse noToken = BASE.send("GET", "/interests/me", null, null);
        System.out.printf("GET /interests/me (no token) -> %d | %s%n%n",
                noToken.statusCode(), noToken.body());

        ApiResponse fakeToken = BASE.send("GET", "/interests/me", null, "definitely-not-a-jwt");
        System.out.printf("GET /interests/me (fake token) -> %d | %s%n%n",
                fakeToken.statusCode(), fakeToken.body());

        System.out.println("=== PROBE FINISHED ===");
    }
}
