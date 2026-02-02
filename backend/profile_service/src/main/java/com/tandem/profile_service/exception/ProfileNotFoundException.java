package com.tandem.profile_service.exception;

import java.util.UUID;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(UUID userId) {
        super("Profile not found for user: " + userId);
    }
}
