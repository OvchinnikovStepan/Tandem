package com.tandem.chat_service.dao.mapper;

import com.tandem.chat_service.dao.enums.ParticipantRole;
import com.tandem.chat_service.dao.model.ChatParticipantEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;


@Component
public class ChatParticipantDaoRowMapper {

    public final RowMapper<ChatParticipantEntity> rowMapper = (rs, rowNum) -> {
        LocalDateTime exitedAt = rs.getTimestamp("exited_at") != null
                ? rs.getTimestamp("exited_at").toLocalDateTime()
                : null;

        return ChatParticipantEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .chatId(rs.getObject("chat_id", UUID.class))
                .userId(rs.getObject("user_id", UUID.class))
                .role(ParticipantRole.fromValue(rs.getString("role")))
                .isMuted(rs.getBoolean("is_muted"))
                .isBanned(rs.getBoolean("is_banned"))
                .joinedAt(rs.getTimestamp("joined_at").toLocalDateTime())
                .exitedAt(exitedAt)
                .build();
    };
}