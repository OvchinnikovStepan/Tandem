package com.tandem.notification_service.dao.mapper;

import com.tandem.notification_service.dao.model.NotificationEntity;
import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@UtilityClass
public class NotificationJdbcMapper {
    public SqlParameterSource mapInsertParams(NotificationEntity entity) {
        return new MapSqlParameterSource()
                .addValue("id", entity.getId())
                .addValue("userId", entity.getUserId())
                .addValue("type", entity.getType())
                .addValue("title", entity.getTitle())
                .addValue("body", entity.getBody())
                .addValue("data", entity.getData())
                .addValue("channel", entity.getChannel())
                .addValue("read", entity.getRead())
                .addValue("readAt", entity.getReadAt())
                .addValue("deliveredAt", entity.getDeliveredAt())
                .addValue("createdAt", entity.getCreatedAt())
                .addValue("expiresAt", entity.getExpiresAt())
                .addValue("archivedAt", entity.getArchivedAt());
    }

    public SqlParameterSource mapFilterParams(
            java.util.UUID userId,
            Integer limit,
            Integer offset,
            String type,
            Boolean readValue
    ) {
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("limit", limit)
                .addValue("offset", offset)
                .addValue("type", type)
                .addValue("readValue", readValue);
    }

    public SqlParameterSource mapMarkReadParams(java.util.UUID notificationId, java.util.UUID userId, java.time.LocalDateTime readAt) {
        return new MapSqlParameterSource()
                .addValue("notificationId", notificationId)
                .addValue("userId", userId)
                .addValue("readAt", readAt);
    }

    public SqlParameterSource mapMarkAllReadParams(java.util.UUID userId, java.time.LocalDateTime readAt) {
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("readAt", readAt);
    }

    public SqlParameterSource mapSoftDeleteParams(java.util.UUID notificationId, java.util.UUID userId, java.time.LocalDateTime archivedAt) {
        return new MapSqlParameterSource()
                .addValue("notificationId", notificationId)
                .addValue("userId", userId)
                .addValue("archivedAt", archivedAt);
    }
}
