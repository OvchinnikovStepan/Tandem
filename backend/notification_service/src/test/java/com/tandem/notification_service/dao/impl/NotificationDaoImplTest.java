package com.tandem.notification_service.dao.impl;

import com.tandem.notification_service.dao.mapper.NotificationDaoRowMapper;
import com.tandem.notification_service.dao.model.NotificationEntity;
import com.tandem.notification_service.dao.queries.NotificationQueries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDaoImplTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final NotificationDaoRowMapper notificationDaoRowMapper = new NotificationDaoRowMapper();

    private NotificationDaoImpl notificationDao;

    @BeforeEach
    void setUp() {
        notificationDao = new NotificationDaoImpl(jdbcTemplate, notificationDaoRowMapper);
    }

    @Test
    void insertDelegatesToJdbc() {
        NotificationEntity entity = sampleEntity();

        notificationDao.insert(entity);

        verify(jdbcTemplate).update(eq(NotificationQueries.INSERT), any(SqlParameterSource.class));
    }

    @Test
    void findByUserIdReturnsQueryResult() {
        UUID userId = UUID.randomUUID();
        NotificationEntity entity = sampleEntity();
        when(jdbcTemplate.query(
                eq(NotificationQueries.SELECT_BY_USER_ID),
                any(SqlParameterSource.class),
                eq(notificationDaoRowMapper.rowMapper)
        )).thenReturn(List.of(entity));

        List<NotificationEntity> result = notificationDao.findByUserId(userId, 10, 0, "t", false);

        assertThat(result).containsExactly(entity);
    }

    @Test
    void countByUserIdReturnsLongValue() {
        UUID userId = UUID.randomUUID();
        when(jdbcTemplate.queryForObject(
                eq(NotificationQueries.COUNT_BY_USER_ID),
                any(SqlParameterSource.class),
                eq(Long.class)
        )).thenReturn(42L);

        assertThat(notificationDao.countByUserId(userId, null, null)).isEqualTo(42L);
    }

    @Test
    void countByUserIdReturnsZeroWhenNull() {
        UUID userId = UUID.randomUUID();
        when(jdbcTemplate.queryForObject(
                eq(NotificationQueries.COUNT_BY_USER_ID),
                any(SqlParameterSource.class),
                eq(Long.class)
        )).thenReturn(null);

        assertThat(notificationDao.countByUserId(userId, "type", true)).isZero();
    }

    @Test
    void countUnreadByUserIdReturnsZeroWhenNull() {
        UUID userId = UUID.randomUUID();
        when(jdbcTemplate.queryForObject(
                eq(NotificationQueries.COUNT_UNREAD_BY_USER_ID),
                anyMap(),
                eq(Long.class)
        )).thenReturn(null);

        assertThat(notificationDao.countUnreadByUserId(userId)).isZero();
    }

    @Test
    void findUnreadByUserIdReturnsRows() {
        UUID userId = UUID.randomUUID();
        NotificationEntity entity = sampleEntity();
        when(jdbcTemplate.query(
                eq(NotificationQueries.SELECT_UNREAD_BY_USER_ID),
                anyMap(),
                any(RowMapper.class)
        )).thenReturn(List.of(entity));

        assertThat(notificationDao.findUnreadByUserId(userId, 5)).containsExactly(entity);
    }

    @Test
    void findByIdAndUserIdReturnsFirstOptional() {
        UUID nid = UUID.randomUUID();
        UUID uid = UUID.randomUUID();
        NotificationEntity entity = sampleEntity();
        when(jdbcTemplate.query(
                eq(NotificationQueries.SELECT_BY_ID_AND_USER_ID),
                anyMap(),
                any(RowMapper.class)
        )).thenReturn(List.of(entity));

        Optional<NotificationEntity> found = notificationDao.findByIdAndUserId(nid, uid);

        assertThat(found).contains(entity);
    }

    @Test
    void findByIdAndUserIdReturnsEmptyWhenNoRows() {
        UUID nid = UUID.randomUUID();
        UUID uid = UUID.randomUUID();
        when(jdbcTemplate.query(
                eq(NotificationQueries.SELECT_BY_ID_AND_USER_ID),
                anyMap(),
                any(RowMapper.class)
        )).thenReturn(List.of());

        assertThat(notificationDao.findByIdAndUserId(nid, uid)).isEmpty();
    }

    @Test
    void markAsReadReturnsTrueWhenUpdated() {
        UUID nid = UUID.randomUUID();
        UUID uid = UUID.randomUUID();
        LocalDateTime readAt = LocalDateTime.now();
        when(jdbcTemplate.update(eq(NotificationQueries.MARK_AS_READ), any(SqlParameterSource.class))).thenReturn(1);

        assertThat(notificationDao.markAsRead(nid, uid, readAt)).isTrue();
    }

    @Test
    void markAsReadReturnsFalseWhenNoRowUpdated() {
        UUID nid = UUID.randomUUID();
        UUID uid = UUID.randomUUID();
        when(jdbcTemplate.update(eq(NotificationQueries.MARK_AS_READ), any(SqlParameterSource.class))).thenReturn(0);

        assertThat(notificationDao.markAsRead(nid, uid, LocalDateTime.now())).isFalse();
    }

    @Test
    void markAllAsReadReturnsUpdateCount() {
        UUID uid = UUID.randomUUID();
        LocalDateTime readAt = LocalDateTime.now();
        when(jdbcTemplate.update(eq(NotificationQueries.MARK_ALL_AS_READ), any(SqlParameterSource.class))).thenReturn(7);

        assertThat(notificationDao.markAllAsRead(uid, readAt)).isEqualTo(7);
    }

    @Test
    void softDeleteReturnsUpdatedFlag() {
        UUID nid = UUID.randomUUID();
        UUID uid = UUID.randomUUID();
        when(jdbcTemplate.update(eq(NotificationQueries.SOFT_DELETE), any(SqlParameterSource.class))).thenReturn(1);

        assertThat(notificationDao.softDelete(nid, uid, LocalDateTime.now())).isTrue();

        when(jdbcTemplate.update(eq(NotificationQueries.SOFT_DELETE), any(SqlParameterSource.class))).thenReturn(0);

        assertThat(notificationDao.softDelete(nid, uid, LocalDateTime.now())).isFalse();
    }

    private NotificationEntity sampleEntity() {
        return NotificationEntity.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .type("message.received")
                .title("t")
                .body("b")
                .data("{}")
                .channel("in-app")
                .read(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
    }
}
