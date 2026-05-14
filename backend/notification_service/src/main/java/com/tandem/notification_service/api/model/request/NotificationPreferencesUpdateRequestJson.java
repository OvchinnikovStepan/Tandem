package com.tandem.notification_service.api.model.request;

import lombok.Data;

import java.util.List;

@Data
public class NotificationPreferencesUpdateRequestJson {
    private Channels channels;
    private Categories categories;
    private QuietHours quietHours;

    @Data
    public static class Channels {
        private Boolean push;
        private Boolean email;
        private Boolean sms;
        private Boolean inApp;
    }

    @Data
    public static class Category {
        private Boolean enabled;
        private List<String> channels;
    }

    @Data
    public static class Categories {
        private Category messages;
        private Category groups;
        private Category system;
    }

    @Data
    public static class QuietHours {
        private Boolean enabled;
        private String start;
        private String end;
    }
}
