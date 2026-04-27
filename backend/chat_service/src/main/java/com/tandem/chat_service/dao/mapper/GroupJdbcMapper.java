package com.tandem.chat_service.dao.mapper;

import com.tandem.chat_service.dao.model.GroupEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;


public final class GroupJdbcMapper {

    private GroupJdbcMapper() {}

    public static MapSqlParameterSource mapInsertParams(GroupEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("name", entity.getName());
        params.addValue("description", entity.getDescription());
        params.addValue("avatarUrl", entity.getAvatarUrl());
        params.addValue("creatorId", entity.getCreatorId());
        params.addValue("visibility", entity.getVisibility().getValue());
        return params;
    }

    public static MapSqlParameterSource mapUpdateParams(GroupEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("name", entity.getName());
        params.addValue("description", entity.getDescription());
        params.addValue("avatarUrl", entity.getAvatarUrl());
        params.addValue("visibility", entity.getVisibility().getValue());
        return params;
    }
}