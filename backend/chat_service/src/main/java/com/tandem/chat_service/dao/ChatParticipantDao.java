package com.tandem.chat_service.dao;

import com.tandem.chat_service.dao.model.ChatParticipantEntity;
import java.util.List;
import java.util.UUID;

/**
 * DAO для работы с участниками чатов
 */
public interface ChatParticipantDao {

    void insert(ChatParticipantEntity entity);
    void update(ChatParticipantEntity entity);
    void delete(UUID id);
    void deleteByChatIdAndUserId(UUID chatId, UUID userId);
    List<ChatParticipantEntity> findByChatId(UUID chatId);
    boolean isParticipant(UUID chatId, UUID userId);
    void updateMutedStatus(UUID chatId, UUID userId, boolean isMuted);
    void updateBannedStatus(UUID chatId, UUID userId, boolean isBanned);
}
