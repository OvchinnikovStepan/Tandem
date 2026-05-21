package com.tandem.interest_service.dao.impl;

import com.tandem.interest_service.dao.DirectoryDao;
import com.tandem.interest_service.dao.mapper.DirectoryDaoRowMapper;
import com.tandem.interest_service.dao.model.GroupDirectoryEntity;
import com.tandem.interest_service.dao.model.UserDirectoryEntity;
import com.tandem.interest_service.dao.queries.DirectoryQueries;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class DirectoryDaoImpl implements DirectoryDao {

    private static final int DEFAULT_SEARCH_LIMIT = 10;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DirectoryDaoRowMapper rowMapper;

    public DirectoryDaoImpl(NamedParameterJdbcTemplate jdbcTemplate, DirectoryDaoRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public void upsertUser(UUID userId, String displayName) {
        jdbcTemplate.update(
                DirectoryQueries.UPSERT_USER,
                Map.of("userId", userId, "displayName", displayName)
        );
    }

    @Override
    public void upsertGroup(UUID groupId, String name) {
        jdbcTemplate.update(
                DirectoryQueries.UPSERT_GROUP,
                Map.of("groupId", groupId, "name", name)
        );
    }

    @Override
    public List<UserDirectoryEntity> searchUsersByNameFragment(String fragment, int limit) {
        if (fragment == null || fragment.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("fragment", fragment);
        params.put("limit", limit > 0 ? limit : DEFAULT_SEARCH_LIMIT);

        return jdbcTemplate.query(
                DirectoryQueries.SEARCH_USERS_BY_FRAGMENT,
                params,
                rowMapper.userRowMapper
        );
    }

    @Override
    public List<GroupDirectoryEntity> searchGroupsByNameFragment(String fragment, int limit) {
        if (fragment == null || fragment.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("fragment", fragment);
        params.put("limit", limit > 0 ? limit : DEFAULT_SEARCH_LIMIT);

        return jdbcTemplate.query(
                DirectoryQueries.SEARCH_GROUPS_BY_FRAGMENT,
                params,
                rowMapper.groupRowMapper
        );
    }
}
