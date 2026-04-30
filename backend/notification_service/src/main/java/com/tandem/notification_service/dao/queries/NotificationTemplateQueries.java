package com.tandem.notification_service.dao.queries;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationTemplateQueries {
    public static final String SELECT_BY_TYPE = """
            SELECT
                id,
                type,
                title_template,
                body_template,
                variables,
                channels,
                created_at,
                updated_at
            FROM notification_templates
            WHERE type = :type
            """;
}
