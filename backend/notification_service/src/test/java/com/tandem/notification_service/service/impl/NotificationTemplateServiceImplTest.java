package com.tandem.notification_service.service.impl;

import com.tandem.notification_service.dao.NotificationTemplateDao;
import com.tandem.notification_service.dao.model.NotificationTemplateEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationTemplateServiceImplTest {

    @Mock
    private NotificationTemplateDao notificationTemplateDao;

    @InjectMocks
    private NotificationTemplateServiceImpl service;

    @Test
    void renderReturnsDefaultsWhenTemplateNotFound() {
        when(notificationTemplateDao.findByType("message.received")).thenReturn(Optional.empty());

        var result = service.render("message.received", "Default title", "Default body", Map.of("name", "Alex"));

        assertThat(result.title()).isEqualTo("Default title");
        assertThat(result.body()).isEqualTo("Default body");
        assertThat(result.channels()).isEmpty();
    }

    @Test
    void renderAppliesTemplateVariablesAndChannels() {
        NotificationTemplateEntity template = NotificationTemplateEntity.builder()
                .id(UUID.randomUUID())
                .type("message.received")
                .titleTemplate("Hi {{name}}")
                .bodyTemplate("From {{sender}}: {{text}}")
                .channels(List.of("in-app", "push"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(notificationTemplateDao.findByType("message.received")).thenReturn(Optional.of(template));

        var result = service.render(
                "message.received",
                "Default title",
                "Default body",
                Map.of("name", "Alex", "sender", "Sam", "text", "Hello")
        );

        assertThat(result.title()).isEqualTo("Hi Alex");
        assertThat(result.body()).isEqualTo("From Sam: Hello");
        assertThat(result.channels()).containsExactly("in-app", "push");
    }

    @Test
    void renderReturnsEmptyStringWhenTemplatePatternBlank() {
        NotificationTemplateEntity template = NotificationTemplateEntity.builder()
                .id(UUID.randomUUID())
                .type("group.created")
                .titleTemplate(" ")
                .bodyTemplate(null)
                .channels(List.of("in-app"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(notificationTemplateDao.findByType("group.created")).thenReturn(Optional.of(template));

        var result = service.render("group.created", "ignored", "ignored", Map.of());

        assertThat(result.title()).isEmpty();
        assertThat(result.body()).isEmpty();
        assertThat(result.channels()).containsExactly("in-app");
    }
}
