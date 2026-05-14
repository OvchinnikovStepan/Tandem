package com.tandem.notification_service.dao.mapper;

import com.tandem.notification_service.dao.model.NotificationPreferenceEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class NotificationPreferenceDaoRowMapper {
    public final RowMapper<NotificationPreferenceEntity> rowMapper = (rs, rowNum) -> NotificationPreferenceEntity.builder()
            .id(rs.getObject("id", java.util.UUID.class))
            .userId(rs.getObject("user_id", java.util.UUID.class))
            .pushEnabled(rs.getBoolean("push_enabled"))
            .emailEnabled(rs.getBoolean("email_enabled"))
            .smsEnabled(rs.getBoolean("sms_enabled"))
            .inAppEnabled(rs.getBoolean("in_app_enabled"))
            .preferences(rs.getString("preferences"))
            .quietHoursEnabled(rs.getBoolean("quiet_hours_enabled"))
            .quietHoursStart(rs.getTime("quiet_hours_start") == null ? null : rs.getTime("quiet_hours_start").toLocalTime())
            .quietHoursEnd(rs.getTime("quiet_hours_end") == null ? null : rs.getTime("quiet_hours_end").toLocalTime())
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
            .build();
}
