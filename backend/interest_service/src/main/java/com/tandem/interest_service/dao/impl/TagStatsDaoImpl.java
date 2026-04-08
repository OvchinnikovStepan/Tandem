package com.tandem.interest_service.dao.impl;

import com.tandem.interest_service.dao.TagStatsDao;

import com.tandem.interest_service.dao.mapper.TagStatsRowMapper;
import com.tandem.interest_service.dao.model.TagStatsEntity;
import com.tandem.interest_service.dao.queries.TagStatsQueries;
import org.springframework.dao.EmptyResultDataAccessException;
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

    private final TagStatsRowMapper tagStatsRowMapper;

    public TagStatsDaoImpl(NamedParameterJdbcTemplate jdbcTemplate, TagStatsRowMapper tagStatsRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.tagStatsRowMapper = tagStatsRowMapper;
    }

    @Override
    public Optional<TagStatsEntity> findByTagId(UUID tagId) {
        List<TagStatsEntity> results = jdbcTemplate.query(
                TagStatsQueries.SELECT_STATS_BY_TAG_ID,
                Map.of("tagId", tagId),
                tagStatsRowMapper.rowMapper
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
                tagStatsRowMapper.rowMapper
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