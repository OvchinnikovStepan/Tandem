package com.tandem.profile_service.api.client;

public class PrivacyClient {

    private final BaseApiClient base;

    public PrivacyClient(BaseApiClient base) {
        this.base = base;
    }

    public BaseApiClient.ApiResponse getMyPrivacySettings(String accessToken) {
        return base.send("GET", "/profile/me/privacy", null, accessToken);
    }

    public BaseApiClient.ApiResponse updateMyPrivacySettings(Object body, String accessToken) {
        return base.send("PUT", "/profile/me/privacy", body, accessToken);
    }
}
