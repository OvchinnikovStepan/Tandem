package com.tandem.notification_service.api.mapper;

import com.tandem.notification_service.api.model.request.NotificationPreferencesUpdateRequestJson;
import com.tandem.notification_service.api.model.response.NotificationListResponseJson;
import com.tandem.notification_service.service.model.request.NotificationPreferencesUpdateRequest;
import com.tandem.notification_service.service.model.response.NotificationItemResponse;
import com.tandem.notification_service.service.model.response.NotificationListResponse;
import com.tandem.notification_service.service.model.response.NotificationPreferencesResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationApiMapperTest {

    @Test
    void toJsonMapsListResponse() {
        NotificationItemResponse item = NotificationItemResponse.builder()
                .notificationId(UUID.randomUUID())
                .type("message.received")
                .title("Title")
                .body("Body")
                .data(Map.of("k", "v"))
                .read(false)
                .channel("in-app")
                .createdAt(LocalDateTime.now())
                .build();
        NotificationListResponse response = NotificationListResponse.builder()
                .notifications(List.of(item))
                .total(10L)
                .unreadCount(3L)
                .page(1)
                .limit(20)
                .build();

        NotificationListResponseJson json = NotificationApiMapper.toJson(response);

        assertThat(json.getTotal()).isEqualTo(10L);
        assertThat(json.getUnreadCount()).isEqualTo(3L);
        assertThat(json.getNotifications()).hasSize(1);
        assertThat(json.getNotifications().get(0).getType()).isEqualTo("message.received");
    }

    @Test
    void toJsonMapsPreferencesResponse() {
        NotificationPreferencesResponse.Category category = NotificationPreferencesResponse.Category.builder()
                .enabled(true)
                .channels(List.of("push", "in-app"))
                .build();
        NotificationPreferencesResponse response = NotificationPreferencesResponse.builder()
                .userId(UUID.randomUUID())
                .channels(NotificationPreferencesResponse.Channels.builder()
                        .push(true)
                        .email(true)
                        .sms(false)
                        .inApp(true)
                        .build())
                .categories(NotificationPreferencesResponse.Categories.builder()
                        .messages(category)
                        .groups(category)
                        .system(category)
                        .build())
                .quietHours(NotificationPreferencesResponse.QuietHours.builder()
                        .enabled(true)
                        .start("22:00")
                        .end("07:00")
                        .build())
                .build();

        var json = NotificationApiMapper.toJson(response);

        assertThat(json.getChannels().getInApp()).isTrue();
        assertThat(json.getCategories().getMessages().getChannels()).containsExactly("push", "in-app");
        assertThat(json.getQuietHours().getStart()).isEqualTo("22:00");
    }

    @Test
    void toServiceRequestMapsNestedFields() {
        NotificationPreferencesUpdateRequestJson requestJson = new NotificationPreferencesUpdateRequestJson();
        NotificationPreferencesUpdateRequestJson.Channels channels = new NotificationPreferencesUpdateRequestJson.Channels();
        channels.setEmail(true);
        requestJson.setChannels(channels);

        NotificationPreferencesUpdateRequestJson.Category groups = new NotificationPreferencesUpdateRequestJson.Category();
        groups.setEnabled(false);
        groups.setChannels(List.of("in-app"));
        NotificationPreferencesUpdateRequestJson.Categories categories = new NotificationPreferencesUpdateRequestJson.Categories();
        categories.setGroups(groups);
        requestJson.setCategories(categories);

        NotificationPreferencesUpdateRequestJson.QuietHours quietHours = new NotificationPreferencesUpdateRequestJson.QuietHours();
        quietHours.setEnabled(true);
        quietHours.setStart("23:00");
        quietHours.setEnd("06:00");
        requestJson.setQuietHours(quietHours);

        NotificationPreferencesUpdateRequest request = NotificationApiMapper.toServiceRequest(requestJson);

        assertThat(request.getChannels().getEmail()).isTrue();
        assertThat(request.getCategories().getGroups().getEnabled()).isFalse();
        assertThat(request.getCategories().getGroups().getChannels()).containsExactly("in-app");
        assertThat(request.getQuietHours().getStart()).isEqualTo("23:00");
    }
}
