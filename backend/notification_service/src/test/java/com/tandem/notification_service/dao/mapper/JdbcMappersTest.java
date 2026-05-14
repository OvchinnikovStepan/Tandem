package com.tandem.notification_service.dao.mapper;

import com.tandem.notification_service.dao.model.DeliveryStatusEntity;
import com.tandem.notification_service.dao.model.NotificationEntity;
import com.tandem.notification_service.dao.model.NotificationPreferenceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcMappersTest {

    @Test
    void notificationJdbcMapperMapsInsertAndFilterParams() {
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        NotificationEntity entity = NotificationEntity.builder()
                .id(notificationId)
                .userId(userId)
                .type("message.received")
                .title("title")
                .body("body")
                .data("{}")
                .channel("in-app")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        SqlParameterSource insert = NotificationJdbcMapper.mapInsertParams(entity);
        SqlParameterSource filter = NotificationJdbcMapper.mapFilterParams(userId, 20, 0, "message.received", false);

        assertThat(insert.getValue("id")).isEqualTo(notificationId);
        assertThat(insert.getValue("channel")).isEqualTo("in-app");
        assertThat(filter.getValue("userId")).isEqualTo(userId);
        assertThat(filter.getValue("limit")).isEqualTo(20);
        assertThat(filter.getValue("readValue")).isEqualTo(false);
    }

    @Test
    void notificationPreferenceJdbcMapperMapsEntityParams() {
        UUID userId = UUID.randomUUID();
        NotificationPreferenceEntity entity = NotificationPreferenceEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .pushEnabled(true)
                .emailEnabled(false)
                .smsEnabled(false)
                .inAppEnabled(true)
                .preferences("{}")
                .quietHoursEnabled(true)
                .quietHoursStart(LocalTime.of(23, 0))
                .quietHoursEnd(LocalTime.of(6, 0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SqlParameterSource params = NotificationPreferenceJdbcMapper.mapEntityParams(entity);

        assertThat(params.getValue("userId")).isEqualTo(userId);
        assertThat(params.getValue("pushEnabled")).isEqualTo(true);
        assertThat(params.getValue("quietHoursStart")).isEqualTo(LocalTime.of(23, 0));
    }

    @Test
    void deliveryStatusJdbcMapperMapsInsertParams() {
        UUID notificationId = UUID.randomUUID();
        DeliveryStatusEntity entity = DeliveryStatusEntity.builder()
                .id(UUID.randomUUID())
                .notificationId(notificationId)
                .channel("push")
                .status("pending")
                .providerResponse("queued")
                .retryCount(1)
                .createdAt(LocalDateTime.now())
                .build();

        SqlParameterSource params = DeliveryStatusJdbcMapper.mapInsertParams(entity);

        assertThat(params.getValue("notificationId")).isEqualTo(notificationId);
        assertThat(params.getValue("channel")).isEqualTo("push");
        assertThat(params.getValue("status")).isEqualTo("pending");
        assertThat(params.getValue("retryCount")).isEqualTo(1);
    }
}
