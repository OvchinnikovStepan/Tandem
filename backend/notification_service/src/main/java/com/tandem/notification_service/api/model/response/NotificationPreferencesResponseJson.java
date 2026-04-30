package com.tandem.notification_service.api.model.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class NotificationPreferencesResponseJson {
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
