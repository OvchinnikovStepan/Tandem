package com.tandem.notification_service.dao.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class NotificationEntity {
    UUID id;
    UUID userId;
    String type;
    String body;
    String data;
    String channel;
    String title;
    Boolean read;
    LocalDateTime readAt;
    LocalDateTime deliveredAt;
    LocalDateTime createdAt;
    LocalDateTime expiresAt;
    LocalDateTime archivedAt;
}
