package com.tandem.notification_service.dao;

import com.tandem.notification_service.dao.model.NotificationPreferenceEntity;

import java.util.Optional;
import java.util.UUID;

public interface NotificationPreferenceDao {
    Optional<NotificationPreferenceEntity> findByUserId(UUID userId);

    void insert(NotificationPreferenceEntity entity);

    void update(NotificationPreferenceEntity entity);
}
