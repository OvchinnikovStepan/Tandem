package com.tandem.profile_service.exception;

public class DataPersistenceException extends RuntimeException {
    public DataPersistenceException(String message) {
        super(message);
    }

    public DataPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
