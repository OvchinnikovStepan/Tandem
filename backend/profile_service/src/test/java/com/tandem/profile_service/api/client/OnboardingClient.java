package com.tandem.profile_service.api.client;

public class OnboardingClient {

    private final BaseApiClient base;

    public OnboardingClient(BaseApiClient base) {
        this.base = base;
    }

    public BaseApiClient.ApiResponse getOnboardingQuestions(String accessToken) {
        return base.send("GET", "/profile/onboarding/questions", null, accessToken);
    }

    public BaseApiClient.ApiResponse completeOnboarding(Object body, String accessToken) {
        return base.send("POST", "/profile/onboarding/complete", body, accessToken);
    }
}
