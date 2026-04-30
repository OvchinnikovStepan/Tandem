package com.tandem.notification_service.service.model.request;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class NotificationCreateRequest {
    UUID userId;
    String type;
    String title;
    String body;
    Map<String, Object> data;
    String channel;
    LocalDateTime expiresAt;
}
