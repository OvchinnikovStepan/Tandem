package com.tandem.notification_service.dao.impl;

import com.tandem.notification_service.dao.mapper.NotificationTemplateDaoRowMapper;
import com.tandem.notification_service.dao.model.NotificationTemplateEntity;
import com.tandem.notification_service.dao.queries.NotificationTemplateQueries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationTemplateDaoImplTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final NotificationTemplateDaoRowMapper rowMapper = new NotificationTemplateDaoRowMapper();

    private NotificationTemplateDaoImpl dao;

    @BeforeEach
    void setUp() {
        dao = new NotificationTemplateDaoImpl(jdbcTemplate, rowMapper);
    }

    @Test
    void findByTypeReturnsFirstRow() {
        NotificationTemplateEntity entity = NotificationTemplateEntity.builder()
                .id(UUID.randomUUID())
                .type("message.received")
                .titleTemplate("Hi")
                .bodyTemplate("Body")
                .channels(List.of("in-app"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(jdbcTemplate.query(
                eq(NotificationTemplateQueries.SELECT_BY_TYPE),
                anyMap(),
                any(RowMapper.class)
        )).thenReturn(List.of(entity));

        Optional<NotificationTemplateEntity> result = dao.findByType("message.received");

        assertThat(result).contains(entity);
    }

    @Test
    void findByTypeReturnsEmptyWhenNoRows() {
        when(jdbcTemplate.query(
                eq(NotificationTemplateQueries.SELECT_BY_TYPE),
                anyMap(),
                any(RowMapper.class)
        )).thenReturn(List.of());

        assertThat(dao.findByType("unknown")).isEmpty();
    }
}
