package com.tandem.notification_service.service.model.response;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class NotificationReadResponse {
    Boolean read;
    LocalDateTime readAt;
}
