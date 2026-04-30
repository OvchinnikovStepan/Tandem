package com.tandem.interest_service.api.step;

import com.tandem.interest_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.interest_service.api.client.UserInterestClient;
import io.qameta.allure.Step;

import java.util.List;
import java.util.UUID;

public class UserInterestSteps {

    private final UserInterestClient client;

    public UserInterestSteps(UserInterestClient client) {
        this.client = client;
    }

    @Step("Get my interests")
    public ApiResponse getMyInterests(String token) {
        return client.getMyInterests(token);
    }

    @Step("Add my interests: {tagIds}")
    public ApiResponse addMyInterests(List<UUID> tagIds, String token) {
        return client.addMyInterests(tagIds, token);
    }

    @Step("Delete my interest: {tagId}")
    public ApiResponse deleteMyInterest(UUID tagId, String token) {
        return client.deleteMyInterest(tagId, token);
    }
}
