package com.tandem.chat_service.dao.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.chat_service.dao.model.MessageEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public final class MessageJdbcMapper {

    private MessageJdbcMapper() {}

    public static MapSqlParameterSource mapInsertParams(MessageEntity entity, ObjectMapper objectMapper) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("chatId", entity.getChatId());
        params.addValue("senderId", entity.getSenderId());
        params.addValue("content", entity.getContent());
        params.addValue("messageType", entity.getMessageType().getValue());

        String metadataJson = null;
        if (entity.getMetadata() != null) {
            try {
                metadataJson = objectMapper.writeValueAsString(entity.getMetadata());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize metadata to JSON string", e);
            }
        }
        params.addValue("metadata", metadataJson);

        params.addValue("sentAt", entity.getSentAt());
        params.addValue("deletedAt", entity.getDeletedAt());
        return params;
    }

    public static MapSqlParameterSource mapUpdateParams(MessageEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("content", entity.getContent());
        params.addValue("deletedAt", entity.getDeletedAt());
        return params;
    }
}