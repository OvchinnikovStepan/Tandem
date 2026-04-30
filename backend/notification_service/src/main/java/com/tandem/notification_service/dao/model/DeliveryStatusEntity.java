package com.tandem.notification_service.dao.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class DeliveryStatusEntity {
    UUID id;
    UUID notificationId;
    String channel;
    String status;
    String providerResponse;
    LocalDateTime attemptedAt;
    LocalDateTime deliveredAt;
    String errorMessage;
    Integer retryCount;
    LocalDateTime createdAt;
}
