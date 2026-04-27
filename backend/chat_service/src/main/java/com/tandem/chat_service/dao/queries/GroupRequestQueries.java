package com.tandem.chat_service.dao.queries;

/**
 * SQL запросы для работы с таблицей group_requests
 */
public final class GroupRequestQueries {
    private GroupRequestQueries() {}

    public static final String INSERT =
            "INSERT INTO group_requests (id, group_id, user_id, requested_by, status, message, created_at, expires_at) " +
                    "VALUES (:id, :groupId, :userId, :requestedBy, :status, :message, :createdAt, :expiresAt)";

    public static final String UPDATE_STATUS =
            "UPDATE group_requests SET status = :status, reviewed_at = :reviewedAt, reviewed_by = :reviewedBy " +
                    "WHERE id = :id";

    public static final String SELECT_BY_ID =
            "SELECT * FROM group_requests WHERE id = :id";

    public static final String SELECT_PENDING_BY_GROUP =
            "SELECT * FROM group_requests WHERE group_id = :groupId AND status = 'pending' AND expires_at > NOW()";

    public static final String SELECT_BY_USER =
            "SELECT * FROM group_requests WHERE user_id = :userId ORDER BY created_at DESC";
}