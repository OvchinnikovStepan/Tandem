package com.tandem.interest_service.dao.impl;

import com.tandem.interest_service.dao.TagDao;
import com.tandem.interest_service.dao.mapper.TagDaoRowMapper;
import com.tandem.interest_service.dao.mapper.TagJdbcMapper;
import com.tandem.interest_service.dao.queries.TagQueries;
import com.tandem.interest_service.dao.model.TagEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TagDaoImpl implements TagDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final int DEFAULT_SEARCH_LIMIT = 10;

    private final TagDaoRowMapper tagDaoRowMapper;

    public TagDaoImpl(NamedParameterJdbcTemplate jdbcTemplate, TagDaoRowMapper tagDaoRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.tagDaoRowMapper = tagDaoRowMapper;
    }

    @Override
    public void insert(TagEntity entity) {
        jdbcTemplate.update(
                TagQueries.INSERT,
                TagJdbcMapper.mapInsertParams(entity)
        );
    }

    @Override
    public void update(TagEntity entity) {
        jdbcTemplate.update(
                TagQueries.UPDATE,
                TagJdbcMapper.mapUpdateParams(entity)
        );
    }

    @Override
    public void delete(UUID id) {
        jdbcTemplate.update(
                TagQueries.DELETE,
                Map.of("id", id)
        );
    }

    @Override
    public Optional<TagEntity> findById(UUID id) {
        List<TagEntity> results = jdbcTemplate.query(
                TagQueries.SELECT_BY_ID,
                Map.of("id", id),
                tagDaoRowMapper.rowMapper
        );
        return results.stream().findFirst();
    }

    @Override
    public List<TagEntity> findAll() {
        return jdbcTemplate.query(
                TagQueries.SELECT_ALL,
                tagDaoRowMapper.rowMapper
        );
    }

    @Override
    public List<TagEntity> findDefault() {
        return jdbcTemplate.query(
                TagQueries.SELECT_DEFAULT,
                tagDaoRowMapper.rowMapper
        );
    }

    @Override
    public Optional<TagEntity> findByName(String name) {
        List<TagEntity> results = jdbcTemplate.query(
                TagQueries.SELECT_BY_NAME,
                Map.of("name", name),
                tagDaoRowMapper.rowMapper
        );
        return results.stream().findFirst();
    }

    @Override
    public List<TagEntity> searchByNamePrefix(String prefix, int limit) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("prefix", prefix.trim() + "%");
        params.put("limit", limit > 0 ? limit : DEFAULT_SEARCH_LIMIT);

        return jdbcTemplate.query(
                TagQueries.SEARCH_BY_PREFIX,
                params,
                tagDaoRowMapper.rowMapper
        );
    }
}
