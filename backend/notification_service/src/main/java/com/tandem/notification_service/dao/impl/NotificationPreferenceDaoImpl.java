package com.tandem.notification_service.dao.impl;

import com.tandem.notification_service.dao.NotificationPreferenceDao;
import com.tandem.notification_service.dao.mapper.NotificationPreferenceDaoRowMapper;
import com.tandem.notification_service.dao.mapper.NotificationPreferenceJdbcMapper;
import com.tandem.notification_service.dao.model.NotificationPreferenceEntity;
import com.tandem.notification_service.dao.queries.NotificationPreferenceQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NotificationPreferenceDaoImpl implements NotificationPreferenceDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final NotificationPreferenceDaoRowMapper rowMapper;

    @Override
    public Optional<NotificationPreferenceEntity> findByUserId(UUID userId) {
        List<NotificationPreferenceEntity> rows = jdbcTemplate.query(
                NotificationPreferenceQueries.SELECT_BY_USER_ID,
                Map.of("userId", userId),
                rowMapper.rowMapper
        );
        return rows.stream().findFirst();
    }

    @Override
    public void insert(NotificationPreferenceEntity entity) {
        jdbcTemplate.update(
                NotificationPreferenceQueries.INSERT,
                NotificationPreferenceJdbcMapper.mapEntityParams(entity)
        );
    }

    @Override
    public void update(NotificationPreferenceEntity entity) {
        jdbcTemplate.update(
                NotificationPreferenceQueries.UPDATE,
                NotificationPreferenceJdbcMapper.mapEntityParams(entity)
        );
    }
}
