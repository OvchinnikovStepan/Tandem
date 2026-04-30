package com.tandem.notification_service.service.model.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class NotificationUnreadResponse {
    Long count;
    List<NotificationItemResponse> notifications;
}
