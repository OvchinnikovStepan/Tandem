package com.tandem.chat_service.dao.mapper;

import com.tandem.chat_service.dao.model.GroupRequestEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public final class GroupRequestJdbcMapper {

    private GroupRequestJdbcMapper() {}

    public static MapSqlParameterSource mapInsertParams(GroupRequestEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("groupId", entity.getGroupId());
        params.addValue("userId", entity.getUserId());
        params.addValue("requestedBy", entity.getRequestedBy());
        params.addValue("status", entity.getStatus().getValue());
        params.addValue("message", entity.getMessage());
        params.addValue("createdAt", entity.getCreatedAt());
        params.addValue("expiresAt", entity.getExpiresAt());
        return params;
    }
}