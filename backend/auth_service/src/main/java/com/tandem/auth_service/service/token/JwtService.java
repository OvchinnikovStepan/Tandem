package com.tandem.auth_service.service.token;

import java.util.UUID;

public interface JwtService {
    String generateAccessToken(UUID userId, UUID sessionId);
    boolean validateToken(String token);
    UUID extractUserId(String token);
    UUID extractSessionId(String token);
}
