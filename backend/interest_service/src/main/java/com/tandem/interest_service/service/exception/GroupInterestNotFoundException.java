package com.tandem.interest_service.service.exception;

import java.util.UUID;

public class GroupInterestNotFoundException extends RuntimeException {
    public GroupInterestNotFoundException(UUID groupId, UUID tagId) {
        super(String.format("Group interest not found for group: %s and tag: %s", groupId, tagId));
    }
}
