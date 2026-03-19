package com.tandem.interest_service.service.exception;

import java.util.UUID;

public class UserInterestNotFoundException extends RuntimeException {
    public UserInterestNotFoundException(UUID userId, UUID tagId) {
        super(String.format("User interest not found for user: %s and tag: %s", userId, tagId));
    }
}