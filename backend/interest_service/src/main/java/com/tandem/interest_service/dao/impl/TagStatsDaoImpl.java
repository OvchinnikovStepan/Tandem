package com.tandem.interest_service.dao.impl;

import com.tandem.interest_service.dao.TagStatsDao;

import com.tandem.interest_service.dao.model.TagStatsEntity;
import com.tandem.interest_service.dao.queries.TagStatsQueries;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Repository
public class TagStatsDaoImpl implements TagStatsDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final int DEFAULT_LIMIT = 10;

    private final RowMapper<TagStatsEntity> rowMapper = (rs, rowNum) ->
            TagStatsEntity.builder()
                    .tagId(rs.getObject("tag_id", UUID.class))
                    .tagName(rs.getString("tag_name"))
                    .usageCount(rs.getInt("usage_count"))
                    .build();

    public TagStatsDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<TagStatsEntity> findByTagId(UUID tagId) {
        List<TagStatsEntity> results = jdbcTemplate.query(
                TagStatsQueries.SELECT_STATS_BY_TAG_ID,
                Map.of("tagId", tagId),
                rowMapper
        );

        return results.stream().findFirst();
    }

    @Override
    public Optional<Integer> findUsageCountByTagId(UUID tagId) {
        try {
            Integer usageCount = jdbcTemplate.queryForObject(
                    TagStatsQueries.SELECT_USAGE_COUNT_BY_TAG_ID,
                    Map.of("tagId", tagId),
                    Integer.class
            );
            return Optional.ofNullable(usageCount);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<TagStatsEntity> findTopTags(int limit) {
        int finalLimit = limit > 0 ? limit : DEFAULT_LIMIT;

        return jdbcTemplate.query(
                TagStatsQueries.SELECT_TOP_STATS,
                Map.of("limit", finalLimit),
                rowMapper
        );
    }

    @Override
    public void refreshMaterializedView() {
        try {
            jdbcTemplate.getJdbcTemplate().execute(TagStatsQueries.REFRESH_STATS_VIEW);
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh materialized view", e);
        }
    }
}