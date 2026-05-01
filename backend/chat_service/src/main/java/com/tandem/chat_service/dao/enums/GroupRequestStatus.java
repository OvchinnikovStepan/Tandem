package com.tandem.chat_service.dao.enums;

import lombok.Getter;

@Getter
public enum GroupRequestStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CANCELLED("cancelled");

    private final String value;

    GroupRequestStatus(String value) {
        this.value = value;
    }

    public static GroupRequestStatus fromValue(String value) {
        for (GroupRequestStatus status : GroupRequestStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}