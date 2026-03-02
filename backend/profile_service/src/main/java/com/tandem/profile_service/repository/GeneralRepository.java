package com.tandem.profile_service.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public abstract class GeneralRepository<T> {

    protected final JdbcTemplate jdbcTemplate;
    protected final String tableName;
    protected final String idColumn;
    protected final String userIdColumn;

    protected GeneralRepository(JdbcTemplate jdbcTemplate, String tableName,
                                String idColumn, String userIdColumn) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableName = tableName;
        this.idColumn = idColumn;
        this.userIdColumn = userIdColumn;
    }

    protected GeneralRepository(JdbcTemplate jdbcTemplate, String tableName, String idColumn) {
        this(jdbcTemplate, tableName, idColumn, "user_id");
    }

    protected abstract RowMapper<T> getRowMapper();

    public Optional<T> findById(UUID id) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", tableName, idColumn);
        try {
            T entity = jdbcTemplate.queryForObject(sql, getRowMapper(), id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<T> findByUserId(UUID userId) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", tableName, userIdColumn);
        try {
            T entity = jdbcTemplate.queryForObject(sql, getRowMapper(), userId);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<T> findAll() {
        String sql = String.format("SELECT * FROM %s", tableName);
        return jdbcTemplate.query(sql, getRowMapper());
    }

    public List<T> findAll(String orderBy) {
        String sql = String.format("SELECT * FROM %s ORDER BY %s", tableName, orderBy);
        return jdbcTemplate.query(sql, getRowMapper());
    }

    public boolean deleteById(UUID id) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?", tableName, idColumn);
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }

    public boolean deleteByUserId(UUID userId) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?", tableName, userIdColumn);
        int affectedRows = jdbcTemplate.update(sql, userId);
        return affectedRows > 0;
    }

    protected UUID generateId() {
        return UUID.randomUUID();
    }
}