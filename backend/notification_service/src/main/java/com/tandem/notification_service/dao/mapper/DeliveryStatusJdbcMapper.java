package com.tandem.notification_service.dao.mapper;

import com.tandem.notification_service.dao.model.DeliveryStatusEntity;
import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@UtilityClass
public class DeliveryStatusJdbcMapper {
    public SqlParameterSource mapInsertParams(DeliveryStatusEntity entity) {
        return new MapSqlParameterSource()
                .addValue("id", entity.getId())
                .addValue("notificationId", entity.getNotificationId())
                .addValue("channel", entity.getChannel())
                .addValue("status", entity.getStatus())
                .addValue("providerResponse", entity.getProviderResponse())
                .addValue("attemptedAt", entity.getAttemptedAt())
                .addValue("deliveredAt", entity.getDeliveredAt())
                .addValue("errorMessage", entity.getErrorMessage())
                .addValue("retryCount", entity.getRetryCount())
                .addValue("createdAt", entity.getCreatedAt());
    }
}
