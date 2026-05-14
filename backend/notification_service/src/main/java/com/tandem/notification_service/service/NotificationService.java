package com.tandem.notification_service.service;

import com.tandem.notification_service.service.model.request.NotificationPreferencesUpdateRequest;
import com.tandem.notification_service.service.model.response.NotificationListResponse;
import com.tandem.notification_service.service.model.response.NotificationPreferencesResponse;
import com.tandem.notification_service.service.model.response.NotificationReadResponse;
import com.tandem.notification_service.service.model.response.NotificationUnreadResponse;

import java.util.UUID;

public interface NotificationService {
    NotificationListResponse getNotifications(UUID userId, Integer page, Integer limit, String type, Boolean read);

    NotificationUnreadResponse getUnreadNotifications(UUID userId);

    NotificationReadResponse markAsRead(UUID userId, UUID notificationId);

    int markAllAsRead(UUID userId);

    boolean delete(UUID userId, UUID notificationId);

    NotificationPreferencesResponse getPreferences(UUID userId);

    NotificationPreferencesResponse updatePreferences(UUID userId, NotificationPreferencesUpdateRequest request);
}
