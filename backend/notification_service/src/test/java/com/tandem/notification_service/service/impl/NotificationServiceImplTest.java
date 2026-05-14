package com.tandem.notification_service.service.impl;

import com.tandem.notification_service.dal.NotificationDal;
import com.tandem.notification_service.service.model.request.NotificationPreferencesUpdateRequest;
import com.tandem.notification_service.service.model.response.NotificationListResponse;
import com.tandem.notification_service.service.model.response.NotificationPreferencesResponse;
import com.tandem.notification_service.service.model.response.NotificationReadResponse;
import com.tandem.notification_service.service.model.response.NotificationUnreadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationDal notificationDal;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void getNotificationsDelegatesToDal() {
        UUID userId = UUID.randomUUID();
        NotificationListResponse expected = NotificationListResponse.builder().page(1).limit(10).build();
        when(notificationDal.getNotifications(userId, 1, 10, "message.received", false)).thenReturn(expected);

        NotificationListResponse result = notificationService.getNotifications(userId, 1, 10, "message.received", false);

        assertThat(result).isSameAs(expected);
        verify(notificationDal).getNotifications(userId, 1, 10, "message.received", false);
    }

    @Test
    void getUnreadNotificationsDelegatesToDal() {
        UUID userId = UUID.randomUUID();
        NotificationUnreadResponse expected = NotificationUnreadResponse.builder().count(5L).build();
        when(notificationDal.getUnreadNotifications(userId)).thenReturn(expected);

        NotificationUnreadResponse result = notificationService.getUnreadNotifications(userId);

        assertThat(result).isSameAs(expected);
        verify(notificationDal).getUnreadNotifications(userId);
    }

    @Test
    void markAsReadDelegatesToDal() {
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        NotificationReadResponse expected = NotificationReadResponse.builder().read(true).build();
        when(notificationDal.markAsRead(userId, notificationId)).thenReturn(expected);

        NotificationReadResponse result = notificationService.markAsRead(userId, notificationId);

        assertThat(result).isSameAs(expected);
        verify(notificationDal).markAsRead(userId, notificationId);
    }

    @Test
    void markAllAsReadDelegatesToDal() {
        UUID userId = UUID.randomUUID();
        when(notificationDal.markAllAsRead(userId)).thenReturn(4);

        int result = notificationService.markAllAsRead(userId);

        assertThat(result).isEqualTo(4);
        verify(notificationDal).markAllAsRead(userId);
    }

    @Test
    void deleteDelegatesToDal() {
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        when(notificationDal.delete(userId, notificationId)).thenReturn(true);

        boolean result = notificationService.delete(userId, notificationId);

        assertThat(result).isTrue();
        verify(notificationDal).delete(userId, notificationId);
    }

    @Test
    void getPreferencesDelegatesToDal() {
        UUID userId = UUID.randomUUID();
        NotificationPreferencesResponse expected = NotificationPreferencesResponse.builder().userId(userId).build();
        when(notificationDal.getPreferences(userId)).thenReturn(expected);

        NotificationPreferencesResponse result = notificationService.getPreferences(userId);

        assertThat(result).isSameAs(expected);
        verify(notificationDal).getPreferences(userId);
    }

    @Test
    void updatePreferencesDelegatesToDal() {
        UUID userId = UUID.randomUUID();
        NotificationPreferencesUpdateRequest request = NotificationPreferencesUpdateRequest.builder().build();
        NotificationPreferencesResponse expected = NotificationPreferencesResponse.builder().userId(userId).build();
        when(notificationDal.updatePreferences(userId, request)).thenReturn(expected);

        NotificationPreferencesResponse result = notificationService.updatePreferences(userId, request);

        assertThat(result).isSameAs(expected);
        verify(notificationDal).updatePreferences(userId, request);
    }
}
