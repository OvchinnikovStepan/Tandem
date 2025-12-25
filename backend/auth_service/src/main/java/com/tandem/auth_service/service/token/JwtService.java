package com.tandem.auth_service.service.token;

import java.util.UUID;

public interface JwtService {
    String generateAccessToken(UUID userId, String email);
    boolean validateToken(String token);
    UUID extractUserId(String token);
    String extractEmail(String token);
}
