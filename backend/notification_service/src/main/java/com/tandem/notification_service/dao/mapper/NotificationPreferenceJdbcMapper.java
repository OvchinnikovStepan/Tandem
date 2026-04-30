package com.tandem.notification_service.dao.mapper;

import com.tandem.notification_service.dao.model.NotificationPreferenceEntity;
import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@UtilityClass
public class NotificationPreferenceJdbcMapper {
    public SqlParameterSource mapEntityParams(NotificationPreferenceEntity entity) {
        return new MapSqlParameterSource()
                .addValue("id", entity.getId())
                .addValue("userId", entity.getUserId())
                .addValue("pushEnabled", entity.getPushEnabled())
                .addValue("emailEnabled", entity.getEmailEnabled())
                .addValue("smsEnabled", entity.getSmsEnabled())
                .addValue("inAppEnabled", entity.getInAppEnabled())
                .addValue("preferences", entity.getPreferences())
                .addValue("quietHoursEnabled", entity.getQuietHoursEnabled())
                .addValue("quietHoursStart", entity.getQuietHoursStart())
                .addValue("quietHoursEnd", entity.getQuietHoursEnd())
                .addValue("createdAt", entity.getCreatedAt())
                .addValue("updatedAt", entity.getUpdatedAt());
    }
}
