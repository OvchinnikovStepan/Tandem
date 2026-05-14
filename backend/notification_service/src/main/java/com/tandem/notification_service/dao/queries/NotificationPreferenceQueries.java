package com.tandem.notification_service.dao.queries;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationPreferenceQueries {
    public static final String SELECT_BY_USER_ID = """
            SELECT
                id,
                user_id,
                push_enabled,
                email_enabled,
                sms_enabled,
                in_app_enabled,
                preferences,
                quiet_hours_enabled,
                quiet_hours_start,
                quiet_hours_end,
                created_at,
                updated_at
            FROM notification_preferences
            WHERE user_id = :userId
            """;

    public static final String INSERT = """
            INSERT INTO notification_preferences (
                id,
                user_id,
                push_enabled,
                email_enabled,
                sms_enabled,
                in_app_enabled,
                preferences,
                quiet_hours_enabled,
                quiet_hours_start,
                quiet_hours_end,
                created_at,
                updated_at
            ) VALUES (
                :id,
                :userId,
                :pushEnabled,
                :emailEnabled,
                :smsEnabled,
                :inAppEnabled,
                CAST(:preferences AS JSONB),
                :quietHoursEnabled,
                :quietHoursStart,
                :quietHoursEnd,
                :createdAt,
                :updatedAt
            )
            """;

    public static final String UPDATE = """
            UPDATE notification_preferences
            SET push_enabled = :pushEnabled,
                email_enabled = :emailEnabled,
                sms_enabled = :smsEnabled,
                in_app_enabled = :inAppEnabled,
                preferences = CAST(:preferences AS JSONB),
                quiet_hours_enabled = :quietHoursEnabled,
                quiet_hours_start = :quietHoursStart,
                quiet_hours_end = :quietHoursEnd,
                updated_at = :updatedAt
            WHERE user_id = :userId
            """;
}
