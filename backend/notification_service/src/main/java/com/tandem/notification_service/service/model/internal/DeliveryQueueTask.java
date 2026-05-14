package com.tandem.notification_service.service.model.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryQueueTask {
    private UUID notificationId;
    private UUID userId;
    private String title;
    private String body;
    private String channel;
    private int attempt;
    private long availableAtEpochMs;
}
