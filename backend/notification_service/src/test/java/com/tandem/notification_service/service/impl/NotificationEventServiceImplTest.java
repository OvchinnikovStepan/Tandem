package com.tandem.notification_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.notification_service.dal.NotificationDal;
import com.tandem.notification_service.service.NotificationDeliveryService;
import com.tandem.notification_service.service.NotificationTemplateService;
import com.tandem.notification_service.service.model.request.NotificationCreateRequest;
import com.tandem.notification_service.service.model.response.NotificationPreferencesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationEventServiceImplTest {

    @Mock
    private NotificationDal notificationDal;
    @Mock
    private NotificationDeliveryService notificationDeliveryService;
    @Mock
    private NotificationTemplateService notificationTemplateService;

    @InjectMocks
    private NotificationEventServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(service, "userRegisteredTopic", "topic.user-registered");
        ReflectionTestUtils.setField(service, "onboardingCompletedTopic", "topic.onboarding");
        ReflectionTestUtils.setField(service, "messageSentTopic", "topic.message-sent");
        ReflectionTestUtils.setField(service, "chatCreatedTopic", "topic.chat-created");
        ReflectionTestUtils.setField(service, "groupCreatedTopic", "topic.group-created");
        ReflectionTestUtils.setField(service, "memberJoinedTopic", "topic.member-joined");
        ReflectionTestUtils.setField(service, "memberBannedTopic", "topic.member-banned");
        ReflectionTestUtils.setField(service, "groupMessageSentTopic", "topic.group-message-sent");
    }

    @Test
    void processEventSkipsMalformedPayload() {
        service.processEvent("topic.message-sent", "not-json");

        verify(notificationDal, never()).createNotification(any());
        verify(notificationDeliveryService, never()).deliver(any(), any(), any(), any(), any());
    }

    @Test
    void processEventCreatesAndDeliversNotification() {
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        when(notificationDal.getPreferences(userId)).thenReturn(enabledPreferences(userId));
        when(notificationTemplateService.render(eq("message.received"), any(), any(), any(Map.class)))
                .thenReturn(new NotificationTemplateService.RenderedTemplate("Rendered title", "Rendered body", List.of("in-app", "push")));
        when(notificationDal.createNotification(any(NotificationCreateRequest.class))).thenReturn(notificationId);

        service.processEvent("topic.message-sent", "{\"recipientId\":\"" + userId + "\",\"message\":\"Hi\"}");

        ArgumentCaptor<NotificationCreateRequest> createCaptor = ArgumentCaptor.forClass(NotificationCreateRequest.class);
        verify(notificationDal).createNotification(createCaptor.capture());
        NotificationCreateRequest request = createCaptor.getValue();
        assertThat(request.getUserId()).isEqualTo(userId);
        assertThat(request.getType()).isEqualTo("message.received");
        assertThat(request.getTitle()).isEqualTo("Rendered title");
        assertThat(request.getBody()).isEqualTo("Rendered body");
        assertThat(request.getChannel()).isEqualTo("in-app");

        verify(notificationDeliveryService)
                .deliver(notificationId, userId, "Rendered title", "Rendered body", List.of("in-app", "push"));
    }

    @Test
    void processEventSkipsCreateWhenUserCannotBeResolved() {
        service.processEvent("topic.message-sent", "{\"message\":\"Hi\"}");

        verify(notificationDal, never()).getPreferences(any());
        verify(notificationDal, never()).createNotification(any());
        verify(notificationDeliveryService, never()).deliver(any(), any(), any(), any(), any());
    }

    @Test
    void processUserRegisteredMapsTopicAndBuildsWelcomeNotification() {
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        when(notificationDal.getPreferences(userId)).thenReturn(enabledPreferences(userId));
        when(notificationTemplateService.render(eq("user.registered"), any(), any(), any(Map.class)))
                .thenAnswer(inv -> new NotificationTemplateService.RenderedTemplate(
                        inv.getArgument(1),
                        inv.getArgument(2),
                        List.of("in-app")
                ));
        when(notificationDal.createNotification(any(NotificationCreateRequest.class))).thenReturn(notificationId);

        service.processEvent("topic.user-registered", "{\"userId\":\"" + userId + "\"}");

        ArgumentCaptor<NotificationCreateRequest> captor = ArgumentCaptor.forClass(NotificationCreateRequest.class);
        verify(notificationDal).createNotification(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo("user.registered");
        assertThat(captor.getValue().getTitle()).isEqualTo("Welcome to Tandem");
        verify(notificationDeliveryService).deliver(eq(notificationId), eq(userId), any(), any(), any());
    }

    @Test
    void processUnknownTopicUsesTopicAsTypeAndDefaultTitle() {
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        when(notificationDal.getPreferences(userId)).thenReturn(enabledPreferences(userId));
        when(notificationTemplateService.render(eq("custom.business.event"), any(), any(), any(Map.class)))
                .thenAnswer(inv -> new NotificationTemplateService.RenderedTemplate(
                        inv.getArgument(1),
                        inv.getArgument(2),
                        List.of("in-app")
                ));
        when(notificationDal.createNotification(any(NotificationCreateRequest.class))).thenReturn(notificationId);

        service.processEvent(
                "custom.business.event",
                "{\"userId\":\"" + userId + "\",\"message\":\"Hello custom\"}"
        );

        ArgumentCaptor<NotificationCreateRequest> captor = ArgumentCaptor.forClass(NotificationCreateRequest.class);
        verify(notificationDal).createNotification(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo("custom.business.event");
        assertThat(captor.getValue().getTitle()).isEqualTo("New Notification");
        assertThat(captor.getValue().getBody()).isEqualTo("Hello custom");
    }

    @Test
    void processSkipsWhenNoDeliveryChannelsEnabled() {
        UUID userId = UUID.randomUUID();
        when(notificationDal.getPreferences(userId)).thenReturn(allChannelsDisabledPreferences(userId));
        when(notificationTemplateService.render(eq("message.received"), any(), any(), any(Map.class)))
                .thenReturn(new NotificationTemplateService.RenderedTemplate("T", "B", List.of("in-app")));

        service.processEvent("topic.message-sent", "{\"recipientId\":\"" + userId + "\"}");

        verify(notificationDal, never()).createNotification(any());
        verify(notificationDeliveryService, never()).deliver(any(), any(), any(), any(), any());
    }

    @Test
    void processGroupMemberBannedAddsCriticalChannels() {
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        NotificationPreferencesResponse prefs = NotificationPreferencesResponse.builder()
                .userId(userId)
                .channels(NotificationPreferencesResponse.Channels.builder()
                        .inApp(false)
                        .push(false)
                        .email(false)
                        .sms(false)
                        .build())
                .categories(NotificationPreferencesResponse.Categories.builder()
                        .messages(NotificationPreferencesResponse.Category.builder().enabled(true).channels(List.of("in-app")).build())
                        .groups(NotificationPreferencesResponse.Category.builder().enabled(true).channels(List.of("in-app")).build())
                        .system(NotificationPreferencesResponse.Category.builder().enabled(true).channels(List.of("in-app")).build())
                        .build())
                .quietHours(NotificationPreferencesResponse.QuietHours.builder().enabled(false).build())
                .build();
        when(notificationDal.getPreferences(userId)).thenReturn(prefs);
        when(notificationTemplateService.render(eq("group.member.banned"), any(), any(), any(Map.class)))
                .thenReturn(new NotificationTemplateService.RenderedTemplate("Group Access Updated", "Moderation", List.of("in-app")));
        when(notificationDal.createNotification(any(NotificationCreateRequest.class))).thenReturn(notificationId);

        service.processEvent(
                "topic.member-banned",
                "{\"bannedUserId\":\"" + userId + "\"}"
        );

        verify(notificationDeliveryService).deliver(
                eq(notificationId),
                eq(userId),
                eq("Group Access Updated"),
                eq("Moderation"),
                eq(List.of("in-app", "email"))
        );
    }

    @Test
    void processChatCreatedDeliversForEachValidParticipant() {
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        UUID n1 = UUID.randomUUID();
        UUID n2 = UUID.randomUUID();
        when(notificationDal.getPreferences(any(UUID.class))).thenAnswer(inv -> enabledPreferences(inv.getArgument(0)));
        when(notificationTemplateService.render(eq("chat.created"), any(), any(), any(Map.class)))
                .thenReturn(new NotificationTemplateService.RenderedTemplate("T", "B", List.of("in-app")));
        when(notificationDal.createNotification(any(NotificationCreateRequest.class)))
                .thenReturn(n1, n2);

        service.processEvent(
                "topic.chat-created",
                "{\"participantIds\":[\"" + u1 + "\",\"" + u2 + "\",\"not-a-uuid\"]}"
        );

        verify(notificationDal, times(2)).createNotification(any(NotificationCreateRequest.class));
        verify(notificationDeliveryService, times(2)).deliver(any(), any(), any(), any(), any());
    }

    @Test
    void processChatCreatedDoesNothingWhenParticipantsMissing() {
        service.processEvent("topic.chat-created", "{\"chatId\":\"x\"}");

        verify(notificationDal, never()).createNotification(any());
        verify(notificationDeliveryService, never()).deliver(any(), any(), any(), any(), any());
    }

    @Test
    void categoryDisabledForcesInAppOnlyChannelList() {
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        NotificationPreferencesResponse prefs = NotificationPreferencesResponse.builder()
                .userId(userId)
                .channels(NotificationPreferencesResponse.Channels.builder()
                        .inApp(true)
                        .push(true)
                        .email(true)
                        .sms(false)
                        .build())
                .categories(NotificationPreferencesResponse.Categories.builder()
                        .messages(NotificationPreferencesResponse.Category.builder()
                                .enabled(false)
                                .channels(List.of("push"))
                                .build())
                        .groups(NotificationPreferencesResponse.Category.builder().enabled(true).channels(List.of("in-app", "push")).build())
                        .system(NotificationPreferencesResponse.Category.builder().enabled(true).channels(List.of("in-app")).build())
                        .build())
                .quietHours(NotificationPreferencesResponse.QuietHours.builder().enabled(false).build())
                .build();
        when(notificationDal.getPreferences(userId)).thenReturn(prefs);
        when(notificationTemplateService.render(eq("message.received"), any(), any(), any(Map.class)))
                .thenReturn(new NotificationTemplateService.RenderedTemplate("T", "B", List.of("push", "in-app")));
        when(notificationDal.createNotification(any(NotificationCreateRequest.class))).thenReturn(notificationId);

        service.processEvent("topic.message-sent", "{\"recipientId\":\"" + userId + "\"}");

        verify(notificationDeliveryService).deliver(eq(notificationId), eq(userId), any(), any(), eq(List.of("in-app")));
    }

    @Test
    void quietHoursInvalidTimesDoNotRestrictChannels() {
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        NotificationPreferencesResponse prefs = NotificationPreferencesResponse.builder()
                .userId(userId)
                .channels(NotificationPreferencesResponse.Channels.builder()
                        .inApp(true)
                        .push(true)
                        .email(false)
                        .sms(false)
                        .build())
                .categories(NotificationPreferencesResponse.Categories.builder()
                        .messages(NotificationPreferencesResponse.Category.builder().enabled(true).channels(List.of("in-app", "push")).build())
                        .groups(NotificationPreferencesResponse.Category.builder().enabled(true).channels(List.of("in-app")).build())
                        .system(NotificationPreferencesResponse.Category.builder().enabled(true).channels(List.of("in-app")).build())
                        .build())
                .quietHours(NotificationPreferencesResponse.QuietHours.builder()
                        .enabled(true)
                        .start("bad")
                        .end("also-bad")
                        .build())
                .build();
        when(notificationDal.getPreferences(userId)).thenReturn(prefs);
        when(notificationTemplateService.render(eq("message.received"), any(), any(), any(Map.class)))
                .thenReturn(new NotificationTemplateService.RenderedTemplate("T", "B", List.of("in-app", "push")));
        when(notificationDal.createNotification(any(NotificationCreateRequest.class))).thenReturn(notificationId);

        service.processEvent("topic.message-sent", "{\"recipientId\":\"" + userId + "\"}");

        verify(notificationDeliveryService).deliver(eq(notificationId), eq(userId), any(), any(), eq(List.of("in-app", "push")));
    }

    private NotificationPreferencesResponse enabledPreferences(UUID userId) {
        NotificationPreferencesResponse.Category category = NotificationPreferencesResponse.Category.builder()
                .enabled(true)
                .channels(List.of("in-app", "push", "email"))
                .build();

        return NotificationPreferencesResponse.builder()
                .userId(userId)
                .channels(NotificationPreferencesResponse.Channels.builder()
                        .inApp(true)
                        .push(true)
                        .email(true)
                        .sms(false)
                        .build())
                .categories(NotificationPreferencesResponse.Categories.builder()
                        .messages(category)
                        .groups(category)
                        .system(category)
                        .build())
                .quietHours(NotificationPreferencesResponse.QuietHours.builder()
                        .enabled(false)
                        .start(null)
                        .end(null)
                        .build())
                .build();
    }

    private NotificationPreferencesResponse allChannelsDisabledPreferences(UUID userId) {
        NotificationPreferencesResponse.Category emptyChannelsCategory = NotificationPreferencesResponse.Category.builder()
                .enabled(true)
                .channels(List.of())
                .build();
        return NotificationPreferencesResponse.builder()
                .userId(userId)
                .channels(NotificationPreferencesResponse.Channels.builder()
                        .inApp(false)
                        .push(false)
                        .email(false)
                        .sms(false)
                        .build())
                .categories(NotificationPreferencesResponse.Categories.builder()
                        .messages(emptyChannelsCategory)
                        .groups(emptyChannelsCategory)
                        .system(emptyChannelsCategory)
                        .build())
                .quietHours(NotificationPreferencesResponse.QuietHours.builder().enabled(false).build())
                .build();
    }
}
