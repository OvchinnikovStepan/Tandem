package com.tandem.notification_service.dao.queries;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationQueries {
    public static final String INSERT = """
            INSERT INTO notifications (
                id,
                user_id,
                type,
                title,
                body,
                data,
                channel,
                read,
                read_at,
                delivered_at,
                created_at,
                expires_at,
                archived_at
            ) VALUES (
                :id,
                :userId,
                :type,
                :title,
                :body,
                CAST(:data AS JSONB),
                :channel,
                :read,
                :readAt,
                :deliveredAt,
                :createdAt,
                :expiresAt,
                :archivedAt
            )
            """;

    public static final String SELECT_COLUMNS = """
            id,
            user_id,
            type,
            title,
            body,
            data,
            channel,
            read,
            read_at,
            delivered_at,
            created_at,
            expires_at,
            archived_at
            """;

    public static final String SELECT_BY_USER_ID = """
            SELECT
            """ + SELECT_COLUMNS + """
            FROM notifications
            WHERE user_id = :userId
              AND archived_at IS NULL
              AND (CAST(:type AS VARCHAR) IS NULL OR type = CAST(:type AS VARCHAR))
              AND (CAST(:readValue AS BOOLEAN) IS NULL OR read = CAST(:readValue AS BOOLEAN))
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
            """;

    public static final String COUNT_BY_USER_ID = """
            SELECT COUNT(1)
            FROM notifications
            WHERE user_id = :userId
              AND archived_at IS NULL
              AND (CAST(:type AS VARCHAR) IS NULL OR type = CAST(:type AS VARCHAR))
              AND (CAST(:readValue AS BOOLEAN) IS NULL OR read = CAST(:readValue AS BOOLEAN))
            """;

    public static final String COUNT_UNREAD_BY_USER_ID = """
            SELECT COUNT(1)
            FROM notifications
            WHERE user_id = :userId
              AND archived_at IS NULL
              AND read = FALSE
            """;

    public static final String SELECT_UNREAD_BY_USER_ID = """
            SELECT
            """ + SELECT_COLUMNS + """
            FROM notifications
            WHERE user_id = :userId
              AND archived_at IS NULL
              AND read = FALSE
            ORDER BY created_at DESC
            LIMIT :limit
            """;

    public static final String SELECT_BY_ID_AND_USER_ID = """
            SELECT
            """ + SELECT_COLUMNS + """
            FROM notifications
            WHERE id = :notificationId
              AND user_id = :userId
              AND archived_at IS NULL
            """;

    public static final String MARK_AS_READ = """
            UPDATE notifications
            SET read = TRUE,
                read_at = :readAt
            WHERE id = :notificationId
              AND user_id = :userId
              AND archived_at IS NULL
              AND read = FALSE
            """;

    public static final String MARK_ALL_AS_READ = """
            UPDATE notifications
            SET read = TRUE,
                read_at = :readAt
            WHERE user_id = :userId
              AND archived_at IS NULL
              AND read = FALSE
            """;

    public static final String SOFT_DELETE = """
            UPDATE notifications
            SET archived_at = :archivedAt
            WHERE id = :notificationId
              AND user_id = :userId
              AND archived_at IS NULL
            """;
}
