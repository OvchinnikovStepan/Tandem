package com.tandem.notification_service.api.model.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NotificationPreferencesUpdateResponseJson {
    NotificationPreferencesResponseJson preferences;
    Boolean updated;
}
