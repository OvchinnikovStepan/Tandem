package com.tandem.interest_service.api.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Клиент для UserInterestApi: /api/interests/me. */
public class UserInterestClient {

    private final BaseApiClient base;

    public UserInterestClient(BaseApiClient base) {
        this.base = base;
    }

    public BaseApiClient.ApiResponse getMyInterests(String token) {
        return base.send("GET", "/interests/me", null, token);
    }

    public BaseApiClient.ApiResponse addMyInterests(List<UUID> tagIds, String token) {
        return base.send("POST", "/interests/me", Map.of("tagIds", tagIds), token);
    }

    public BaseApiClient.ApiResponse deleteMyInterest(UUID tagId, String token) {
        return base.send("DELETE", "/interests/me", Map.of("tagId", tagId), token);
    }
}
