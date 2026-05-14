package com.tandem.notification_service.api.mapper;

import com.tandem.notification_service.api.model.request.NotificationPreferencesUpdateRequestJson;
import com.tandem.notification_service.api.model.response.*;
import com.tandem.notification_service.service.model.request.NotificationPreferencesUpdateRequest;
import com.tandem.notification_service.service.model.response.*;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class NotificationApiMapper {

    public NotificationListResponseJson toJson(NotificationListResponse response) {
        return NotificationListResponseJson.builder()
                .notifications(mapNotifications(response.getNotifications()))
                .total(response.getTotal())
                .unreadCount(response.getUnreadCount())
                .page(response.getPage())
                .limit(response.getLimit())
                .build();
    }

    public NotificationUnreadResponseJson toJson(NotificationUnreadResponse response) {
        return NotificationUnreadResponseJson.builder()
                .count(response.getCount())
                .notifications(mapNotifications(response.getNotifications()))
                .build();
    }

    public NotificationReadResponseJson toJson(NotificationReadResponse response) {
        return NotificationReadResponseJson.builder()
                .read(response.getRead())
                .readAt(response.getReadAt())
                .build();
    }

    public NotificationPreferencesResponseJson toJson(NotificationPreferencesResponse response) {
        NotificationPreferencesResponseJson.Channels channels = NotificationPreferencesResponseJson.Channels.builder()
                .push(response.getChannels().getPush())
                .email(response.getChannels().getEmail())
                .sms(response.getChannels().getSms())
                .inApp(response.getChannels().getInApp())
                .build();

        NotificationPreferencesResponseJson.Categories categories = NotificationPreferencesResponseJson.Categories.builder()
                .messages(mapCategory(response.getCategories().getMessages()))
                .groups(mapCategory(response.getCategories().getGroups()))
                .system(mapCategory(response.getCategories().getSystem()))
                .build();

        NotificationPreferencesResponseJson.QuietHours quietHours = NotificationPreferencesResponseJson.QuietHours.builder()
                .enabled(response.getQuietHours().getEnabled())
                .start(response.getQuietHours().getStart())
                .end(response.getQuietHours().getEnd())
                .build();

        return NotificationPreferencesResponseJson.builder()
                .userId(response.getUserId())
                .channels(channels)
                .categories(categories)
                .quietHours(quietHours)
                .build();
    }

    public NotificationPreferencesUpdateRequest toServiceRequest(NotificationPreferencesUpdateRequestJson request) {
        return NotificationPreferencesUpdateRequest.builder()
                .channels(request.getChannels() == null ? null : NotificationPreferencesUpdateRequest.Channels.builder()
                        .push(request.getChannels().getPush())
                        .email(request.getChannels().getEmail())
                        .sms(request.getChannels().getSms())
                        .inApp(request.getChannels().getInApp())
                        .build())
                .categories(request.getCategories() == null ? null : NotificationPreferencesUpdateRequest.Categories.builder()
                        .messages(mapCategoryRequest(request.getCategories().getMessages()))
                        .groups(mapCategoryRequest(request.getCategories().getGroups()))
                        .system(mapCategoryRequest(request.getCategories().getSystem()))
                        .build())
                .quietHours(request.getQuietHours() == null ? null : NotificationPreferencesUpdateRequest.QuietHours.builder()
                        .enabled(request.getQuietHours().getEnabled())
                        .start(request.getQuietHours().getStart())
                        .end(request.getQuietHours().getEnd())
                        .build())
                .build();
    }

    private NotificationPreferencesResponseJson.Category mapCategory(NotificationPreferencesResponse.Category category) {
        return NotificationPreferencesResponseJson.Category.builder()
                .enabled(category.getEnabled())
                .channels(category.getChannels())
                .build();
    }

    private NotificationPreferencesUpdateRequest.Category mapCategoryRequest(NotificationPreferencesUpdateRequestJson.Category category) {
        if (category == null) {
            return null;
        }
        return NotificationPreferencesUpdateRequest.Category.builder()
                .enabled(category.getEnabled())
                .channels(category.getChannels())
                .build();
    }

    private List<NotificationResponseJson> mapNotifications(List<NotificationItemResponse> notifications) {
        return notifications.stream()
                .map(NotificationApiMapper::toJson)
                .toList();
    }

    private NotificationResponseJson toJson(NotificationItemResponse response) {
        return NotificationResponseJson.builder()
                .notificationId(response.getNotificationId())
                .type(response.getType())
                .title(response.getTitle())
                .body(response.getBody())
                .data(response.getData())
                .read(response.getRead())
                .readAt(response.getReadAt())
                .channel(response.getChannel())
                .createdAt(response.getCreatedAt())
                .build();
    }
}
