package com.tandem.chat_service.service.model.request;

import com.tandem.chat_service.dao.enums.MessageType;
import com.tandem.chat_service.service.model.dto.MessageMetadata;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendMessageRequest {
    private String content;
    private MessageType type;
    private MessageMetadata metadata;
}