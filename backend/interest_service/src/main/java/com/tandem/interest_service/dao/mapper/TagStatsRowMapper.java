package com.tandem.interest_service.dao.mapper;

import com.tandem.interest_service.dao.model.TagStatsEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TagStatsRowMapper {
    public final RowMapper<TagStatsEntity> rowMapper = (rs, rowNum) ->
            TagStatsEntity.builder()
                    .tagId(rs.getObject("tag_id", UUID.class))
                    .tagName(rs.getString("tag_name"))
                    .usageCount(rs.getInt("usage_count"))
                    .build();

}
