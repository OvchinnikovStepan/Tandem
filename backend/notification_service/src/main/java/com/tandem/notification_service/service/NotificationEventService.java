package com.tandem.notification_service.service;

public interface NotificationEventService {
    void processEvent(String topic, String payload);
}
