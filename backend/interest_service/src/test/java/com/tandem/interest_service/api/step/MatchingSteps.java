package com.tandem.interest_service.api.step;

import com.tandem.interest_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.interest_service.api.client.MatchingClient;
import io.qameta.allure.Step;

public class MatchingSteps {

    private final MatchingClient client;

    public MatchingSteps(MatchingClient client) {
        this.client = client;
    }

    @Step("Get matching users (limit={limit}, minMatchCount={minMatchCount})")
    public ApiResponse getMatchingUsers(Integer limit, Integer minMatchCount, String token) {
        return client.getMatchingUsers(limit, minMatchCount, token);
    }
}
