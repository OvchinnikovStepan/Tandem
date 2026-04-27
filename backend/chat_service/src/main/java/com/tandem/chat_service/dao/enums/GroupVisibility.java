package com.tandem.chat_service.dao.enums;

import lombok.Getter;

@Getter
public enum GroupVisibility {
    PUBLIC("public"),
    PRIVATE("private");

    private final String value;

    GroupVisibility(String value) {
        this.value = value;
    }

    public static GroupVisibility fromValue(String value) {
        for (GroupVisibility visibility : GroupVisibility.values()) {
            if (visibility.value.equalsIgnoreCase(value)) {
                return visibility;
            }
        }
        throw new IllegalArgumentException("Unknown visibility: " + value);
    }
}