package com.tandem.auth_service.api.client;

public class SessionClient {

    private final BaseApiClient base;

    public SessionClient(BaseApiClient base) {
        this.base = base;
    }

    public BaseApiClient.ApiResponse getSessions(String accessToken) {
        return base.send("GET", "/sessions", null, accessToken);
    }

    public BaseApiClient.ApiResponse deleteSession(String sessionId, String accessToken) {
        return base.send("DELETE", "/sessions/" + sessionId, null, accessToken);
    }
}
