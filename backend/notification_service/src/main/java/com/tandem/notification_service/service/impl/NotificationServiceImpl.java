package com.tandem.notification_service.service.impl;

import com.tandem.notification_service.dal.NotificationDal;
import com.tandem.notification_service.service.NotificationService;
import com.tandem.notification_service.service.model.request.NotificationPreferencesUpdateRequest;
import com.tandem.notification_service.service.model.response.NotificationListResponse;
import com.tandem.notification_service.service.model.response.NotificationPreferencesResponse;
import com.tandem.notification_service.service.model.response.NotificationReadResponse;
import com.tandem.notification_service.service.model.response.NotificationUnreadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDal notificationDal;

    @Override
    public NotificationListResponse getNotifications(UUID userId, Integer page, Integer limit, String type, Boolean read) {
        return notificationDal.getNotifications(userId, page, limit, type, read);
    }

    @Override
    public NotificationUnreadResponse getUnreadNotifications(UUID userId) {
        return notificationDal.getUnreadNotifications(userId);
    }

    @Override
    public NotificationReadResponse markAsRead(UUID userId, UUID notificationId) {
        return notificationDal.markAsRead(userId, notificationId);
    }

    @Override
    public int markAllAsRead(UUID userId) {
        return notificationDal.markAllAsRead(userId);
    }

    @Override
    public boolean delete(UUID userId, UUID notificationId) {
        return notificationDal.delete(userId, notificationId);
    }

    @Override
    public NotificationPreferencesResponse getPreferences(UUID userId) {
        return notificationDal.getPreferences(userId);
    }

    @Override
    public NotificationPreferencesResponse updatePreferences(UUID userId, NotificationPreferencesUpdateRequest request) {
        return notificationDal.updatePreferences(userId, request);
    }
}
