package com.tandem.chat_service.dao.impl;

import com.tandem.chat_service.dao.GroupRequestDao;
import com.tandem.chat_service.dao.enums.GroupRequestStatus;
import com.tandem.chat_service.dao.mapper.GroupRequestJdbcMapper;
import com.tandem.chat_service.dao.mapper.GroupRequestRowMapper;
import com.tandem.chat_service.dao.model.GroupRequestEntity;
import com.tandem.chat_service.dao.queries.GroupRequestQueries;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GroupRequestDaoImpl implements GroupRequestDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final GroupRequestRowMapper rowMapper;

    public GroupRequestDaoImpl(NamedParameterJdbcTemplate jdbcTemplate, GroupRequestRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public void insert(GroupRequestEntity entity) {
        jdbcTemplate.update(GroupRequestQueries.INSERT, GroupRequestJdbcMapper.mapInsertParams(entity));
    }

    @Override
    public void updateStatus(UUID id, GroupRequestStatus status, LocalDateTime reviewedAt, UUID reviewedBy) {
        jdbcTemplate.update(GroupRequestQueries.UPDATE_STATUS, Map.of(
                "id", id,
                "status", status.getValue(),
                "reviewedAt", reviewedAt,
                "reviewedBy", reviewedBy
        ));
    }

    @Override
    public Optional<GroupRequestEntity> findById(UUID id) {
        List<GroupRequestEntity> results = jdbcTemplate.query(
                GroupRequestQueries.SELECT_BY_ID,
                Map.of("id", id),
                rowMapper.rowMapper
        );
        return results.stream().findFirst();
    }

    @Override
    public List<GroupRequestEntity> findPendingByGroupId(UUID groupId) {
        return jdbcTemplate.query(
                GroupRequestQueries.SELECT_PENDING_BY_GROUP,
                Map.of("groupId", groupId),
                rowMapper.rowMapper
        );
    }

    @Override
    public List<GroupRequestEntity> findByUserId(UUID userId) {
        return jdbcTemplate.query(
                GroupRequestQueries.SELECT_BY_USER,
                Map.of("userId", userId),
                rowMapper.rowMapper
        );
    }
}
