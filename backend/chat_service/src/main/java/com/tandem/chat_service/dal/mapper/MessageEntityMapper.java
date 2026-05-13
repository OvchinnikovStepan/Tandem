package com.tandem.chat_service.dal.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.chat_service.dao.model.MessageEntity;
import com.tandem.chat_service.service.model.dto.MessageMetadata;
import com.tandem.chat_service.service.model.request.SendMessageRequest;
import com.tandem.chat_service.service.model.request.UpdateMessageRequest;
import com.tandem.chat_service.service.model.response.MessageResponse;
import com.tandem.chat_service.service.model.response.PaginatedMessagesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageEntityMapper {

    private final ObjectMapper objectMapper;

    public MessageEntity mapToEntity(UUID chatId, UUID senderId, SendMessageRequest request) {
        return MessageEntity.builder()
                .chatId(chatId)
                .senderId(senderId)
                .content(request.getContent())
                .messageType(request.getType())
                .metadata(objectMapper.convertValue(request.getMetadata(), new TypeReference<Map<String, Object>>() {}))
                .build();
    }

    public MessageEntity mapToUpdatedEntity(MessageEntity existing, UpdateMessageRequest request) {
        return MessageEntity.builder()
                .id(existing.getId())
                .chatId(existing.getChatId())
                .senderId(existing.getSenderId())
                .content(request.getContent())
                .messageType(existing.getMessageType())
                .metadata(existing.getMetadata())
                .sentAt(existing.getSentAt())
                .deletedAt(existing.getDeletedAt())
                .build();
    }

    public MessageResponse mapToResponse(MessageEntity entity) {
        if (entity == null) {
            return null;
        }

        return MessageResponse.builder()
                .messageId(entity.getId())
                .chatId(entity.getChatId())
                .senderId(entity.getSenderId())
                .content(entity.getContent())
                .type(entity.getMessageType())
                .metadata(objectMapper.convertValue(entity.getMetadata(), MessageMetadata.class))
                .sentAt(entity.getSentAt())
                .build();
    }

    public PaginatedMessagesResponse mapToPaginatedResponse(List<MessageEntity> messages, boolean hasMore) {
        List<MessageResponse> responseList = messages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PaginatedMessagesResponse.builder()
                .messages(responseList)
                .hasMore(hasMore)
                .build();
    }
}