package com.tandem.notification_service.dao.mapper;

import com.tandem.notification_service.dao.model.NotificationEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class NotificationDaoRowMapper {
    public final RowMapper<NotificationEntity> rowMapper = (rs, rowNum) -> NotificationEntity.builder()
            .id(rs.getObject("id", java.util.UUID.class))
            .userId(rs.getObject("user_id", java.util.UUID.class))
            .type(rs.getString("type"))
            .channel(rs.getString("channel"))
            .title(rs.getString("title"))
            .body(rs.getString("body"))
            .data(rs.getString("data"))
            .read(rs.getBoolean("read"))
            .readAt(rs.getTimestamp("read_at") == null ? null : rs.getTimestamp("read_at").toLocalDateTime())
            .deliveredAt(rs.getTimestamp("delivered_at") == null ? null : rs.getTimestamp("delivered_at").toLocalDateTime())
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .expiresAt(rs.getTimestamp("expires_at") == null ? null : rs.getTimestamp("expires_at").toLocalDateTime())
            .archivedAt(rs.getTimestamp("archived_at") == null ? null : rs.getTimestamp("archived_at").toLocalDateTime())
            .build();
}
