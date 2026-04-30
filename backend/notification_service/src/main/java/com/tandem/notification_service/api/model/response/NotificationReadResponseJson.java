package com.tandem.notification_service.api.model.response;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class NotificationReadResponseJson {
    Boolean read;
    LocalDateTime readAt;
}
