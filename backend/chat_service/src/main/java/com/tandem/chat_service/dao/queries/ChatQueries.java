package com.tandem.chat_service.dao.queries;

/**
 * SQL запросы для работы с таблицей chats
 */
public final class ChatQueries {

    private ChatQueries() {}

    public static final String INSERT =
            "INSERT INTO chats (id, group_id, created_at, last_message_at) " +
                    "VALUES (:id, :groupId, :createdAt, :lastMessageAt)";

    public static final String UPDATE =
            "UPDATE chats SET group_id = :groupId, last_message_at = :lastMessageAt WHERE id = :id";

    public static final String DELETE =
            "DELETE FROM chats WHERE id = :id";

    public static final String SELECT_BY_ID =
            "SELECT id, group_id, created_at, last_message_at FROM chats WHERE id = :id";

    public static final String SELECT_BY_GROUP_ID =
            "SELECT id, group_id, created_at, last_message_at FROM chats WHERE group_id = :groupId";

    public static final String SELECT_PERSONAL_CHATS_BY_USER =
            "SELECT c.id, c.group_id, c.created_at, c.last_message_at " +
                    "FROM chats c " +
                    "INNER JOIN chat_participants cp ON c.id = cp.chat_id " +
                    "WHERE cp.user_id = :userId AND c.group_id IS NULL";

    public static final String SELECT_GROUP_CHATS_BY_USER =
            "SELECT c.id, c.group_id, c.created_at, c.last_message_at " +
                    "FROM chats c " +
                    "INNER JOIN chat_participants cp ON c.id = cp.chat_id " +
                    "WHERE cp.user_id = :userId AND c.group_id IS NOT NULL";

    public static final String UPDATE_LAST_MESSAGE_AT =
            "UPDATE chats SET last_message_at = :lastMessageAt WHERE id = :id";
}
