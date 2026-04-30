package com.tandem.notification_service.api.impl;

import com.tandem.notification_service.api.NotificationApi;
import com.tandem.notification_service.api.mapper.NotificationApiMapper;
import com.tandem.notification_service.api.model.request.NotificationPreferencesUpdateRequestJson;
import com.tandem.notification_service.api.model.response.*;
import com.tandem.notification_service.api.support.RequestUserProvider;
import com.tandem.notification_service.service.NotificationService;
import com.tandem.notification_service.service.model.request.NotificationPreferencesUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotificationApiImpl implements NotificationApi {

    private final NotificationService notificationService;
    private final RequestUserProvider requestUserProvider;

    @Override
    public ResponseEntity<NotificationListResponseJson> getNotifications(Integer page, Integer limit, String type, Boolean read) {
        UUID userId = requestUserProvider.getCurrentUserId();
        return ResponseEntity.ok(NotificationApiMapper.toJson(
                notificationService.getNotifications(userId, page, limit, type, read)
        ));
    }

    @Override
    public ResponseEntity<NotificationUnreadResponseJson> getUnreadNotifications() {
        UUID userId = requestUserProvider.getCurrentUserId();
        return ResponseEntity.ok(NotificationApiMapper.toJson(
                notificationService.getUnreadNotifications(userId)
        ));
    }

    @Override
    public ResponseEntity<NotificationReadResponseJson> markAsRead(UUID notificationId) {
        UUID userId = requestUserProvider.getCurrentUserId();
        return ResponseEntity.ok(NotificationApiMapper.toJson(
                notificationService.markAsRead(userId, notificationId)
        ));
    }

    @Override
    public ResponseEntity<NotificationMarkAllReadResponseJson> markAllAsRead() {
        UUID userId = requestUserProvider.getCurrentUserId();
        return ResponseEntity.ok(NotificationMarkAllReadResponseJson.builder()
                .marked(notificationService.markAllAsRead(userId))
                .build());
    }

    @Override
    public ResponseEntity<NotificationDeleteResponseJson> deleteNotification(UUID notificationId) {
        UUID userId = requestUserProvider.getCurrentUserId();
        return ResponseEntity.ok(NotificationDeleteResponseJson.builder()
                .deleted(notificationService.delete(userId, notificationId))
                .build());
    }

    @Override
    public ResponseEntity<NotificationPreferencesResponseJson> getPreferences() {
        UUID userId = requestUserProvider.getCurrentUserId();
        return ResponseEntity.ok(NotificationApiMapper.toJson(
                notificationService.getPreferences(userId)
        ));
    }

    @Override
    public ResponseEntity<NotificationPreferencesUpdateResponseJson> updatePreferences(NotificationPreferencesUpdateRequestJson request) {
        UUID userId = requestUserProvider.getCurrentUserId();
        NotificationPreferencesUpdateRequest serviceRequest = NotificationApiMapper.toServiceRequest(request);
        return ResponseEntity.ok(NotificationPreferencesUpdateResponseJson.builder()
                .updated(true)
                .preferences(NotificationApiMapper.toJson(notificationService.updatePreferences(userId, serviceRequest)))
                .build());
    }
}
