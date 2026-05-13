package com.tandem.chat_service.dao.model;

import com.tandem.chat_service.dao.enums.MessageType;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Map;

@Getter
@Builder
public class MessageEntity {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private UUID chatId;
    private UUID senderId;
    private String content;
    private MessageType messageType;
    private Map<String, Object> metadata;
    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();
    private LocalDateTime deletedAt;
}
