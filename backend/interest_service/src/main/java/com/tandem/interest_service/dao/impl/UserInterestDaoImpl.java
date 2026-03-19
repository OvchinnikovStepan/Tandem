package com.tandem.interest_service.dao.impl;

import com.tandem.interest_service.dao.UserInterestDao;
import com.tandem.interest_service.dao.mapper.UserInterestJdbcMapper;
import com.tandem.interest_service.dao.queries.UserInterestQueries;
import com.tandem.interest_service.dao.model.UserInterestEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserInterestDaoImpl implements UserInterestDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<UserInterestEntity> rowMapper = (rs, rowNum) ->
            UserInterestEntity.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .userId(UUID.fromString(rs.getString("user_id")))
                    .tagId(UUID.fromString(rs.getString("tag_id")))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .build();

    public UserInterestDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertBatch(List<UserInterestEntity> entities) {
        MapSqlParameterSource[] batchArgs = entities.stream()
                .map(UserInterestJdbcMapper::mapInsertParams)
                .toArray(MapSqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(UserInterestQueries.INSERT, batchArgs);
    }

    @Override
    public void delete(UUID id) {
        jdbcTemplate.update(
                UserInterestQueries.DELETE,
                Map.of("id", id)
        );
    }

    @Override
    public List<UserInterestEntity> findByUserId(UUID userId) {
        return jdbcTemplate.query(
                UserInterestQueries.SELECT_BY_USER_ID,
                Map.of("userId", userId),
                rowMapper
        );
    }

    @Override
    public Optional<UserInterestEntity> findByUserIdAndTagId(UUID userId, UUID tagId) {
        List<UserInterestEntity> results = jdbcTemplate.query(
                UserInterestQueries.SELECT_BY_USER_AND_TAG,
                Map.of(
                        "userId", userId,
                        "tagId", tagId
                ),
                rowMapper
        );
        return results.stream().findFirst();
    }

    @Override
    public List<Map<String, Object>> findUsersWithCommonTagsCount(UUID currentUserId, int minMatchCount) {
        return jdbcTemplate.queryForList(
                UserInterestQueries.FIND_USERS_BY_MIN_COMMON_TAGS,
                Map.of(
                        "currentUserId", currentUserId,
                        "minMatchCount", minMatchCount
                )
        );
    }

    @Override
    public int countUserInterests(UUID userId) {
        Integer count = jdbcTemplate.queryForObject(
                UserInterestQueries.COUNT_USER_INTERESTS,
                Map.of("userId", userId),
                Integer.class
        );
        return count != null ? count : 0;
    }

    @Override
    public List<UUID> findCommonTagIds(UUID userId1, UUID userId2) {
        Map<String, Object> params = Map.of(
                "userId1", userId1,
                "userId2", userId2
        );

        return jdbcTemplate.queryForList(
                UserInterestQueries.SELECT_COMMON_TAG_IDS,
                params,
                UUID.class
        );
    }
}
