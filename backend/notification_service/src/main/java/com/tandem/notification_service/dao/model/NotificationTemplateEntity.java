package com.tandem.notification_service.dao.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class NotificationTemplateEntity {
    UUID id;
    String type;
    String titleTemplate;
    String bodyTemplate;
    String variables;
    List<String> channels;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
