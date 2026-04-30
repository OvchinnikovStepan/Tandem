package com.tandem.notification_service.service.model.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class NotificationPreferencesResponse {
    UUID userId;
    Channels channels;
    Categories categories;
    QuietHours quietHours;

    @Value
    @Builder
    public static class Channels {
        Boolean push;
        Boolean email;
        Boolean sms;
        Boolean inApp;
    }

    @Value
    @Builder
    public static class Category {
        Boolean enabled;
        List<String> channels;
    }

    @Value
    @Builder
    public static class Categories {
        Category messages;
        Category groups;
        Category system;
    }

    @Value
    @Builder
    public static class QuietHours {
        Boolean enabled;
        String start;
        String end;
    }
}
