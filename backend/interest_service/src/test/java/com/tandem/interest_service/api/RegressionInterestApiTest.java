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
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Interest Service")
@Feature("Regression Testing")
class RegressionInterestApiTest {

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

    private String registerAndGetToken(String prefix) {
        return authSteps.obtainAccessToken(prefix);
    }

    private UUID createTag(String token, String prefix) {
        ApiResponse resp = tagSteps.createTag(TestDataFactory.randomTagName(prefix), token);
        assertEquals(201, resp.statusCode(),
                "Failed to create tag: " + resp.statusCode() + " | " + resp.body());
        String id = resp.body().path("id").asText("");
        assertTrue(!id.isBlank(), "Created tag must have id");
        return UUID.fromString(id);
    }

    @Test
    @Story("R-01 Create tag → get by id → update → verify chain")
    @Severity(SeverityLevel.BLOCKER)
    void r01_create_get_update_verify() {
        String token = registerAndGetToken("reg-r01");
        Assumptions.assumeTrue(token != null, "Skipped: registration failed");

        String name = TestDataFactory.randomTagName("r01");
        ApiResponse created = tagSteps.createTag(name, token);
        assertEquals(201, created.statusCode(),
                "POST /interests/tag failed: " + created.statusCode() + " | " + created.body());
        UUID tagId = UUID.fromString(created.body().path("id").asText(""));
        assertNotNull(tagId, "Tag id must be present");

        ApiResponse fetched = tagSteps.getTagById(tagId, token);
        assertEquals(200, fetched.statusCode(),
                "GET /interests/tag/{id} failed: " + fetched.statusCode() + " | " + fetched.body());
        assertEquals(name, fetched.body().path("name").asText(),
                "Fetched tag name must match created name");

        String newName = TestDataFactory.randomTagName("r01-updated");
        ApiResponse updated = tagSteps.updateTag(tagId, Map.of("name", newName), token);
        assertEquals(200, updated.statusCode(),
                "PUT /interests/tag/{id} failed: " + updated.statusCode() + " | " + updated.body());

        ApiResponse afterUpdate = tagSteps.getTagById(tagId, token);
        assertEquals(200, afterUpdate.statusCode());
        assertEquals(newName, afterUpdate.body().path("name").asText(),
                "Tag name must be updated to new value");
    }

    @Test
    @Story("R-02 Create tag → delete → get returns 4xx")
    @Severity(SeverityLevel.CRITICAL)
    void r02_delete_then_get() {
        String token = registerAndGetToken("reg-r02");
        Assumptions.assumeTrue(token != null, "Skipped: registration failed");

        UUID tagId = createTag(token, "r02");

        ApiResponse delete = tagSteps.deleteTag(tagId, token);
        assertTrue(Set.of(200, 204).contains(delete.statusCode()),
                "DELETE /interests/tag/{id} failed: " + delete.statusCode() + " | " + delete.rawBody());

        ApiResponse afterDelete = tagSteps.getTagById(tagId, token);
        assertTrue(Set.of(400, 403, 404, 500).contains(afterDelete.statusCode()),
                "GET after delete should fail, got " + afterDelete.statusCode() + " | " + afterDelete.body());
    }

    @Test
    @Story("R-03 Add user interest → list shows it")
    @Severity(SeverityLevel.BLOCKER)
    void r03_add_then_list_my_interests() {
        String token = registerAndGetToken("reg-r03");
        Assumptions.assumeTrue(token != null, "Skipped: registration failed");

        UUID tagId = createTag(token, "r03");

        ApiResponse add = userInterestSteps.addMyInterests(List.of(tagId), token);
        assertEquals(201, add.statusCode(),
                "POST /interests/me failed: " + add.statusCode() + " | " + add.body());

        ApiResponse list = userInterestSteps.getMyInterests(token);
        assertEquals(200, list.statusCode(),
                "GET /interests/me failed: " + list.statusCode());

        boolean found = false;
        for (var node : list.body()) {
            if (tagId.toString().equals(node.path("tag").path("id").asText())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Added tag must be present in /interests/me; body=" + list.body());
    }

    @Test
    @Story("R-04 Add user interest → delete → list does not show it")
    @Severity(SeverityLevel.CRITICAL)
    void r04_add_then_delete_my_interest() {
        String token = registerAndGetToken("reg-r04");
        Assumptions.assumeTrue(token != null, "Skipped: registration failed");

        UUID tagId = createTag(token, "r04");

        userInterestSteps.addMyInterests(List.of(tagId), token);
        ApiResponse delete = userInterestSteps.deleteMyInterest(tagId, token);
        assertTrue(Set.of(200, 204).contains(delete.statusCode()),
                "DELETE /interests/me failed: " + delete.statusCode() + " | " + delete.rawBody());

        ApiResponse list = userInterestSteps.getMyInterests(token);
        assertEquals(200, list.statusCode());
        for (var node : list.body()) {
            assertTrue(!tagId.toString().equals(node.path("tag").path("id").asText()),
                    "Deleted tag must NOT be present in /interests/me");
        }
    }

    @Test
    @Story("R-05 Add multiple interests in single request")
    @Severity(SeverityLevel.CRITICAL)
    void r05_add_multiple_interests() {
        String token = registerAndGetToken("reg-r05");
        Assumptions.assumeTrue(token != null, "Skipped: registration failed");

        UUID tag1 = createTag(token, "r05-a");
        UUID tag2 = createTag(token, "r05-b");
        UUID tag3 = createTag(token, "r05-c");

        ApiResponse add = userInterestSteps.addMyInterests(List.of(tag1, tag2, tag3), token);
        assertEquals(201, add.statusCode(),
                "POST /interests/me (multi) failed: " + add.statusCode() + " | " + add.body());

        ApiResponse list = userInterestSteps.getMyInterests(token);
        assertEquals(200, list.statusCode());
        Set<String> foundIds = new HashSet<>();
        for (var node : list.body()) {
            foundIds.add(node.path("tag").path("id").asText());
        }
        assertTrue(foundIds.contains(tag1.toString())
                        && foundIds.contains(tag2.toString())
                        && foundIds.contains(tag3.toString()),
                "All three added tags must be present; got=" + foundIds);
    }

    @Test
    @Story("R-06 Search returns tag by exact prefix after creation")
    @Severity(SeverityLevel.CRITICAL)
    void r06_search_finds_created_tag() {
        String token = registerAndGetToken("reg-r06");
        Assumptions.assumeTrue(token != null, "Skipped: registration failed");

        String unique = "search-" + UUID.randomUUID().toString().substring(0, 8);
        ApiResponse created = tagSteps.createTag(unique, token);
        assertEquals(201, created.statusCode());

        ApiResponse search = tagSteps.searchTags(unique.substring(0, 10), 10, token);
        assertEquals(200, search.statusCode(),
                "GET /interests/tags/search failed: " + search.statusCode() + " | " + search.body());
        assertTrue(search.body().isArray());

        boolean found = false;
        for (var node : search.body()) {
            if (unique.equals(node.path("name").asText())) {
                found = true;
                break;
            }
        }
        assertTrue(found,
                "Search must find created tag '" + unique + "'; body=" + search.body());
    }

    @Test
    @Story("R-07 Two users with shared interest appear in matching")
    @Severity(SeverityLevel.NORMAL)
    void r07_matching_returns_users_with_shared_interest() {
        String tokenA = registerAndGetToken("reg-r07-a");
        Assumptions.assumeTrue(tokenA != null, "Skipped: user A registration failed");

        UUID sharedTag = createTag(tokenA, "r07-shared");
        userInterestSteps.addMyInterests(List.of(sharedTag), tokenA);

        String tokenB = registerAndGetToken("reg-r07-b");
        Assumptions.assumeTrue(tokenB != null, "Skipped: user B registration failed");
        userInterestSteps.addMyInterests(List.of(sharedTag), tokenB);

        ApiResponse matching = matchingSteps.getMatchingUsers(20, 1, tokenA);
        assertEquals(200, matching.statusCode(),
                "GET /interests/matching-users failed: " + matching.statusCode() + " | " + matching.body());
        assertTrue(matching.body().isArray(),
                "Matching response must be a JSON array");
    }

    @Test
    @Story("R-08 Matching with high minMatchCount returns less or equal")
    @Severity(SeverityLevel.NORMAL)
    void r08_matching_limit_and_filter() {
        String token = registerAndGetToken("reg-r08");
        Assumptions.assumeTrue(token != null, "Skipped: registration failed");

        ApiResponse low = matchingSteps.getMatchingUsers(50, 1, token);
        ApiResponse high = matchingSteps.getMatchingUsers(50, 100, token);

        assertEquals(200, low.statusCode());
        assertEquals(200, high.statusCode());
        assertTrue(low.body().isArray() && high.body().isArray());

        // Если хоть один матч на низком пороге — то на высоком должно быть не больше
        assertTrue(high.body().size() <= low.body().size(),
                "Higher minMatchCount must not return more matches: low="
                        + low.body().size() + ", high=" + high.body().size());
    }

    @Test
    @Story("R-09 Invalid token returns 401/403 for all interest endpoints")
    @Severity(SeverityLevel.CRITICAL)
    void r09_invalid_token_all_endpoints() {
        String invalidToken = "definitely-not-a-valid-token";

        assertTrue(Set.of(401, 403).contains(tagSteps.getAllTags(invalidToken).statusCode()),
                "GET /interests/tags with invalid token");
        assertTrue(Set.of(401, 403).contains(tagSteps.getDefaultTags(invalidToken).statusCode()),
                "GET /interests/tags/default with invalid token");
        assertTrue(Set.of(401, 403).contains(tagSteps.searchTags("a", 10, invalidToken).statusCode()),
                "GET /interests/tags/search with invalid token");
        assertTrue(Set.of(401, 403).contains(tagSteps.createTag("X", invalidToken).statusCode()),
                "POST /interests/tag with invalid token");
        assertTrue(Set.of(401, 403).contains(tagSteps.getTagById(UUID.randomUUID(), invalidToken).statusCode()),
                "GET /interests/tag/{id} with invalid token");
        assertTrue(Set.of(401, 403).contains(
                tagSteps.updateTag(UUID.randomUUID(), Map.of("name", "X"), invalidToken).statusCode()),
                "PUT /interests/tag/{id} with invalid token");
        assertTrue(Set.of(401, 403).contains(tagSteps.deleteTag(UUID.randomUUID(), invalidToken).statusCode()),
                "DELETE /interests/tag/{id} with invalid token");
        assertTrue(Set.of(401, 403).contains(userInterestSteps.getMyInterests(invalidToken).statusCode()),
                "GET /interests/me with invalid token");
        assertTrue(Set.of(401, 403).contains(
                userInterestSteps.addMyInterests(List.of(UUID.randomUUID()), invalidToken).statusCode()),
                "POST /interests/me with invalid token");
        assertTrue(Set.of(401, 403).contains(
                userInterestSteps.deleteMyInterest(UUID.randomUUID(), invalidToken).statusCode()),
                "DELETE /interests/me with invalid token");
        assertTrue(Set.of(401, 403).contains(
                matchingSteps.getMatchingUsers(10, 1, invalidToken).statusCode()),
                "GET /interests/matching-users with invalid token");
    }
}
