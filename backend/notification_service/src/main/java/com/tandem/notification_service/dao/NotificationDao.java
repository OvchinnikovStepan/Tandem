package com.tandem.notification_service.dao;

import com.tandem.notification_service.dao.model.NotificationEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationDao {
    void insert(NotificationEntity entity);

    List<NotificationEntity> findByUserId(UUID userId, Integer limit, Integer offset, String type, Boolean read);

    long countByUserId(UUID userId, String type, Boolean read);

    long countUnreadByUserId(UUID userId);

    List<NotificationEntity> findUnreadByUserId(UUID userId, Integer limit);

    Optional<NotificationEntity> findByIdAndUserId(UUID notificationId, UUID userId);

    boolean markAsRead(UUID notificationId, UUID userId, java.time.LocalDateTime readAt);

    int markAllAsRead(UUID userId, java.time.LocalDateTime readAt);

    boolean softDelete(UUID notificationId, UUID userId, java.time.LocalDateTime archivedAt);
}
