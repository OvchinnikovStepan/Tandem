package com.tandem.notification_service.dao.impl;

import com.tandem.notification_service.dao.DeliveryStatusDao;
import com.tandem.notification_service.dao.mapper.DeliveryStatusJdbcMapper;
import com.tandem.notification_service.dao.model.DeliveryStatusEntity;
import com.tandem.notification_service.dao.queries.DeliveryStatusQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeliveryStatusDaoImpl implements DeliveryStatusDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void insert(DeliveryStatusEntity entity) {
        jdbcTemplate.update(
                DeliveryStatusQueries.INSERT,
                DeliveryStatusJdbcMapper.mapInsertParams(entity)
        );
    }
}
