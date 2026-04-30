package com.tandem.profile_service.api.step;

import com.tandem.profile_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.profile_service.api.client.PrivacyClient;

import io.qameta.allure.Step;

public class PrivacySteps {

    private final PrivacyClient privacyClient;

    public PrivacySteps(PrivacyClient privacyClient) {
        this.privacyClient = privacyClient;
    }

    @Step("Get my privacy settings")
    public ApiResponse getMyPrivacySettings(String accessToken) {
        return privacyClient.getMyPrivacySettings(accessToken);
    }

    @Step("Update my privacy settings")
    public ApiResponse updateMyPrivacySettings(Object body, String accessToken) {
        return privacyClient.updateMyPrivacySettings(body, accessToken);
    }
}
