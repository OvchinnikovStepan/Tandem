package com.tandem.auth_service.api.dto.password;

public enum PasswordFeedback {

    TOO_SHORT("Password must be at least 8 characters"),

    HAS_UPPERCASE("Contains uppercase letters"),
    MISSING_UPPERCASE("Add uppercase letters"),

    HAS_LOWERCASE("Contains lowercase letters"),
    MISSING_LOWERCASE("Add lowercase letters"),

    HAS_NUMBER("Contains numbers"),
    MISSING_NUMBER("Add numbers"),

    HAS_SPECIAL("Contains special characters"),
    MISSING_SPECIAL("Add special characters"),

    COMMON_PASSWORD("Password is too common"),

    HAS_SEQUENTIAL("Avoid sequential characters"),
    HAS_REPEATED("Avoid repeated characters"),

    GOOD_ENTROPY("Good password randomness"),
    LOW_ENTROPY("Password is predictable");

    private final String message;

    PasswordFeedback(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}