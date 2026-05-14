package com.tandem.notification_service.service.model.response;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class NotificationItemResponse {
    UUID notificationId;
    String type;
    String title;
    String body;
    Map<String, Object> data;
    Boolean read;
    LocalDateTime readAt;
    String channel;
    LocalDateTime createdAt;
}
