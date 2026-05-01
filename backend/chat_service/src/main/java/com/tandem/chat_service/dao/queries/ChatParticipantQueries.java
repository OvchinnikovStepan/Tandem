package com.tandem.chat_service.dao.queries;

/**
 * SQL запросы для работы с таблицей chat_participants
 */
public final class ChatParticipantQueries {

    private ChatParticipantQueries() {}

    public static final String INSERT =
            "INSERT INTO chat_participants (id, chat_id, user_id, role, is_muted, is_banned, joined_at, exited_at) " +
                    "VALUES (:id, :chatId, :userId, :role, :isMuted, :isBanned, :joinedAt, :exitedAt)";

    public static final String UPDATE =
            "UPDATE chat_participants SET role = :role, is_muted = :isMuted, " +
                    "is_banned = :isBanned, exited_at = :exitedAt WHERE id = :id";

    public static final String DELETE =
            "DELETE FROM chat_participants WHERE id = :id";

    public static final String DELETE_BY_CHAT_ID_AND_USER_ID =
            "DELETE FROM chat_participants WHERE chat_id = :chatId AND user_id = :userId";

    public static final String SELECT_BY_CHAT_ID =
            "SELECT id, chat_id, user_id, role, is_muted, is_banned, joined_at, exited_at " +
                    "FROM chat_participants WHERE chat_id = :chatId";

    public static final String CHECK_IS_PARTICIPANT =
            "SELECT COUNT(*) > 0 FROM chat_participants WHERE chat_id = :chatId AND user_id = :userId";

    public static final String UPDATE_MUTED_STATUS =
            "UPDATE chat_participants SET is_muted = :isMuted WHERE chat_id = :chatId AND user_id = :userId";

    public static final String UPDATE_BANNED_STATUS =
            "UPDATE chat_participants SET is_banned = :isBanned WHERE chat_id = :chatId AND user_id = :userId";
}