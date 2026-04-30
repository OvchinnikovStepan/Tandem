package com.tandem.profile_service.api.step;

import com.tandem.profile_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.profile_service.api.client.OnboardingClient;

import io.qameta.allure.Step;

public class OnboardingSteps {

    private final OnboardingClient onboardingClient;

    public OnboardingSteps(OnboardingClient onboardingClient) {
        this.onboardingClient = onboardingClient;
    }

    @Step("Get onboarding questions")
    public ApiResponse getOnboardingQuestions(String accessToken) {
        return onboardingClient.getOnboardingQuestions(accessToken);
    }

    @Step("Complete onboarding")
    public ApiResponse completeOnboarding(Object body, String accessToken) {
        return onboardingClient.completeOnboarding(body, accessToken);
    }
}
