package com.tandem.notification_service.dao.impl;

import com.tandem.notification_service.dao.model.DeliveryStatusEntity;
import com.tandem.notification_service.dao.queries.DeliveryStatusQueries;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryStatusDaoImplTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private DeliveryStatusDaoImpl deliveryStatusDao;

    @Test
    void insertDelegatesToJdbc() {
        DeliveryStatusEntity entity = DeliveryStatusEntity.builder()
                .id(UUID.randomUUID())
                .notificationId(UUID.randomUUID())
                .channel("push")
                .status("pending")
                .providerResponse("queued")
                .attemptedAt(LocalDateTime.now())
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        deliveryStatusDao.insert(entity);

        verify(jdbcTemplate).update(eq(DeliveryStatusQueries.INSERT), any(SqlParameterSource.class));
    }
}
