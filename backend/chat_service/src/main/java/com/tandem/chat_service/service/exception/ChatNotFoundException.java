package com.tandem.chat_service.service.exception;

import java.util.UUID;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(UUID id) {
        super(String.format("Chat not found with id: %s", id));
    }
}