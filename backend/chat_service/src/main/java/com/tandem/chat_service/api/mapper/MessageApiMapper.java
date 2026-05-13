package com.tandem.chat_service.api.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.chat_service.api.model.dto.MessageMetadataJson;
import com.tandem.chat_service.api.model.request.SendMessageRequestJson;
import com.tandem.chat_service.api.model.request.UpdateMessageRequestJson;
import com.tandem.chat_service.api.model.response.MessageResponseJson;
import com.tandem.chat_service.api.model.response.PaginatedMessagesResponseJson;
import com.tandem.chat_service.service.model.dto.MessageMetadata;
import com.tandem.chat_service.service.model.request.GetMessagesFilter;
import com.tandem.chat_service.service.model.request.SendMessageRequest;
import com.tandem.chat_service.service.model.request.UpdateMessageRequest;
import com.tandem.chat_service.service.model.response.MessageResponse;
import com.tandem.chat_service.service.model.response.PaginatedMessagesResponse;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class MessageApiMapper {

    public SendMessageRequest toServiceModel(SendMessageRequestJson json, ObjectMapper objectMapper) {
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

    public GetMessagesFilter toGetMessagesFilter(
            UUID chatId, UUID currentUserId, int limit, LocalDateTime beforeDateTime, LocalDateTime afterDateTime) {
        return GetMessagesFilter.builder()
                .chatId(chatId)
                .requesterId(currentUserId)
                .before(beforeDateTime)
                .after(afterDateTime)
                .limit(limit)
                .build();
    }

    public MessageResponseJson toJson(MessageResponse response, ObjectMapper objectMapper) {
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

    public static PaginatedMessagesResponseJson toPaginatedJson(PaginatedMessagesResponse response, ObjectMapper objectMapper) {
        return PaginatedMessagesResponseJson.builder()
                .messages(response.getMessages().stream()
                        .map(msg -> toJson(msg, objectMapper))
                        .collect(Collectors.toList()))
                .hasMore(response.isHasMore())
                .build();
    }
}