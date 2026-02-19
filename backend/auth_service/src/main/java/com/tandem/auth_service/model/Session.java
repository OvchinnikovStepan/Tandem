package com.tandem.auth_service.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tandem.auth_service.api.dto.SessionDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Session {

    private UUID id;
    private UUID userId;
    private String accessTokenHash;
    private String refreshTokenHash;
    private String deviceInfo;
    private String ipAddress;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;

    public SessionDto toDto() {
        return new SessionDto(
            this.getId(),
            this.getUserId(),
            this.getDeviceInfo(),
            this.getIpAddress(),
            this.getCreatedAt(),
            this.getExpiresAt()
        );
    }
}
