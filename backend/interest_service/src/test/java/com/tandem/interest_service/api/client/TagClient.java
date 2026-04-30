package com.tandem.interest_service.api.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Клиент для TagApi: /api/interests/tag*, /api/interests/tags*. */
public class TagClient {

    private final BaseApiClient base;

    public TagClient(BaseApiClient base) {
        this.base = base;
    }

    public BaseApiClient.ApiResponse getAllTags(String token) {
        return base.send("GET", "/interests/tags", null, token);
    }

    public BaseApiClient.ApiResponse getDefaultTags(String token) {
        return base.send("GET", "/interests/tags/default", null, token);
    }

    public BaseApiClient.ApiResponse createTag(String name, String token) {
        return base.send("POST", "/interests/tag", Map.of("name", name), token);
    }

    public BaseApiClient.ApiResponse updateTag(UUID id, Map<String, Object> updates, String token) {
        return base.send("PUT", "/interests/tag/" + id, updates, token);
    }

    public BaseApiClient.ApiResponse deleteTag(UUID id, String token) {
        return base.send("DELETE", "/interests/tag/" + id, null, token);
    }

    public BaseApiClient.ApiResponse getTagById(UUID id, String token) {
        return base.send("GET", "/interests/tag/" + id, null, token);
    }

    public BaseApiClient.ApiResponse searchTags(String search, Integer limit, String token) {
        Map<String, Object> qp = new LinkedHashMap<>();
        if (search != null) qp.put("search", search);
        if (limit != null) qp.put("limit", limit);
        return base.get("/interests/tags/search", qp, token);
    }
}
