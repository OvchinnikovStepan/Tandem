package com.tandem.notification_service.dal.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.notification_service.dao.model.NotificationEntity;
import com.tandem.notification_service.dao.model.NotificationPreferenceEntity;
import com.tandem.notification_service.service.model.request.DeliveryStatusCreateRequest;
import com.tandem.notification_service.service.model.request.NotificationCreateRequest;
import com.tandem.notification_service.service.model.response.NotificationPreferencesResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationEntityMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void toEntityMapsNotificationCreateRequest() {
        UUID userId = UUID.randomUUID();
        NotificationCreateRequest request = NotificationCreateRequest.builder()
                .userId(userId)
                .type("message.received")
                .title("Title")
                .body("Body")
                .data(Map.of("msg", "hello"))
                .channel("in-app")
                .expiresAt(LocalDateTime.now().plusDays(2))
                .build();

        NotificationEntity entity = NotificationEntityMapper.toEntity(request, objectMapper);

        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getRead()).isFalse();
        assertThat(entity.getData()).contains("msg");
    }

    @Test
    void toEntityMapsDeliveryStatusAndSetsDeliveredAtForSentStatus() {
        DeliveryStatusCreateRequest request = DeliveryStatusCreateRequest.builder()
                .notificationId(UUID.randomUUID())
                .channel("email")
                .status("sent")
                .providerResponse("ok")
                .retryCount(null)
                .build();

        var entity = NotificationEntityMapper.toEntity(request);

        assertThat(entity.getDeliveredAt()).isNotNull();
        assertThat(entity.getRetryCount()).isZero();
    }

    @Test
    void toResponseMapsPreferencesWithDefaultsForMissingCategory() {
        NotificationPreferenceEntity entity = NotificationPreferenceEntity.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .pushEnabled(true)
                .emailEnabled(false)
                .smsEnabled(false)
                .inAppEnabled(true)
                .preferences("{\"messages\":{\"enabled\":false,\"channels\":[\"in-app\"]}}")
                .quietHoursEnabled(true)
                .quietHoursStart(LocalTime.of(22, 0))
                .quietHoursEnd(LocalTime.of(7, 0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        NotificationPreferencesResponse response = NotificationEntityMapper.toResponse(entity, objectMapper);

        assertThat(response.getCategories().getMessages().getEnabled()).isFalse();
        assertThat(response.getCategories().getMessages().getChannels()).containsExactly("in-app");
        assertThat(response.getCategories().getGroups().getEnabled()).isTrue();
        assertThat(response.getCategories().getGroups().getChannels()).containsExactly("push", "in-app", "email");
        assertThat(response.getQuietHours().getStart()).isEqualTo("22:00");
    }

    @Test
    void parseJsonMapThrowsForInvalidJson() {
        assertThatThrownBy(() -> NotificationEntityMapper.parseJsonMap(objectMapper, "{bad-json"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unable to parse JSON payload");
    }

    @Test
    void defaultCategoriesMapContainsExpectedKeys() {
        Map<String, Object> defaults = NotificationEntityMapper.defaultCategoriesMap();

        assertThat(defaults.keySet()).containsExactly("messages", "groups", "system");
        assertThat(defaults.values()).allSatisfy(value -> {
            Map<?, ?> category = (Map<?, ?>) value;
            assertThat(category.get("enabled")).isEqualTo(true);
            assertThat(category.get("channels")).isEqualTo(List.of("push", "in-app", "email"));
        });
    }
}
