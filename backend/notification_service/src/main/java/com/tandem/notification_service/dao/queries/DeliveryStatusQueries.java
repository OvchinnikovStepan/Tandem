package com.tandem.notification_service.dao.queries;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DeliveryStatusQueries {
    public static final String INSERT = """
            INSERT INTO delivery_status (
                id,
                notification_id,
                channel,
                status,
                provider_response,
                attempted_at,
                delivered_at,
                error_message,
                retry_count,
                created_at
            ) VALUES (
                :id,
                :notificationId,
                :channel,
                :status,
                :providerResponse,
                :attemptedAt,
                :deliveredAt,
                :errorMessage,
                :retryCount,
                :createdAt
            )
            """;
}
