package com.tandem.interest_service.dao.mapper;

import com.tandem.interest_service.dao.model.TagEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TagDaoRowMapper {
    public final RowMapper<TagEntity> rowMapper = (rs, rowNum) -> {
        return TagEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .name(rs.getString("name"))
                .imageUrl(rs.getString("image_url"))
                .build();
    };
}
