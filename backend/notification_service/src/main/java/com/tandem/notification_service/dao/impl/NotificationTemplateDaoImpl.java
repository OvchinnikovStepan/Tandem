package com.tandem.notification_service.dao.impl;

import com.tandem.notification_service.dao.NotificationTemplateDao;
import com.tandem.notification_service.dao.mapper.NotificationTemplateDaoRowMapper;
import com.tandem.notification_service.dao.model.NotificationTemplateEntity;
import com.tandem.notification_service.dao.queries.NotificationTemplateQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationTemplateDaoImpl implements NotificationTemplateDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final NotificationTemplateDaoRowMapper rowMapper;

    @Override
    public Optional<NotificationTemplateEntity> findByType(String type) {
        List<NotificationTemplateEntity> rows = jdbcTemplate.query(
                NotificationTemplateQueries.SELECT_BY_TYPE,
                Map.of("type", type),
                rowMapper.rowMapper
        );
        return rows.stream().findFirst();
    }
}
