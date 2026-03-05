package com.tandem.auth_service.service.session;

import com.tandem.auth_service.api.dto.SessionDto;
import com.tandem.auth_service.api.dto.auth.common.RefreshResultDto;

import java.util.List;
import java.util.UUID;

public interface SessionService {

    void createSession(
        UUID sessionId,
        UUID userId,
        String accessToken,
        String refreshToken,
        String ipAddress,
        String deviceInfo
    );

    void logout(UUID sessionId);

    RefreshResultDto refresh(String refreshToken);

    List<SessionDto> getActiveSessions(UUID userId);

    void terminateSession(UUID userId, UUID sessionId);

    Boolean isSessionActive(UUID sessionId);
}
