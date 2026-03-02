package com.tandem.interest_service.dao.mapper;

import com.tandem.interest_service.dao.model.TagEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Маппер для TagEntity
 */
public final class TagJdbcMapper {

    public static MapSqlParameterSource mapInsertParams(TagEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("name", entity.getName());
        // imageUrl в БД будет NULL по умолчанию
        return params;
    }

    public static MapSqlParameterSource mapUpdateParams(TagEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("name", entity.getName());
        params.addValue("imageUrl", entity.getImageUrl());
        return params;
    }
}