package com.tandem.notification_service.service;

import java.util.List;
import java.util.UUID;

public interface NotificationDeliveryService {
    void deliver(UUID notificationId, UUID userId, String title, String body, List<String> channels);
}
