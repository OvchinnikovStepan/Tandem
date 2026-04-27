package com.tandem.chat_service.dao.mapper;

import com.tandem.chat_service.dao.model.ChatEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;


public final class ChatJdbcMapper {

    private ChatJdbcMapper() {}

    public static MapSqlParameterSource mapInsertParams(ChatEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("groupId", entity.getGroupId());
        params.addValue("createdAt", entity.getCreatedAt());
        params.addValue("lastMessageAt", entity.getLastMessageAt());
        return params;
    }

    public static MapSqlParameterSource mapUpdateParams(ChatEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("groupId", entity.getGroupId());
        params.addValue("lastMessageAt", entity.getLastMessageAt());
        return params;
    }
}