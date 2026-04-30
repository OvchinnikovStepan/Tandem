package com.tandem.notification_service.service.impl;

import com.tandem.notification_service.dao.NotificationTemplateDao;
import com.tandem.notification_service.dao.model.NotificationTemplateEntity;
import com.tandem.notification_service.service.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private final NotificationTemplateDao notificationTemplateDao;

    @Override
    public RenderedTemplate render(String type, String defaultTitle, String defaultBody, Map<String, Object> data) {
        NotificationTemplateEntity template = notificationTemplateDao.findByType(type).orElse(null);
        if (template == null) {
            return new RenderedTemplate(defaultTitle, defaultBody, List.of());
        }

        String title = applyTemplate(template.getTitleTemplate(), data);
        String body = applyTemplate(template.getBodyTemplate(), data);
        return new RenderedTemplate(title, body, template.getChannels());
    }

    private String applyTemplate(String pattern, Map<String, Object> data) {
        if (pattern == null || pattern.isBlank()) {
            return "";
        }
        String result = pattern;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String token = "{{" + entry.getKey() + "}}";
            result = result.replace(token, String.valueOf(entry.getValue()));
        }
        return result;
    }
}
