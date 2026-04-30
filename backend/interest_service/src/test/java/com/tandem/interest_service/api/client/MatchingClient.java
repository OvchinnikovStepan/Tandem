package com.tandem.interest_service.api.client;

import java.util.LinkedHashMap;
import java.util.Map;

/** Клиент для MatchingApi: /api/interests/matching-users. */
public class MatchingClient {

    private final BaseApiClient base;

    public MatchingClient(BaseApiClient base) {
        this.base = base;
    }

    public BaseApiClient.ApiResponse getMatchingUsers(Integer limit, Integer minMatchCount, String token) {
        Map<String, Object> qp = new LinkedHashMap<>();
        if (limit != null) qp.put("limit", limit);
        if (minMatchCount != null) qp.put("minMatchCount", minMatchCount);
        return base.get("/interests/matching-users", qp, token);
    }
}
