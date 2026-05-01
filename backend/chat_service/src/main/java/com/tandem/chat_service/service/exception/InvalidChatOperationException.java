package com.tandem.chat_service.service.exception;

public class InvalidChatOperationException extends RuntimeException {
    public InvalidChatOperationException(String message) {
        super(message);
    }
}
