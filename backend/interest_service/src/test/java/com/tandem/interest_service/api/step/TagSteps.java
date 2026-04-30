package com.tandem.interest_service.api.step;

import com.tandem.interest_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.interest_service.api.client.TagClient;
import io.qameta.allure.Step;

import java.util.Map;
import java.util.UUID;

public class TagSteps {

    private final TagClient tagClient;

    public TagSteps(TagClient tagClient) {
        this.tagClient = tagClient;
    }

    @Step("Get all tags")
    public ApiResponse getAllTags(String token) {
        return tagClient.getAllTags(token);
    }

    @Step("Get default tags")
    public ApiResponse getDefaultTags(String token) {
        return tagClient.getDefaultTags(token);
    }

    @Step("Create tag with name: {name}")
    public ApiResponse createTag(String name, String token) {
        return tagClient.createTag(name, token);
    }

    @Step("Update tag {id}")
    public ApiResponse updateTag(UUID id, Map<String, Object> updates, String token) {
        return tagClient.updateTag(id, updates, token);
    }

    @Step("Delete tag {id}")
    public ApiResponse deleteTag(UUID id, String token) {
        return tagClient.deleteTag(id, token);
    }

    @Step("Get tag by id: {id}")
    public ApiResponse getTagById(UUID id, String token) {
        return tagClient.getTagById(id, token);
    }

    @Step("Search tags by prefix: {search} (limit={limit})")
    public ApiResponse searchTags(String search, Integer limit, String token) {
        return tagClient.searchTags(search, limit, token);
    }
}
