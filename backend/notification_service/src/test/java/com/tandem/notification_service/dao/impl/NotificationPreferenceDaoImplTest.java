package com.tandem.notification_service.dao.impl;

import com.tandem.notification_service.dao.mapper.NotificationPreferenceDaoRowMapper;
import com.tandem.notification_service.dao.model.NotificationPreferenceEntity;
import com.tandem.notification_service.dao.queries.NotificationPreferenceQueries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationPreferenceDaoImplTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final NotificationPreferenceDaoRowMapper rowMapper = new NotificationPreferenceDaoRowMapper();

    private NotificationPreferenceDaoImpl dao;

    @BeforeEach
    void setUp() {
        dao = new NotificationPreferenceDaoImpl(jdbcTemplate, rowMapper);
    }

    @Test
    void findByUserIdReturnsFirstRow() {
        UUID userId = UUID.randomUUID();
        NotificationPreferenceEntity entity = sample(userId);
        when(jdbcTemplate.query(
                eq(NotificationPreferenceQueries.SELECT_BY_USER_ID),
                anyMap(),
                any(RowMapper.class)
        )).thenReturn(List.of(entity));

        assertThat(dao.findByUserId(userId)).contains(entity);
    }

    @Test
    void findByUserIdReturnsEmptyWhenNoRows() {
        UUID userId = UUID.randomUUID();
        when(jdbcTemplate.query(
                eq(NotificationPreferenceQueries.SELECT_BY_USER_ID),
                anyMap(),
                any(RowMapper.class)
        )).thenReturn(List.of());

        assertThat(dao.findByUserId(userId)).isEmpty();
    }

    @Test
    void insertDelegatesToJdbc() {
        NotificationPreferenceEntity entity = sample(UUID.randomUUID());

        dao.insert(entity);

        verify(jdbcTemplate).update(eq(NotificationPreferenceQueries.INSERT), any(SqlParameterSource.class));
    }

    @Test
    void updateDelegatesToJdbc() {
        NotificationPreferenceEntity entity = sample(UUID.randomUUID());

        dao.update(entity);

        verify(jdbcTemplate).update(eq(NotificationPreferenceQueries.UPDATE), any(SqlParameterSource.class));
    }

    private NotificationPreferenceEntity sample(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        return NotificationPreferenceEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .pushEnabled(true)
                .emailEnabled(true)
                .smsEnabled(false)
                .inAppEnabled(true)
                .preferences("{}")
                .quietHoursEnabled(false)
                .quietHoursStart(LocalTime.of(22, 0))
                .quietHoursEnd(LocalTime.of(7, 0))
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
