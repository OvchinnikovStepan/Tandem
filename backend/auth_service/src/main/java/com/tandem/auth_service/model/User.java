package com.tandem.auth_service.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tandem.auth_service.api.dto.UserDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class User {
    private UUID id;
    private String email;
    private String phoneNumber;
    private String passwordHash;
    private boolean emailVerified;
    private boolean phoneVerified;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public UserDto toDto()
    {
        return new UserDto(
            this.getId(),
            this.getEmail(),
            this.getPhoneNumber(),
            this.isEmailVerified(),
            this.isPhoneVerified(),
            this.getCreatedAt(),
            this.getLastLoginAt()
        );
    };
}
