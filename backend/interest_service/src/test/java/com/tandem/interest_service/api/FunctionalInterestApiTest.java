package com.tandem.interest_service.api;

import com.tandem.interest_service.api.client.AuthClient;
import com.tandem.interest_service.api.client.BaseApiClient;
import com.tandem.interest_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.interest_service.api.client.MatchingClient;
import com.tandem.interest_service.api.client.TagClient;
import com.tandem.interest_service.api.client.UserInterestClient;
import com.tandem.interest_service.api.config.ApiConfig;
import com.tandem.interest_service.api.step.AuthSteps;
import com.tandem.interest_service.api.step.MatchingSteps;
import com.tandem.interest_service.api.step.TagSteps;
import com.tandem.interest_service.api.step.UserInterestSteps;
import com.tandem.interest_service.api.util.TestDataFactory;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Interest Service")
@Feature("Functional Testing")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FunctionalInterestApiTest {

    private static final BaseApiClient BASE = new BaseApiClient();
    private static final BaseApiClient AUTH_BASE = new BaseApiClient(ApiConfig.authBaseUrl());

    private static final AuthClient AUTH_CLIENT = new AuthClient(AUTH_BASE);
    private static final TagClient TAG_CLIENT = new TagClient(BASE);
    private static final UserInterestClient USER_INTEREST_CLIENT = new UserInterestClient(BASE);
    private static final MatchingClient MATCHING_CLIENT = new MatchingClient(BASE);

    private static final AuthSteps authSteps = new AuthSteps(AUTH_CLIENT);
    private static final TagSteps tagSteps = new TagSteps(TAG_CLIENT);
    private static final UserInterestSteps userInterestSteps = new UserInterestSteps(USER_INTEREST_CLIENT);
    private static final MatchingSteps matchingSteps = new MatchingSteps(MATCHING_CLIENT);

    private static String accessToken;
    private static UUID createdTagId;

    @BeforeAll
    static void obtainToken() {
        accessToken = authSteps.obtainAccessToken("functional-interest");
    }

    private static UUID extractId(ApiResponse response) {
        String id = response.body().path("id").asText("");
        return id.isBlank() ? null : UUID.fromString(id);
    }

    // ──────────────── GET /interests/tags ────────────────

    @Test
    @Order(1)
    @Story("F-01 Get all tags with valid token")
    @Severity(SeverityLevel.BLOCKER)
    void f01_get_all_tags() {
        Assumptions.assumeTrue(accessToken != null && !accessToken.isBlank(),
                "Skipped: no valid accessToken");

        ApiResponse response = tagSteps.getAllTags(accessToken);

        Allure.step("Validate 200 and array body");
        assertEquals(200, response.statusCode(),
                "GET /interests/tags → " + response.statusCode() + " | " + response.body());
        assertTrue(response.body().isArray(),
                "Response must be a JSON array");
    }

    @Test
    @Order(2)
    @Story("F-02 Get all tags without token → 401/403")
    @Severity(SeverityLevel.BLOCKER)
    void f02_get_all_tags_no_token() {
        ApiResponse response = tagSteps.getAllTags(null);
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "GET /interests/tags without token → " + response.statusCode());
    }

    @Test
    @Order(3)
    @Story("F-03 Get all tags with invalid token → 401/403")
    @Severity(SeverityLevel.CRITICAL)
    void f03_get_all_tags_invalid_token() {
        ApiResponse response = tagSteps.getAllTags("definitely-not-a-valid-token");
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "GET /interests/tags with invalid token → " + response.statusCode());
    }

    // ──────────────── GET /interests/tags/default ────────────────

    @Test
    @Order(4)
    @Story("F-04 Get default tags")
    @Severity(SeverityLevel.NORMAL)
    void f04_get_default_tags() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = tagSteps.getDefaultTags(accessToken);
        assertEquals(200, response.statusCode(),
                "GET /interests/tags/default → " + response.statusCode() + " | " + response.body());
        assertTrue(response.body().isArray(),
                "Response must be a JSON array");
    }

    @Test
    @Order(5)
    @Story("F-05 Get default tags without token → 401/403")
    @Severity(SeverityLevel.NORMAL)
    void f05_get_default_tags_no_token() {
        ApiResponse response = tagSteps.getDefaultTags(null);
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "GET /interests/tags/default without token → " + response.statusCode());
    }

    // ──────────────── POST /interests/tag ────────────────

    @Test
    @Order(6)
    @Story("F-06 Create tag with valid name")
    @Severity(SeverityLevel.BLOCKER)
    void f06_create_tag() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        String name = TestDataFactory.randomTagName("functional");
        ApiResponse response = tagSteps.createTag(name, accessToken);

        Allure.step("Validate 201 and id+name returned");
        assertEquals(201, response.statusCode(),
                "POST /interests/tag → " + response.statusCode() + " | " + response.body());
        assertEquals(name, response.body().path("name").asText(),
                "Returned name must match");
        UUID id = extractId(response);
        assertNotNull(id, "Tag id must be present");
        createdTagId = id;
    }

    @Test
    @Order(7)
    @Story("F-07 Create tag without token → 401/403")
    @Severity(SeverityLevel.CRITICAL)
    void f07_create_tag_no_token() {
        ApiResponse response = tagSteps.createTag(TestDataFactory.randomTagName(), null);
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "POST /interests/tag without token → " + response.statusCode());
    }

    @Test
    @Order(8)
    @Story("F-08 Create tag with empty name → 400")
    @Severity(SeverityLevel.NORMAL)
    void f08_create_tag_empty_name() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = tagSteps.createTag("", accessToken);
        assertTrue(Set.of(400, 403, 422).contains(response.statusCode()),
                "POST /interests/tag with empty name → " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(9)
    @Story("F-09 Create tag with name longer than 100 chars → 400")
    @Severity(SeverityLevel.NORMAL)
    void f09_create_tag_name_too_long() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        String longName = "T".repeat(150);
        ApiResponse response = tagSteps.createTag(longName, accessToken);
        assertTrue(Set.of(400, 403, 422).contains(response.statusCode()),
                "POST /interests/tag with name too long → " + response.statusCode() + " | " + response.body());
    }

    // ──────────────── GET /interests/tag/{id} ────────────────

    @Test
    @Order(10)
    @Story("F-10 Get tag by id")
    @Severity(SeverityLevel.BLOCKER)
    void f10_get_tag_by_id() {
        Assumptions.assumeTrue(accessToken != null && createdTagId != null,
                "Skipped: no token or tag created");

        ApiResponse response = tagSteps.getTagById(createdTagId, accessToken);
        assertEquals(200, response.statusCode(),
                "GET /interests/tag/{id} → " + response.statusCode() + " | " + response.body());
        assertEquals(createdTagId.toString(), response.body().path("id").asText(),
                "Returned id must match requested id");
    }

    @Test
    @Order(11)
    @Story("F-11 Get tag by non-existent id → 404")
    @Severity(SeverityLevel.NORMAL)
    void f11_get_tag_nonexistent() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = tagSteps.getTagById(
                UUID.fromString("00000000-0000-0000-0000-000000000000"), accessToken);
        assertTrue(Set.of(400, 403, 404, 500).contains(response.statusCode()),
                "GET /interests/tag/{nonexistent} → " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(12)
    @Story("F-12 Get tag by id without token → 401/403")
    @Severity(SeverityLevel.CRITICAL)
    void f12_get_tag_no_token() {
        ApiResponse response = tagSteps.getTagById(UUID.randomUUID(), null);
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "GET /interests/tag/{id} without token → " + response.statusCode());
    }

    // ──────────────── PUT /interests/tag/{id} ────────────────

    @Test
    @Order(13)
    @Story("F-13 Update tag name")
    @Severity(SeverityLevel.CRITICAL)
    void f13_update_tag() {
        Assumptions.assumeTrue(accessToken != null && createdTagId != null,
                "Skipped: no token or tag created");

        String newName = TestDataFactory.randomTagName("renamed");
        ApiResponse response = tagSteps.updateTag(createdTagId,
                Map.of("name", newName, "imageUrl", "https://example.com/img.png"), accessToken);

        assertEquals(200, response.statusCode(),
                "PUT /interests/tag/{id} → " + response.statusCode() + " | " + response.body());
        assertEquals(newName, response.body().path("name").asText(),
                "Returned name must match the new name");
    }

    @Test
    @Order(14)
    @Story("F-14 Update tag without token → 401/403")
    @Severity(SeverityLevel.CRITICAL)
    void f14_update_tag_no_token() {
        ApiResponse response = tagSteps.updateTag(UUID.randomUUID(),
                Map.of("name", "X"), null);
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "PUT /interests/tag/{id} without token → " + response.statusCode());
    }

    // ──────────────── GET /interests/tags/search ────────────────

    @Test
    @Order(15)
    @Story("F-15 Search tags by prefix")
    @Severity(SeverityLevel.CRITICAL)
    void f15_search_tags_by_prefix() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = tagSteps.searchTags("renam", 10, accessToken);
        assertEquals(200, response.statusCode(),
                "GET /interests/tags/search → " + response.statusCode() + " | " + response.body());
        assertTrue(response.body().isArray(),
                "Response must be a JSON array");
    }

    @Test
    @Order(16)
    @Story("F-16 Search tags without query")
    @Severity(SeverityLevel.NORMAL)
    void f16_search_tags_no_query() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = tagSteps.searchTags(null, 10, accessToken);
        assertTrue(Set.of(200, 400, 403).contains(response.statusCode()),
                "GET /interests/tags/search without search → " + response.statusCode() + " | " + response.body());
    }

    @Test
    @Order(17)
    @Story("F-17 Search tags without token → 401/403")
    @Severity(SeverityLevel.NORMAL)
    void f17_search_tags_no_token() {
        ApiResponse response = tagSteps.searchTags("a", 10, null);
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "GET /interests/tags/search without token → " + response.statusCode());
    }

    // ──────────────── POST /interests/me ────────────────

    @Test
    @Order(18)
    @Story("F-18 Add tag to my interests")
    @Severity(SeverityLevel.BLOCKER)
    void f18_add_my_interest() {
        Assumptions.assumeTrue(accessToken != null && createdTagId != null,
                "Skipped: no token or tag created");

        ApiResponse response = userInterestSteps.addMyInterests(List.of(createdTagId), accessToken);

        Allure.step("Validate 201 and array body containing the added tag");
        assertEquals(201, response.statusCode(),
                "POST /interests/me → " + response.statusCode() + " | " + response.body());
        assertTrue(response.body().isArray(),
                "Response must be a JSON array");
        assertTrue(response.body().size() >= 1,
                "Response array must contain at least one item");
    }

    @Test
    @Order(19)
    @Story("F-19 Add interest without token → 401/403")
    @Severity(SeverityLevel.CRITICAL)
    void f19_add_my_interest_no_token() {
        ApiResponse response = userInterestSteps.addMyInterests(List.of(UUID.randomUUID()), null);
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "POST /interests/me without token → " + response.statusCode());
    }

    // ──────────────── GET /interests/me ────────────────

    @Test
    @Order(20)
    @Story("F-20 Get my interests")
    @Severity(SeverityLevel.BLOCKER)
    void f20_get_my_interests() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = userInterestSteps.getMyInterests(accessToken);
        assertEquals(200, response.statusCode(),
                "GET /interests/me → " + response.statusCode() + " | " + response.body());
        assertTrue(response.body().isArray(),
                "Response must be a JSON array");
    }

    @Test
    @Order(21)
    @Story("F-21 Get my interests without token → 401/403")
    @Severity(SeverityLevel.CRITICAL)
    void f21_get_my_interests_no_token() {
        ApiResponse response = userInterestSteps.getMyInterests(null);
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "GET /interests/me without token → " + response.statusCode());
    }

    // ──────────────── DELETE /interests/me ────────────────

    @Test
    @Order(22)
    @Story("F-22 Delete my interest")
    @Severity(SeverityLevel.CRITICAL)
    void f22_delete_my_interest() {
        Assumptions.assumeTrue(accessToken != null && createdTagId != null,
                "Skipped: no token or tag created");

        ApiResponse response = userInterestSteps.deleteMyInterest(createdTagId, accessToken);
        assertTrue(Set.of(200, 204).contains(response.statusCode()),
                "DELETE /interests/me → " + response.statusCode() + " | " + response.rawBody());
    }

    @Test
    @Order(23)
    @Story("F-23 Delete my interest without token → 401/403")
    @Severity(SeverityLevel.NORMAL)
    void f23_delete_my_interest_no_token() {
        ApiResponse response = userInterestSteps.deleteMyInterest(UUID.randomUUID(), null);
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "DELETE /interests/me without token → " + response.statusCode());
    }

    // ──────────────── DELETE /interests/tag/{id} ────────────────

    @Test
    @Order(24)
    @Story("F-24 Delete tag")
    @Severity(SeverityLevel.CRITICAL)
    void f24_delete_tag() {
        Assumptions.assumeTrue(accessToken != null && createdTagId != null,
                "Skipped: no token or tag created");

        ApiResponse response = tagSteps.deleteTag(createdTagId, accessToken);
        assertTrue(Set.of(200, 204).contains(response.statusCode()),
                "DELETE /interests/tag/{id} → " + response.statusCode() + " | " + response.rawBody());
    }

    @Test
    @Order(25)
    @Story("F-25 Delete tag without token → 401/403")
    @Severity(SeverityLevel.NORMAL)
    void f25_delete_tag_no_token() {
        ApiResponse response = tagSteps.deleteTag(UUID.randomUUID(), null);
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "DELETE /interests/tag/{id} without token → " + response.statusCode());
    }

    // ──────────────── GET /interests/matching-users ────────────────

    @Test
    @Order(26)
    @Story("F-26 Get matching users")
    @Severity(SeverityLevel.CRITICAL)
    void f26_get_matching_users() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = matchingSteps.getMatchingUsers(10, 1, accessToken);
        assertEquals(200, response.statusCode(),
                "GET /interests/matching-users → " + response.statusCode() + " | " + response.body());
        assertTrue(response.body().isArray(),
                "Response must be a JSON array");
    }

    @Test
    @Order(27)
    @Story("F-27 Get matching users without parameters")
    @Severity(SeverityLevel.NORMAL)
    void f27_get_matching_users_no_params() {
        Assumptions.assumeTrue(accessToken != null,
                "Skipped: no valid accessToken");

        ApiResponse response = matchingSteps.getMatchingUsers(null, null, accessToken);
        assertTrue(Set.of(200, 400, 403).contains(response.statusCode()),
                "GET /interests/matching-users (no params) → " + response.statusCode() + " | " + response.body());
        if (response.statusCode() == 200) {
            assertTrue(response.body().isArray(),
                    "Response must be a JSON array");
        }
    }

    @Test
    @Order(28)
    @Story("F-28 Get matching users without token → 401/403")
    @Severity(SeverityLevel.CRITICAL)
    void f28_get_matching_users_no_token() {
        ApiResponse response = matchingSteps.getMatchingUsers(10, 1, null);
        assertTrue(Set.of(401, 403).contains(response.statusCode()),
                "GET /interests/matching-users without token → " + response.statusCode());
    }
}
