package com.tandem.chat_service.dao.impl;

import com.tandem.chat_service.dao.GroupDao;
import com.tandem.chat_service.dao.enums.GroupVisibility;
import com.tandem.chat_service.dao.mapper.GroupDaoRowMapper;
import com.tandem.chat_service.dao.mapper.GroupJdbcMapper;
import com.tandem.chat_service.dao.model.GroupEntity;
import com.tandem.chat_service.dao.queries.GroupQueries;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GroupDaoImpl implements GroupDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final GroupDaoRowMapper rowMapper;

    public GroupDaoImpl(NamedParameterJdbcTemplate jdbcTemplate, GroupDaoRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public void insert(GroupEntity entity) {
        jdbcTemplate.update(GroupQueries.INSERT, GroupJdbcMapper.mapInsertParams(entity));
    }

    @Override
    public void update(GroupEntity entity) {
        jdbcTemplate.update(GroupQueries.UPDATE, GroupJdbcMapper.mapUpdateParams(entity));
    }

    @Override
    public void delete(UUID id) {
        jdbcTemplate.update(GroupQueries.DELETE, Map.of("id", id));
    }

    @Override
    public Optional<GroupEntity> findById(UUID id) {
        List<GroupEntity> results = jdbcTemplate.query(
                GroupQueries.SELECT_BY_ID,
                Map.of("id", id),
                rowMapper.rowMapper
        );
        return results.stream().findFirst();
    }

    @Override
    public List<GroupEntity> findByCreatorId(UUID creatorId) {
        return jdbcTemplate.query(
                GroupQueries.SELECT_BY_CREATOR_ID,
                Map.of("creatorId", creatorId),
                rowMapper.rowMapper
        );
    }

    @Override
    public List<GroupEntity> findByVisibility(GroupVisibility visibility) {
        return jdbcTemplate.query(
                GroupQueries.SELECT_BY_VISIBILITY,
                Map.of("visibility", visibility.getValue()),
                rowMapper.rowMapper
        );
    }

    @Override
    public Optional<GroupEntity> findByName(String name) {
        List<GroupEntity> results = jdbcTemplate.query(
                GroupQueries.SELECT_BY_NAME,
                Map.of("name", name),
                rowMapper.rowMapper
        );
        return results.stream().findFirst();
    }

    @Override
    public List<GroupEntity> searchByNamePrefix(String prefix, int limit) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return List.of();
        }

        return jdbcTemplate.query(
                GroupQueries.SEARCH_BY_PREFIX,
                Map.of("prefix", prefix.trim() + "%", "limit", limit > 0 ? limit : 10),
                rowMapper.rowMapper
        );
    }
}