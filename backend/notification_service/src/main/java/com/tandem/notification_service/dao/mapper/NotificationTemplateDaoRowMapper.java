package com.tandem.notification_service.dao.mapper;

import com.tandem.notification_service.dao.model.NotificationTemplateEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class NotificationTemplateDaoRowMapper {
    public final RowMapper<NotificationTemplateEntity> rowMapper = (rs, rowNum) -> NotificationTemplateEntity.builder()
            .id(rs.getObject("id", java.util.UUID.class))
            .type(rs.getString("type"))
            .titleTemplate(rs.getString("title_template"))
            .bodyTemplate(rs.getString("body_template"))
            .variables(rs.getString("variables"))
            .channels(toStringList(rs.getArray("channels")))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
            .build();

    private List<String> toStringList(Array array) throws SQLException {
        if (array == null) {
            return Collections.emptyList();
        }
        Object value = array.getArray();
        if (value instanceof String[] items) {
            return Arrays.asList(items);
        }
        return Collections.emptyList();
    }
}
