package com.tandem.chat_service.dao.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.chat_service.dao.enums.MessageType;
import com.tandem.chat_service.dao.model.MessageEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class MessageDaoRowMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public final RowMapper<MessageEntity> rowMapper = (rs, rowNum) -> {
        LocalDateTime deletedAt = rs.getTimestamp("deleted_at") != null
                ? rs.getTimestamp("deleted_at").toLocalDateTime()
                : null;

        String jsonString = rs.getString("metadata");
        Map<String, Object> metadata = null;

        if (jsonString != null && !jsonString.isBlank()) {
            try {
                metadata = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse metadata JSON", e);
            }
        }

        return MessageEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .chatId(rs.getObject("chat_id", UUID.class))
                .senderId(rs.getObject("sender_id", UUID.class))
                .content(rs.getString("content"))
                .messageType(MessageType.fromValue(rs.getString("message_type")))
                .metadata(metadata)
                .sentAt(rs.getTimestamp("sent_at").toLocalDateTime())
                .deletedAt(deletedAt)
                .build();
    };
}