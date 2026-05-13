package com.tandem.chat_service.service.model.response;

import com.tandem.chat_service.dao.enums.MessageType;
import com.tandem.chat_service.service.model.dto.MessageMetadata;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class MessageResponse {
    private UUID messageId;
    private UUID chatId;
    private UUID senderId;
    private String senderName;
    private String content;
    private MessageType type;
    private MessageMetadata metadata;
    private LocalDateTime sentAt;
    private LocalDateTime editedAt;
}