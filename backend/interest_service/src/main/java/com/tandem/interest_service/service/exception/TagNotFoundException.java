package com.tandem.interest_service.service.exception;

import java.util.UUID;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(UUID id) {
        super("Tag not found with id: " + id);
    }
}