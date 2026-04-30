package com.tandem.notification_service.dao;

import com.tandem.notification_service.dao.model.NotificationTemplateEntity;

import java.util.Optional;

public interface NotificationTemplateDao {
    Optional<NotificationTemplateEntity> findByType(String type);
}
