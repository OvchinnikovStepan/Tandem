package com.tandem.chat_service.dao.mapper;

import com.tandem.chat_service.dao.enums.GroupVisibility;
import com.tandem.chat_service.dao.model.GroupEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class GroupDaoRowMapper {

    public final RowMapper<GroupEntity> rowMapper = (rs, rowNum) ->
            GroupEntity.builder()
                    .id(rs.getObject("id", UUID.class))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .avatarUrl(rs.getString("avatar_url"))
                    .creatorId(rs.getObject("creator_id", UUID.class))
                    .visibility(GroupVisibility.fromValue(rs.getString("visibility")))
                    .build();
}