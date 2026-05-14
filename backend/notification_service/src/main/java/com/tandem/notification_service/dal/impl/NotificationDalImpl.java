package com.tandem.notification_service.dal.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.notification_service.cache.NotificationCacheService;
import com.tandem.notification_service.dal.NotificationDal;
import com.tandem.notification_service.dal.mapper.NotificationEntityMapper;
import com.tandem.notification_service.dao.DeliveryStatusDao;
import com.tandem.notification_service.dao.NotificationDao;
import com.tandem.notification_service.dao.NotificationPreferenceDao;
import com.tandem.notification_service.dao.model.NotificationEntity;
import com.tandem.notification_service.dao.model.NotificationPreferenceEntity;
import com.tandem.notification_service.service.model.request.DeliveryStatusCreateRequest;
import com.tandem.notification_service.service.model.request.NotificationCreateRequest;
import com.tandem.notification_service.service.model.request.NotificationPreferencesUpdateRequest;
import com.tandem.notification_service.service.model.response.NotificationItemResponse;
import com.tandem.notification_service.service.model.response.NotificationListResponse;
import com.tandem.notification_service.service.model.response.NotificationPreferencesResponse;
import com.tandem.notification_service.service.model.response.NotificationReadResponse;
import com.tandem.notification_service.service.model.response.NotificationUnreadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NotificationDalImpl implements NotificationDal {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;
    private static final int UNREAD_PREVIEW_LIMIT = 20;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final NotificationDao notificationDao;
    private final DeliveryStatusDao deliveryStatusDao;
    private final NotificationPreferenceDao notificationPreferenceDao;
    private final NotificationCacheService cacheService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public UUID createNotification(NotificationCreateRequest request) {
        NotificationEntity entity = NotificationEntityMapper.toEntity(request, objectMapper);
        notificationDao.insert(entity);
        cacheService.incrementUnreadCount(request.getUserId());
        return entity.getId();
    }

    @Override
    @Transactional
    public void createDeliveryStatus(DeliveryStatusCreateRequest request) {
        deliveryStatusDao.insert(NotificationEntityMapper.toEntity(request));
    }

    @Override
    public NotificationListResponse getNotifications(UUID userId, Integer page, Integer limit, String type, Boolean read) {
        int safePage = page == null || page < 1 ? DEFAULT_PAGE : page;
        int safeLimit = limit == null || limit < 1 ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT);
        int offset = (safePage - 1) * safeLimit;

        List<NotificationItemResponse> notifications = notificationDao.findByUserId(userId, safeLimit, offset, type, read)
                .stream()
                .map(entity -> NotificationEntityMapper.toResponse(entity, objectMapper))
                .collect(Collectors.toList());

        long unreadCount = cacheService.getUnreadCount(userId)
                .orElseGet(() -> {
                    long calculated = notificationDao.countUnreadByUserId(userId);
                    cacheService.setUnreadCount(userId, calculated);
                    return calculated;
                });

        return NotificationListResponse.builder()
                .notifications(notifications)
                .total(notificationDao.countByUserId(userId, type, read))
                .unreadCount(unreadCount)
                .page(safePage)
                .limit(safeLimit)
                .build();
    }

    @Override
    public NotificationUnreadResponse getUnreadNotifications(UUID userId) {
        List<NotificationItemResponse> unreadNotifications = notificationDao.findUnreadByUserId(userId, UNREAD_PREVIEW_LIMIT)
                .stream()
                .map(entity -> NotificationEntityMapper.toResponse(entity, objectMapper))
                .collect(Collectors.toList());

        long unreadCount = cacheService.getUnreadCount(userId)
                .orElseGet(() -> {
                    long calculated = notificationDao.countUnreadByUserId(userId);
                    cacheService.setUnreadCount(userId, calculated);
                    return calculated;
                });

        return NotificationUnreadResponse.builder()
                .count(unreadCount)
                .notifications(unreadNotifications)
                .build();
    }

    @Override
    @Transactional
    public NotificationReadResponse markAsRead(UUID userId, UUID notificationId) {
        LocalDateTime readAt = LocalDateTime.now();
        boolean updated = notificationDao.markAsRead(notificationId, userId, readAt);

        if (!updated) {
            Optional<NotificationEntity> existing = notificationDao.findByIdAndUserId(notificationId, userId);
            if (existing.isPresent()) {
                return NotificationReadResponse.builder()
                        .read(existing.get().getRead())
                        .readAt(existing.get().getReadAt())
                        .build();
            }
            throw new IllegalArgumentException("Notification not found for user");
        }
        cacheService.evictUnreadCount(userId);

        return NotificationReadResponse.builder()
                .read(true)
                .readAt(readAt)
                .build();
    }

    @Override
    @Transactional
    public int markAllAsRead(UUID userId) {
        int marked = notificationDao.markAllAsRead(userId, LocalDateTime.now());
        cacheService.setUnreadCount(userId, 0);
        return marked;
    }

    @Override
    @Transactional
    public boolean delete(UUID userId, UUID notificationId) {
        boolean deleted = notificationDao.softDelete(notificationId, userId, LocalDateTime.now());
        if (deleted) {
            cacheService.evictUnreadCount(userId);
        }
        return deleted;
    }

    @Override
    @Transactional
    public NotificationPreferencesResponse getPreferences(UUID userId) {
        Optional<NotificationPreferencesResponse> fromCache = cacheService.getPreferences(userId);
        if (fromCache.isPresent()) {
            return fromCache.get();
        }

        NotificationPreferenceEntity preferences = notificationPreferenceDao.findByUserId(userId)
                .orElseGet(() -> {
                    NotificationPreferenceEntity created = defaultPreferences(userId);
                    notificationPreferenceDao.insert(created);
                    return created;
                });
        NotificationPreferencesResponse response = NotificationEntityMapper.toResponse(preferences, objectMapper);
        cacheService.setPreferences(userId, response);
        return response;
    }

    @Override
    @Transactional
    public NotificationPreferencesResponse updatePreferences(UUID userId, NotificationPreferencesUpdateRequest request) {
        NotificationPreferenceEntity current = notificationPreferenceDao.findByUserId(userId)
                .orElseGet(() -> {
                    NotificationPreferenceEntity created = defaultPreferences(userId);
                    notificationPreferenceDao.insert(created);
                    return created;
                });

        NotificationPreferenceEntity updated = mergePreferences(current, request);
        notificationPreferenceDao.update(updated);
        NotificationPreferencesResponse response = NotificationEntityMapper.toResponse(updated, objectMapper);
        cacheService.setPreferences(userId, response);
        return response;
    }

    private NotificationPreferenceEntity defaultPreferences(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        return NotificationPreferenceEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .pushEnabled(true)
                .emailEnabled(true)
                .smsEnabled(false)
                .inAppEnabled(true)
                .preferences(NotificationEntityMapper.toJson(objectMapper, NotificationEntityMapper.defaultCategoriesMap()))
                .quietHoursEnabled(false)
                .quietHoursStart(null)
                .quietHoursEnd(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private NotificationPreferenceEntity mergePreferences(
            NotificationPreferenceEntity current,
            NotificationPreferencesUpdateRequest request
    ) {
        NotificationPreferencesUpdateRequest.Channels channelsRequest = request.getChannels();
        NotificationPreferencesUpdateRequest.QuietHours quietHoursRequest = request.getQuietHours();

        boolean pushEnabled = channelsRequest != null && channelsRequest.getPush() != null
                ? channelsRequest.getPush()
                : current.getPushEnabled();
        boolean emailEnabled = channelsRequest != null && channelsRequest.getEmail() != null
                ? channelsRequest.getEmail()
                : current.getEmailEnabled();
        boolean smsEnabled = channelsRequest != null && channelsRequest.getSms() != null
                ? channelsRequest.getSms()
                : current.getSmsEnabled();
        boolean inAppEnabled = channelsRequest != null && channelsRequest.getInApp() != null
                ? channelsRequest.getInApp()
                : current.getInAppEnabled();

        Map<String, Object> categories = NotificationEntityMapper.parseJsonMap(objectMapper, current.getPreferences());
        if (request.getCategories() != null) {
            categories = mergeCategories(categories, request.getCategories());
        }

        boolean quietEnabled = quietHoursRequest != null && quietHoursRequest.getEnabled() != null
                ? quietHoursRequest.getEnabled()
                : current.getQuietHoursEnabled();

        LocalTime quietStart = parseTimeOrDefault(
                quietHoursRequest != null ? quietHoursRequest.getStart() : null,
                current.getQuietHoursStart()
        );
        LocalTime quietEnd = parseTimeOrDefault(
                quietHoursRequest != null ? quietHoursRequest.getEnd() : null,
                current.getQuietHoursEnd()
        );

        return current.toBuilder()
                .pushEnabled(pushEnabled)
                .emailEnabled(emailEnabled)
                .smsEnabled(smsEnabled)
                .inAppEnabled(inAppEnabled)
                .preferences(NotificationEntityMapper.toJson(objectMapper, categories))
                .quietHoursEnabled(quietEnabled)
                .quietHoursStart(quietStart)
                .quietHoursEnd(quietEnd)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> mergeCategories(
            Map<String, Object> currentCategories,
            NotificationPreferencesUpdateRequest.Categories requestCategories
    ) {
        Map<String, Object> merged = currentCategories.isEmpty()
                ? NotificationEntityMapper.defaultCategoriesMap()
                : new java.util.LinkedHashMap<>(currentCategories);

        mergeCategory(merged, "messages", requestCategories.getMessages());
        mergeCategory(merged, "groups", requestCategories.getGroups());
        mergeCategory(merged, "system", requestCategories.getSystem());
        return merged;
    }

    @SuppressWarnings("unchecked")
    private void mergeCategory(
            Map<String, Object> merged,
            String key,
            NotificationPreferencesUpdateRequest.Category update
    ) {
        if (update == null) {
            return;
        }

        Map<String, Object> existing = merged.get(key) instanceof Map<?, ?> map
                ? new java.util.LinkedHashMap<>((Map<String, Object>) map)
                : new java.util.LinkedHashMap<>();

        if (update.getEnabled() != null) {
            existing.put("enabled", update.getEnabled());
        } else if (!existing.containsKey("enabled")) {
            existing.put("enabled", true);
        }

        if (update.getChannels() != null) {
            existing.put("channels", update.getChannels());
        } else if (!existing.containsKey("channels")) {
            existing.put("channels", List.of("push", "in-app", "email"));
        }

        merged.put(key, existing);
    }

    private LocalTime parseTimeOrDefault(String value, LocalTime defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return LocalTime.parse(value, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            log.warn("Ignoring invalid quiet hours value: {}", value);
            return defaultValue;
        }
    }
}
