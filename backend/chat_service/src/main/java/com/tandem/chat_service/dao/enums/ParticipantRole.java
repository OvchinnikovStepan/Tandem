package com.tandem.chat_service.dao.enums;

import lombok.Getter;

@Getter
public enum ParticipantRole {
    MEMBER("member"),
    ADMIN("admin");

    private final String value;

    ParticipantRole(String value) {
        this.value = value;
    }

    public static ParticipantRole fromValue(String value) {
        for (ParticipantRole role : ParticipantRole.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}