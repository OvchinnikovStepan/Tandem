package com.tandem.interest_service.dao.mapper;

import com.tandem.interest_service.dao.model.GroupTagEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public final class GroupTagJdbcMapper {

    private GroupTagJdbcMapper() {}

    public static MapSqlParameterSource mapInsertParams(GroupTagEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("groupId", entity.getGroupId());
        params.addValue("tagId", entity.getTagId());
        params.addValue("createdAt", entity.getCreatedAt());
        return params;
    }
}
