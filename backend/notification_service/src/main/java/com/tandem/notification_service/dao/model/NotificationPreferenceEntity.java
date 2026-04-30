package com.tandem.notification_service.dao.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class NotificationPreferenceEntity {
    UUID id;
    UUID userId;
    Boolean pushEnabled;
    Boolean emailEnabled;
    Boolean smsEnabled;
    Boolean inAppEnabled;
    String preferences;
    Boolean quietHoursEnabled;
    LocalTime quietHoursStart;
    LocalTime quietHoursEnd;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
