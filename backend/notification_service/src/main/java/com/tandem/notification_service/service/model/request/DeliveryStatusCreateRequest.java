package com.tandem.notification_service.service.model.request;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class DeliveryStatusCreateRequest {
    UUID notificationId;
    String channel;
    String status;
    String providerResponse;
    String errorMessage;
    Integer retryCount;
}
