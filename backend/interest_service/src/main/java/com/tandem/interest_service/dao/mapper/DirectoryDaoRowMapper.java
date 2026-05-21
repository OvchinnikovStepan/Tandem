package com.tandem.interest_service.dao.mapper;

import com.tandem.interest_service.dao.model.GroupDirectoryEntity;
import com.tandem.interest_service.dao.model.UserDirectoryEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DirectoryDaoRowMapper {

    public final RowMapper<UserDirectoryEntity> userRowMapper = (rs, rowNum) ->
            UserDirectoryEntity.builder()
                    .userId(rs.getObject("user_id", UUID.class))
                    .displayName(rs.getString("display_name"))
                    .build();

    public final RowMapper<GroupDirectoryEntity> groupRowMapper = (rs, rowNum) ->
            GroupDirectoryEntity.builder()
                    .groupId(rs.getObject("group_id", UUID.class))
                    .name(rs.getString("name"))
                    .build();
}
