package com.tandem.notification_service.dal.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.notification_service.dao.model.DeliveryStatusEntity;
import com.tandem.notification_service.dao.model.NotificationEntity;
import com.tandem.notification_service.dao.model.NotificationPreferenceEntity;
import com.tandem.notification_service.service.model.request.DeliveryStatusCreateRequest;
import com.tandem.notification_service.service.model.request.NotificationCreateRequest;
import com.tandem.notification_service.service.model.response.NotificationItemResponse;
import com.tandem.notification_service.service.model.response.NotificationPreferencesResponse;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class NotificationEntityMapper {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public NotificationEntity toEntity(NotificationCreateRequest request, ObjectMapper objectMapper) {
        return NotificationEntity.builder()
                .id(java.util.UUID.randomUUID())
                .userId(request.getUserId())
                .type(request.getType())
                .title(request.getTitle())
                .body(request.getBody())
                .data(toJson(objectMapper, request.getData()))
                .channel(request.getChannel())
                .read(false)
                .readAt(null)
                .deliveredAt(null)
                .createdAt(LocalDateTime.now())
                .expiresAt(request.getExpiresAt())
                .archivedAt(null)
                .build();
    }

    public DeliveryStatusEntity toEntity(DeliveryStatusCreateRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return DeliveryStatusEntity.builder()
                .id(java.util.UUID.randomUUID())
                .notificationId(request.getNotificationId())
                .channel(request.getChannel())
                .status(request.getStatus())
                .providerResponse(request.getProviderResponse())
                .attemptedAt(now)
                .deliveredAt("sent".equalsIgnoreCase(request.getStatus()) || "delivered".equalsIgnoreCase(request.getStatus()) ? now : null)
                .errorMessage(request.getErrorMessage())
                .retryCount(request.getRetryCount() == null ? 0 : request.getRetryCount())
                .createdAt(now)
                .build();
    }

    public NotificationItemResponse toResponse(NotificationEntity entity, ObjectMapper objectMapper) {
        Map<String, Object> payload = parseJsonMap(objectMapper, entity.getData());
        return NotificationItemResponse.builder()
                .notificationId(entity.getId())
                .type(entity.getType())
                .title(entity.getTitle())
                .body(entity.getBody())
                .data(payload)
                .read(entity.getRead())
                .readAt(entity.getReadAt())
                .channel(entity.getChannel())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public NotificationPreferencesResponse toResponse(NotificationPreferenceEntity entity, ObjectMapper objectMapper) {
        NotificationPreferencesResponse.Channels channels = NotificationPreferencesResponse.Channels.builder()
                .push(entity.getPushEnabled())
                .email(entity.getEmailEnabled())
                .sms(entity.getSmsEnabled())
                .inApp(entity.getInAppEnabled())
                .build();

        Map<String, Object> categoriesMap = parseJsonMap(objectMapper, entity.getPreferences());
        NotificationPreferencesResponse.Categories categories = NotificationPreferencesResponse.Categories.builder()
                .messages(mapCategory(categoriesMap, "messages"))
                .groups(mapCategory(categoriesMap, "groups"))
                .system(mapCategory(categoriesMap, "system"))
                .build();

        NotificationPreferencesResponse.QuietHours quietHours = NotificationPreferencesResponse.QuietHours.builder()
                .enabled(entity.getQuietHoursEnabled())
                .start(formatTime(entity.getQuietHoursStart()))
                .end(formatTime(entity.getQuietHoursEnd()))
                .build();

        return NotificationPreferencesResponse.builder()
                .userId(entity.getUserId())
                .channels(channels)
                .categories(categories)
                .quietHours(quietHours)
                .build();
    }

    public String toJson(ObjectMapper objectMapper, Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize JSON payload", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> parseJsonMap(ObjectMapper objectMapper, String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to parse JSON payload", e);
        }
    }

    @SuppressWarnings("unchecked")
    private NotificationPreferencesResponse.Category mapCategory(Map<String, Object> categoriesMap, String key) {
        Object rawValue = categoriesMap.get(key);
        if (!(rawValue instanceof Map<?, ?> categoryMap)) {
            return NotificationPreferencesResponse.Category.builder()
                    .enabled(true)
                    .channels(List.of("push", "in-app", "email"))
                    .build();
        }

        Object channelsValue = categoryMap.get("channels");
        List<String> channels = channelsValue instanceof List<?> list
                ? list.stream().map(String::valueOf).toList()
                : List.of("push", "in-app", "email");

        Object enabledValue = categoryMap.get("enabled");
        boolean enabled = !(enabledValue instanceof Boolean) || (Boolean) enabledValue;

        return NotificationPreferencesResponse.Category.builder()
                .enabled(enabled)
                .channels(channels)
                .build();
    }

    public Map<String, Object> defaultCategoriesMap() {
        Map<String, Object> categories = new LinkedHashMap<>();
        categories.put("messages", defaultCategoryTemplate());
        categories.put("groups", defaultCategoryTemplate());
        categories.put("system", defaultCategoryTemplate());
        return categories;
    }

    private Map<String, Object> defaultCategoryTemplate() {
        Map<String, Object> template = new LinkedHashMap<>();
        template.put("enabled", true);
        template.put("channels", List.of("push", "in-app", "email"));
        return template;
    }

    private String formatTime(LocalTime value) {
        return value == null ? null : value.format(TIME_FORMATTER);
    }
}
