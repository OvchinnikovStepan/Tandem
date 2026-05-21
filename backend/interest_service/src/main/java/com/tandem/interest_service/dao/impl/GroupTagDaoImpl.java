package com.tandem.interest_service.dao.impl;

import com.tandem.interest_service.dao.GroupTagDao;
import com.tandem.interest_service.dao.mapper.GroupTagJdbcMapper;
import com.tandem.interest_service.dao.mapper.GroupTagRowMapper;
import com.tandem.interest_service.dao.queries.GroupTagQueries;
import com.tandem.interest_service.dao.model.GroupTagEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GroupTagDaoImpl implements GroupTagDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final GroupTagRowMapper groupTagRowMapper;

    public GroupTagDaoImpl(NamedParameterJdbcTemplate jdbcTemplate, GroupTagRowMapper groupTagRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.groupTagRowMapper = groupTagRowMapper;
    }

    @Override
    public void insertBatch(List<GroupTagEntity> entities) {
        MapSqlParameterSource[] batchArgs = entities.stream()
                .map(GroupTagJdbcMapper::mapInsertParams)
                .toArray(MapSqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(GroupTagQueries.INSERT, batchArgs);
    }

    @Override
    public void delete(UUID id) {
        jdbcTemplate.update(
                GroupTagQueries.DELETE,
                Map.of("id", id)
        );
    }

    @Override
    public List<GroupTagEntity> findByGroupId(UUID groupId) {
        return jdbcTemplate.query(
                GroupTagQueries.SELECT_BY_GROUP_ID,
                Map.of("groupId", groupId),
                groupTagRowMapper.rowMapper
        );
    }

    @Override
    public Optional<GroupTagEntity> findByGroupIdAndTagId(UUID groupId, UUID tagId) {
        List<GroupTagEntity> results = jdbcTemplate.query(
                GroupTagQueries.SELECT_BY_GROUP_AND_TAG,
                Map.of(
                        "groupId", groupId,
                        "tagId", tagId
                ),
                groupTagRowMapper.rowMapper
        );
        return results.stream().findFirst();
    }

    @Override
    public int countGroupTags(UUID groupId) {
        Integer count = jdbcTemplate.queryForObject(
                GroupTagQueries.COUNT_GROUP_TAGS,
                Map.of("groupId", groupId),
                Integer.class
        );
        return count != null ? count : 0;
    }

    @Override
    public List<Map<String, Object>> findGroupsWithCommonTagsWithUserCount(UUID userId, int minMatchCount) {
        return jdbcTemplate.queryForList(
                GroupTagQueries.FIND_GROUPS_BY_MIN_COMMON_TAGS_WITH_USER,
                Map.of(
                        "userId", userId,
                        "minMatchCount", minMatchCount
                )
        );
    }

    @Override
    public List<UUID> findCommonTagIdsBetweenUserAndGroup(UUID userId, UUID groupId) {
        return jdbcTemplate.queryForList(
                GroupTagQueries.SELECT_COMMON_TAG_IDS_USER_GROUP,
                Map.of(
                        "userId", userId,
                        "groupId", groupId
                ),
                UUID.class
        );
    }
}
