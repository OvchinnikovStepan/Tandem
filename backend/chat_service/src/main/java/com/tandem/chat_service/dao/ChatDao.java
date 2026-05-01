package com.tandem.chat_service.dao;

import com.tandem.chat_service.dao.model.ChatEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatDao {
    void insert(ChatEntity entity);
    void update(ChatEntity entity);
    void delete(UUID id);
    Optional<ChatEntity> findById(UUID id);
    Optional<ChatEntity> findByGroupId(UUID groupId);
    List<ChatEntity> findPersonalChatsByUser(UUID userId);
    List<ChatEntity> findGroupChatsByUser(UUID userId);
    void updateLastMessageAt(UUID chatId, LocalDateTime lastMessageAt);
}