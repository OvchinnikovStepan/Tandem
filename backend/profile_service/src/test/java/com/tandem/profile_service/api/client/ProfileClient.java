package com.tandem.profile_service.api.client;

import java.util.Map;

public class ProfileClient {

    private final BaseApiClient base;

    public ProfileClient(BaseApiClient base) {
        this.base = base;
    }

    public BaseApiClient.ApiResponse getAllProfiles(String accessToken) {
        return base.send("GET", "/profiles", null, accessToken);
    }

    public BaseApiClient.ApiResponse getMyProfile(String accessToken) {
        return base.send("GET", "/profile/me", null, accessToken);
    }

    public BaseApiClient.ApiResponse getProfileById(String userId, String accessToken) {
        return base.send("GET", "/profile/" + userId, null, accessToken);
    }

    public BaseApiClient.ApiResponse updateMyProfile(Object body, String accessToken) {
        return base.send("PUT", "/profile/me", body, accessToken);
    }

    public BaseApiClient.ApiResponse patchMyProfile(Object body, String accessToken) {
        return base.send("PATCH", "/profile/me", body, accessToken);
    }

    public BaseApiClient.ApiResponse deleteMyProfile(String accessToken) {
        return base.send("DELETE", "/profile/me", null, accessToken);
    }
}
