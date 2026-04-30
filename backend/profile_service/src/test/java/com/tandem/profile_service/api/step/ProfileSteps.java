package com.tandem.profile_service.api.step;

import com.tandem.profile_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.profile_service.api.client.ProfileClient;

import io.qameta.allure.Step;

public class ProfileSteps {

    private final ProfileClient profileClient;

    public ProfileSteps(ProfileClient profileClient) {
        this.profileClient = profileClient;
    }

    @Step("Get all profiles")
    public ApiResponse getAllProfiles(String accessToken) {
        return profileClient.getAllProfiles(accessToken);
    }

    @Step("Get my profile")
    public ApiResponse getMyProfile(String accessToken) {
        return profileClient.getMyProfile(accessToken);
    }

    @Step("Get profile by userId: {userId}")
    public ApiResponse getProfileById(String userId, String accessToken) {
        return profileClient.getProfileById(userId, accessToken);
    }

    @Step("Update my profile (full)")
    public ApiResponse updateMyProfile(Object body, String accessToken) {
        return profileClient.updateMyProfile(body, accessToken);
    }

    @Step("Patch my profile (partial)")
    public ApiResponse patchMyProfile(Object body, String accessToken) {
        return profileClient.patchMyProfile(body, accessToken);
    }

    @Step("Delete my profile")
    public ApiResponse deleteMyProfile(String accessToken) {
        return profileClient.deleteMyProfile(accessToken);
    }
}
