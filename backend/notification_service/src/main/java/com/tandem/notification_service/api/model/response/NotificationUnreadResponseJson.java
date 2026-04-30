package com.tandem.notification_service.api.model.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class NotificationUnreadResponseJson {
    Long count;
    List<NotificationResponseJson> notifications;
}
