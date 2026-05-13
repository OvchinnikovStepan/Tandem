package com.tandem.chat_service.dao.queries;

public final class MessageQueries {

    private MessageQueries() {}

    public static final String INSERT =
            "INSERT INTO messages (id, chat_id, sender_id, content, message_type, metadata, sent_at, deleted_at) " +
                    "VALUES (:id, :chatId, :senderId, :content, :messageType, :metadata::jsonb, :sentAt, :deletedAt)";

    public static final String UPDATE =
            "UPDATE messages SET content = :content, deleted_at = :deletedAt WHERE id = :id";

    public static final String SOFT_DELETE =
            "UPDATE messages SET deleted_at = :deletedAt WHERE id = :id";

    public static final String SELECT_BY_ID =
            "SELECT id, chat_id, sender_id, content, message_type, metadata, sent_at, deleted_at " +
                    "FROM messages WHERE id = :id";

    public static final String SELECT_BY_CHAT_ID_PAGINATED_BEFORE =
            "SELECT id, chat_id, sender_id, content, message_type, metadata, sent_at, deleted_at " +
                    "FROM messages WHERE chat_id = :chatId AND deleted_at IS NULL AND sent_at < :before " +
                    "ORDER BY sent_at DESC LIMIT :limit";

    public static final String SELECT_BY_CHAT_ID_PAGINATED_AFTER =
            "SELECT id, chat_id, sender_id, content, message_type, metadata, sent_at, deleted_at " +
                    "FROM messages WHERE chat_id = :chatId AND deleted_at IS NULL AND sent_at > :after " +
                    "ORDER BY sent_at ASC LIMIT :limit";
}
