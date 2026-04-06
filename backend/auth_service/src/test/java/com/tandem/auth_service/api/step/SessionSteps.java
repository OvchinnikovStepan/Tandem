package com.tandem.auth_service.api.step;

import com.tandem.auth_service.api.client.BaseApiClient.ApiResponse;
import com.tandem.auth_service.api.client.SessionClient;

import io.qameta.allure.Step;

public class SessionSteps {

    private final SessionClient sessionClient;

    public SessionSteps(SessionClient sessionClient) {
        this.sessionClient = sessionClient;
    }

    @Step("Get active sessions")
    public ApiResponse getSessions(String accessToken) {
        return sessionClient.getSessions(accessToken);
    }

    @Step("Delete session: {sessionId}")
    public ApiResponse deleteSession(String sessionId, String accessToken) {
        return sessionClient.deleteSession(sessionId, accessToken);
    }
}
