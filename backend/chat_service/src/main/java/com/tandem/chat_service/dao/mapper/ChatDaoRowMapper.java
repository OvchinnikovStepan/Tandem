package com.tandem.chat_service.dao.mapper;

import com.tandem.chat_service.dao.model.ChatEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;


@Component
public class ChatDaoRowMapper {

    public final RowMapper<ChatEntity> rowMapper = (rs, rowNum) -> {
        LocalDateTime lastMessageAt = rs.getTimestamp("last_message_at") != null
                ? rs.getTimestamp("last_message_at").toLocalDateTime()
                : null;

        return ChatEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .groupId(rs.getObject("group_id", UUID.class))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .lastMessageAt(lastMessageAt)
                .build();
    };
}