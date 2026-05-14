package com.tandem.notification_service.dao.impl;

import com.tandem.notification_service.dao.NotificationDao;
import com.tandem.notification_service.dao.mapper.NotificationDaoRowMapper;
import com.tandem.notification_service.dao.mapper.NotificationJdbcMapper;
import com.tandem.notification_service.dao.model.NotificationEntity;
import com.tandem.notification_service.dao.queries.NotificationQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NotificationDaoImpl implements NotificationDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final NotificationDaoRowMapper notificationDaoRowMapper;

    @Override
    public void insert(NotificationEntity entity) {
        jdbcTemplate.update(
                NotificationQueries.INSERT,
                NotificationJdbcMapper.mapInsertParams(entity)
        );
    }

    @Override
    public List<NotificationEntity> findByUserId(UUID userId, Integer limit, Integer offset, String type, Boolean read) {
        return jdbcTemplate.query(
                NotificationQueries.SELECT_BY_USER_ID,
                NotificationJdbcMapper.mapFilterParams(userId, limit, offset, type, read),
                notificationDaoRowMapper.rowMapper
        );
    }

    @Override
    public long countByUserId(UUID userId, String type, Boolean read) {
        Long count = jdbcTemplate.queryForObject(
                NotificationQueries.COUNT_BY_USER_ID,
                NotificationJdbcMapper.mapFilterParams(userId, 0, 0, type, read),
                Long.class
        );
        return count == null ? 0L : count;
    }

    @Override
    public long countUnreadByUserId(UUID userId) {
        Long count = jdbcTemplate.queryForObject(
                NotificationQueries.COUNT_UNREAD_BY_USER_ID,
                Map.of("userId", userId),
                Long.class
        );
        return count == null ? 0L : count;
    }

    @Override
    public List<NotificationEntity> findUnreadByUserId(UUID userId, Integer limit) {
        return jdbcTemplate.query(
                NotificationQueries.SELECT_UNREAD_BY_USER_ID,
                Map.of("userId", userId, "limit", limit),
                notificationDaoRowMapper.rowMapper
        );
    }

    @Override
    public Optional<NotificationEntity> findByIdAndUserId(UUID notificationId, UUID userId) {
        List<NotificationEntity> result = jdbcTemplate.query(
                NotificationQueries.SELECT_BY_ID_AND_USER_ID,
                Map.of("notificationId", notificationId, "userId", userId),
                notificationDaoRowMapper.rowMapper
        );
        return result.stream().findFirst();
    }

    @Override
    public boolean markAsRead(UUID notificationId, UUID userId, LocalDateTime readAt) {
        int updated = jdbcTemplate.update(
                NotificationQueries.MARK_AS_READ,
                NotificationJdbcMapper.mapMarkReadParams(notificationId, userId, readAt)
        );
        return updated > 0;
    }

    @Override
    public int markAllAsRead(UUID userId, LocalDateTime readAt) {
        return jdbcTemplate.update(
                NotificationQueries.MARK_ALL_AS_READ,
                NotificationJdbcMapper.mapMarkAllReadParams(userId, readAt)
        );
    }

    @Override
    public boolean softDelete(UUID notificationId, UUID userId, LocalDateTime archivedAt) {
        int updated = jdbcTemplate.update(
                NotificationQueries.SOFT_DELETE,
                NotificationJdbcMapper.mapSoftDeleteParams(notificationId, userId, archivedAt)
        );
        return updated > 0;
    }
}
