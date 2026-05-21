package com.tandem.interest_service.dao.mapper;

import com.tandem.interest_service.dao.model.GroupTagEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class GroupTagRowMapper {
    public final RowMapper<GroupTagEntity> rowMapper = (rs, rowNum) ->
            GroupTagEntity.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .groupId(UUID.fromString(rs.getString("group_id")))
                    .tagId(UUID.fromString(rs.getString("tag_id")))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .build();
}
