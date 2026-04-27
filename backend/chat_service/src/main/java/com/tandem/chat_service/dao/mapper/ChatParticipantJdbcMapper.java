package com.tandem.chat_service.dao.mapper;

import com.tandem.chat_service.dao.model.ChatParticipantEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;


public final class ChatParticipantJdbcMapper {

    private ChatParticipantJdbcMapper() {}

    public static MapSqlParameterSource mapInsertParams(ChatParticipantEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("chatId", entity.getChatId());
        params.addValue("userId", entity.getUserId());
        params.addValue("role", entity.getRole().getValue());
        params.addValue("isMuted", entity.getIsMuted());
        params.addValue("isBanned", entity.getIsBanned());
        params.addValue("joinedAt", entity.getJoinedAt());
        params.addValue("exitedAt", entity.getExitedAt());
        return params;
    }

    public static MapSqlParameterSource mapUpdateParams(ChatParticipantEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("role", entity.getRole().getValue());
        params.addValue("isMuted", entity.getIsMuted());
        params.addValue("isBanned", entity.getIsBanned());
        params.addValue("exitedAt", entity.getExitedAt());
        return params;
    }
}