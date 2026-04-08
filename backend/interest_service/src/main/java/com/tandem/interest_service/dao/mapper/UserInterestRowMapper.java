package com.tandem.interest_service.dao.mapper;

import com.tandem.interest_service.dao.model.UserInterestEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserInterestRowMapper {
    public final RowMapper<UserInterestEntity> rowMapper = (rs, rowNum) ->
            UserInterestEntity.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .userId(UUID.fromString(rs.getString("user_id")))
                    .tagId(UUID.fromString(rs.getString("tag_id")))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .build();
}
