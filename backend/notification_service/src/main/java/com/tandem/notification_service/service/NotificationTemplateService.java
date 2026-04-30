package com.tandem.notification_service.service;

import java.util.List;
import java.util.Map;

public interface NotificationTemplateService {
    RenderedTemplate render(String type, String defaultTitle, String defaultBody, Map<String, Object> data);

    record RenderedTemplate(String title, String body, List<String> channels) {
    }
}
