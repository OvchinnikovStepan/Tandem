package com.tandem.chat_service.dao.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MessageType {
    TEXT("text"),
    FILE("file"),
    LINK("link"),
    CODE("code"),
    EMOJI("emoji"),
    STICKER("sticker");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    // Эта аннотация указывает Jackson использовать это значение при конвертации в JSON (в ответ)
    @JsonValue
    public String getValue() {
        return value;
    }

    // Эта аннотация указывает Jackson использовать этот метод при чтении из JSON (из запроса)
    @JsonCreator
    public static MessageType fromValue(String value) {
        if (value == null) return null;
        for (MessageType type : MessageType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown message type: " + value);
    }
}