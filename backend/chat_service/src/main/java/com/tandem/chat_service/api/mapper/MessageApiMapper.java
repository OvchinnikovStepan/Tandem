package com.tandem.chat_service.api.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.chat_service.api.model.dto.MessageMetadataJson;
import com.tandem.chat_service.api.model.request.SendMessageRequestJson;
import com.tandem.chat_service.api.model.request.UpdateMessageRequestJson;
import com.tandem.chat_service.api.model.response.MessageResponseJson;
import com.tandem.chat_service.api.model.response.PaginatedMessagesResponseJson;
import com.tandem.chat_service.service.model.dto.MessageMetadata;
import com.tandem.chat_service.service.model.request.SendMessageRequest;
import com.tandem.chat_service.service.model.request.UpdateMessageRequest;
import com.tandem.chat_service.service.model.response.MessageResponse;
import com.tandem.chat_service.service.model.response.PaginatedMessagesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageApiMapper {

    private final ObjectMapper objectMapper;

    public SendMessageRequest toServiceModel(SendMessageRequestJson json) {
        return SendMessageRequest.builder()
                .content(json.getContent())
                .type(json.getType())
                .metadata(json.getMetadata() != null ?
                        objectMapper.convertValue(json.getMetadata(), MessageMetadata.class) : null)
                .build();
    }

    public UpdateMessageRequest toUpdateServiceModel(UpdateMessageRequestJson json) {
        return UpdateMessageRequest.builder()
                .content(json.getContent())
                .build();
    }

    public MessageResponseJson toJson(MessageResponse response) {
        if (response == null) return null;

        return MessageResponseJson.builder()
                .messageId(response.getMessageId())
                .chatId(response.getChatId())
                .senderId(response.getSenderId())
                .senderName(response.getSenderName())
                .content(response.getContent())
                .type(response.getType())
                .metadata(response.getMetadata() != null ?
                        objectMapper.convertValue(response.getMetadata(), MessageMetadataJson.class) : null)
                .sentAt(response.getSentAt())
                .editedAt(response.getEditedAt())
                .build();
    }

    public PaginatedMessagesResponseJson toPaginatedJson(PaginatedMessagesResponse response) {
        return PaginatedMessagesResponseJson.builder()
                .messages(response.getMessages().stream()
                        .map(this::toJson)
                        .collect(Collectors.toList()))
                .hasMore(response.isHasMore())
                .build();
    }
}