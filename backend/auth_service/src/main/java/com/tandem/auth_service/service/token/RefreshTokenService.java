package com.tandem.auth_service.service.token;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RefreshTokenService {

    public String generateRefreshToken(UUID uuid) {
        return UUID.randomUUID().toString();
    }
}
