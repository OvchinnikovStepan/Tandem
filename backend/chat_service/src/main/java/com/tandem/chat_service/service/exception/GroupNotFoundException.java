package com.tandem.chat_service.service.exception;

import java.util.UUID;

public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException(UUID id) {
        super(String.format("Group not found with id: %s", id));
    }

    public GroupNotFoundException(String name) {
        super(String.format("Group not found with name: '%s'", name));
    }
}
