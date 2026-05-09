package com.tandem.chat_service.dao;

import com.tandem.chat_service.dao.model.MessageEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageDao {
    void insert(MessageEntity entity);
    void update(MessageEntity entity);
    void softDelete(UUID id, LocalDateTime deletedAt);
    Optional<MessageEntity> findById(UUID id);
    List<MessageEntity> findMessagesByChatIdBefore(UUID chatId, LocalDateTime before, int limit);
    List<MessageEntity> findMessagesByChatIdAfter(UUID chatId, LocalDateTime after, int limit);
}
