package com.tandem.notification_service.dal.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.notification_service.cache.NotificationCacheService;
import com.tandem.notification_service.dao.DeliveryStatusDao;
import com.tandem.notification_service.dao.NotificationDao;
import com.tandem.notification_service.dao.NotificationPreferenceDao;
import com.tandem.notification_service.dao.model.NotificationEntity;
import com.tandem.notification_service.dao.model.NotificationPreferenceEntity;
import com.tandem.notification_service.service.model.request.NotificationPreferencesUpdateRequest;
import com.tandem.notification_service.service.model.response.NotificationListResponse;
import com.tandem.notification_service.service.model.response.NotificationPreferencesResponse;
import com.tandem.notification_service.service.model.response.NotificationReadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDalImplTest {

    @Mock
    private NotificationDao notificationDao;
    @Mock
    private DeliveryStatusDao deliveryStatusDao;
    @Mock
    private NotificationPreferenceDao notificationPreferenceDao;
    @Mock
    private NotificationCacheService cacheService;

    @InjectMocks
    private NotificationDalImpl notificationDal;

    @Test
    void getNotificationsUsesSafePagingAndCachesUnreadCountOnMiss() {
        ObjectMapper objectMapper = new ObjectMapper();
        notificationDal = new NotificationDalImpl(notificationDao, deliveryStatusDao, notificationPreferenceDao, cacheService, objectMapper);
        UUID userId = UUID.randomUUID();
        when(notificationDao.findByUserId(userId, 20, 0, "message.received", false))
                .thenReturn(List.of(notificationEntity(userId)));
        when(cacheService.getUnreadCount(userId)).thenReturn(Optional.empty());
        when(notificationDao.countUnreadByUserId(userId)).thenReturn(3L);
        when(notificationDao.countByUserId(userId, "message.received", false)).thenReturn(11L);

        NotificationListResponse response = notificationDal.getNotifications(userId, null, -1, "message.received", false);

        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getLimit()).isEqualTo(20);
        assertThat(response.getUnreadCount()).isEqualTo(3L);
        assertThat(response.getTotal()).isEqualTo(11L);
        assertThat(response.getNotifications()).hasSize(1);
        verify(cacheService).setUnreadCount(userId, 3L);
    }

    @Test
    void markAsReadReturnsExistingStateWhenAlreadyRead() {
        ObjectMapper objectMapper = new ObjectMapper();
        notificationDal = new NotificationDalImpl(notificationDao, deliveryStatusDao, notificationPreferenceDao, cacheService, objectMapper);
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        LocalDateTime readAt = LocalDateTime.now().minusHours(1);
        when(notificationDao.markAsRead(any(), any(), any())).thenReturn(false);
        when(notificationDao.findByIdAndUserId(notificationId, userId))
                .thenReturn(Optional.of(notificationEntity(userId).toBuilder().id(notificationId).read(true).readAt(readAt).build()));

        NotificationReadResponse response = notificationDal.markAsRead(userId, notificationId);

        assertThat(response.getRead()).isTrue();
        assertThat(response.getReadAt()).isEqualTo(readAt);
        verify(cacheService, never()).evictUnreadCount(userId);
    }

    @Test
    void markAsReadThrowsWhenNotificationMissing() {
        ObjectMapper objectMapper = new ObjectMapper();
        notificationDal = new NotificationDalImpl(notificationDao, deliveryStatusDao, notificationPreferenceDao, cacheService, objectMapper);
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        when(notificationDao.markAsRead(any(), any(), any())).thenReturn(false);
        when(notificationDao.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationDal.markAsRead(userId, notificationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Notification not found");
    }

    @Test
    void getPreferencesReturnsCacheValueWhenPresent() {
        ObjectMapper objectMapper = new ObjectMapper();
        notificationDal = new NotificationDalImpl(notificationDao, deliveryStatusDao, notificationPreferenceDao, cacheService, objectMapper);
        UUID userId = UUID.randomUUID();
        NotificationPreferencesResponse cached = NotificationPreferencesResponse.builder().userId(userId).build();
        when(cacheService.getPreferences(userId)).thenReturn(Optional.of(cached));

        NotificationPreferencesResponse response = notificationDal.getPreferences(userId);

        assertThat(response).isSameAs(cached);
        verify(notificationPreferenceDao, never()).findByUserId(userId);
    }

    @Test
    void getPreferencesCreatesDefaultWhenRecordMissing() {
        ObjectMapper objectMapper = new ObjectMapper();
        notificationDal = new NotificationDalImpl(notificationDao, deliveryStatusDao, notificationPreferenceDao, cacheService, objectMapper);
        UUID userId = UUID.randomUUID();
        when(cacheService.getPreferences(userId)).thenReturn(Optional.empty());
        when(notificationPreferenceDao.findByUserId(userId)).thenReturn(Optional.empty());

        NotificationPreferencesResponse response = notificationDal.getPreferences(userId);

        ArgumentCaptor<NotificationPreferenceEntity> captor = ArgumentCaptor.forClass(NotificationPreferenceEntity.class);
        verify(notificationPreferenceDao).insert(captor.capture());
        NotificationPreferenceEntity inserted = captor.getValue();
        assertThat(inserted.getUserId()).isEqualTo(userId);
        assertThat(inserted.getPushEnabled()).isTrue();
        assertThat(response.getChannels().getInApp()).isTrue();
        verify(cacheService).setPreferences(userId, response);
    }

    @Test
    void updatePreferencesMergesIncomingFieldsAndSavesResult() {
        ObjectMapper objectMapper = new ObjectMapper();
        notificationDal = new NotificationDalImpl(notificationDao, deliveryStatusDao, notificationPreferenceDao, cacheService, objectMapper);
        UUID userId = UUID.randomUUID();
        NotificationPreferenceEntity current = NotificationPreferenceEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .pushEnabled(true)
                .emailEnabled(false)
                .smsEnabled(false)
                .inAppEnabled(true)
                .preferences("{\"messages\":{\"enabled\":true,\"channels\":[\"push\",\"in-app\"]}}")
                .quietHoursEnabled(false)
                .quietHoursStart(LocalTime.of(22, 0))
                .quietHoursEnd(LocalTime.of(7, 0))
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
        when(notificationPreferenceDao.findByUserId(userId)).thenReturn(Optional.of(current));
        NotificationPreferencesUpdateRequest request = NotificationPreferencesUpdateRequest.builder()
                .channels(NotificationPreferencesUpdateRequest.Channels.builder().email(true).build())
                .quietHours(NotificationPreferencesUpdateRequest.QuietHours.builder().enabled(true).start("09:30").end("invalid").build())
                .categories(NotificationPreferencesUpdateRequest.Categories.builder()
                        .groups(NotificationPreferencesUpdateRequest.Category.builder()
                                .enabled(false)
                                .channels(List.of("in-app"))
                                .build())
                        .build())
                .build();

        NotificationPreferencesResponse response = notificationDal.updatePreferences(userId, request);

        ArgumentCaptor<NotificationPreferenceEntity> captor = ArgumentCaptor.forClass(NotificationPreferenceEntity.class);
        verify(notificationPreferenceDao).update(captor.capture());
        NotificationPreferenceEntity updated = captor.getValue();
        assertThat(updated.getEmailEnabled()).isTrue();
        assertThat(updated.getQuietHoursEnabled()).isTrue();
        assertThat(updated.getQuietHoursStart()).isEqualTo(LocalTime.of(9, 30));
        assertThat(updated.getQuietHoursEnd()).isEqualTo(LocalTime.of(7, 0));
        assertThat(response.getCategories().getGroups().getEnabled()).isFalse();
        assertThat(response.getCategories().getGroups().getChannels()).containsExactly("in-app");
        verify(cacheService).setPreferences(userId, response);
    }

    private NotificationEntity notificationEntity(UUID userId) {
        return NotificationEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type("message.received")
                .title("Title")
                .body("Body")
                .data(toJson(Map.of("k", "v")))
                .channel("in-app")
                .read(false)
                .readAt(null)
                .deliveredAt(null)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .archivedAt(null)
                .build();
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return new ObjectMapper().writeValueAsString(payload);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
