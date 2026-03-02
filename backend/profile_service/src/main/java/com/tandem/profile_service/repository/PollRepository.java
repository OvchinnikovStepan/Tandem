package com.tandem.profile_service.repository;

import com.tandem.profile_service.model.Poll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PollRepository extends GeneralRepository<Poll> {

    @Autowired
    public PollRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, "polls", "id");
    }

    @Override
    protected RowMapper<Poll> getRowMapper() {
        return (rs, rowNum) -> Poll.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .version(rs.getInt("version"))
                .isActive(rs.getBoolean("is_active"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    public Poll save(Poll poll) {
        LocalDateTime now = LocalDateTime.now();
        if (poll.getId() == null) {
            poll.setId(UUID.randomUUID());
            poll.setCreatedAt(now);
            poll.setUpdatedAt(now);

            String sql = """
                INSERT INTO polls (id, name, version, is_active, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

            jdbcTemplate.update(sql,
                    poll.getId(),
                    poll.getName(),
                    poll.getVersion(),
                    poll.isActive(),
                    Timestamp.valueOf(poll.getCreatedAt()),
                    Timestamp.valueOf(poll.getUpdatedAt()));
        } else {
            poll.setUpdatedAt(now);

            String sql = """
                UPDATE polls SET
                    name = ?,
                    version = ?,
                    is_active = ?,
                    updated_at = ?
                WHERE id = ?
                """;

            jdbcTemplate.update(sql,
                    poll.getName(),
                    poll.getVersion(),
                    poll.isActive(),
                    Timestamp.valueOf(poll.getUpdatedAt()),
                    poll.getId());
        }
        return poll;
    }

    public Optional<Poll> findActiveByNameAndVersion(String name, int version) {
        String sql = "SELECT * FROM polls WHERE name = ? AND version = ? AND is_active = TRUE";
        try {
            Poll poll = jdbcTemplate.queryForObject(sql, getRowMapper(), name, version);
            return Optional.ofNullable(poll);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Poll> findLatestActive() {
        String sql = """
        SELECT * FROM polls
        WHERE is_active = TRUE
        ORDER BY created_at DESC
        LIMIT 1
        """;
        try {
            Poll poll = jdbcTemplate.queryForObject(sql, getRowMapper());
            return Optional.ofNullable(poll);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
