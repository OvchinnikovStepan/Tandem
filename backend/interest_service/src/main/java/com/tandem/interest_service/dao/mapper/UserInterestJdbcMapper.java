package com.tandem.interest_service.dao.mapper;

import com.tandem.interest_service.dao.model.UserInterestEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Маппер для UserInterestEntity
 */
public final class UserInterestJdbcMapper {

    public static MapSqlParameterSource mapInsertParams(UserInterestEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("userId", entity.getUserId());
        params.addValue("tagId", entity.getTagId());
        params.addValue("createdAt", entity.getCreatedAt());
        return params;
    }
}
