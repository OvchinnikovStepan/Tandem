package com.tandem.chat_service.service.exception;

import java.util.UUID;

public class GroupRequestNotFoundException extends RuntimeException {
    public GroupRequestNotFoundException(UUID id) {
        super(String.format("Group request not found with id: %s", id));
    }
}