package com.tandem.chat_service.dao.mapper;

import com.tandem.chat_service.dao.enums.GroupRequestStatus;
import com.tandem.chat_service.dao.model.GroupRequestEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class GroupRequestRowMapper {

    public final RowMapper<GroupRequestEntity> rowMapper = (rs, rowNum) -> {
        LocalDateTime reviewedAt = rs.getTimestamp("reviewed_at") != null
                ? rs.getTimestamp("reviewed_at").toLocalDateTime()
                : null;

        return GroupRequestEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .groupId(rs.getObject("group_id", UUID.class))
                .userId(rs.getObject("user_id", UUID.class))
                .requestedBy(rs.getObject("requested_by", UUID.class))
                .status(GroupRequestStatus.fromValue(rs.getString("status")))
                .message(rs.getString("message"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .expiresAt(rs.getTimestamp("expires_at").toLocalDateTime())
                .reviewedAt(reviewedAt)
                .reviewedBy(rs.getObject("reviewed_by", UUID.class))
                .build();
    };
}