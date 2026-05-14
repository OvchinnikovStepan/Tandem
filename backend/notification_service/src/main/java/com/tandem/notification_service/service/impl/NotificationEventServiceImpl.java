package com.tandem.notification_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.notification_service.dal.NotificationDal;
import com.tandem.notification_service.service.NotificationDeliveryService;
import com.tandem.notification_service.service.NotificationEventService;
import com.tandem.notification_service.service.NotificationTemplateService;
import com.tandem.notification_service.service.model.request.NotificationCreateRequest;
import com.tandem.notification_service.service.model.response.NotificationPreferencesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventServiceImpl implements NotificationEventService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Value("${tandem.kafka.topic.user-registered}")
    private String userRegisteredTopic;
    @Value("${tandem.kafka.topic.profile-onboarding-completed}")
    private String onboardingCompletedTopic;
    @Value("${tandem.kafka.topic.message-sent}")
    private String messageSentTopic;
    @Value("${tandem.kafka.topic.chat-created}")
    private String chatCreatedTopic;
    @Value("${tandem.kafka.topic.group-created}")
    private String groupCreatedTopic;
    @Value("${tandem.kafka.topic.member-joined}")
    private String memberJoinedTopic;
    @Value("${tandem.kafka.topic.member-banned}")
    private String memberBannedTopic;
    @Value("${tandem.kafka.topic.group-message-sent}")
    private String groupMessageSentTopic;

    private final NotificationDal notificationDal;
    private final NotificationDeliveryService notificationDeliveryService;
    private final NotificationTemplateService notificationTemplateService;
    private final ObjectMapper objectMapper;

    @Override
    public void processEvent(String topic, String payload) {
        JsonNode node = readPayload(payload);
        if (node == null) {
            return;
        }

        if (topic.equals(chatCreatedTopic)) {
            processChatCreated(node);
            return;
        }

        UUID userId = resolveTargetUserId(topic, node);
        if (userId == null) {
            log.warn("Cannot resolve target user for topic={} payload={}", topic, payload);
            return;
        }

        String type = resolveType(topic);
        String title = buildTitle(type, node);
        String body = buildBody(type, node);
        Map<String, Object> data = objectToMap(node);

        createAndDeliver(userId, type, title, body, data);
    }

    @Transactional
    protected void createAndDeliver(UUID userId, String type, String title, String body, Map<String, Object> data) {
        NotificationPreferencesResponse preferences = notificationDal.getPreferences(userId);
        NotificationTemplateService.RenderedTemplate rendered = notificationTemplateService.render(type, title, body, data);
        List<String> channels = resolveChannels(preferences, type, rendered.channels());
        if (channels.isEmpty()) {
            log.info("Notification skipped by preferences userId={} type={}", userId, type);
            return;
        }

        UUID notificationId = notificationDal.createNotification(NotificationCreateRequest.builder()
                .userId(userId)
                .type(type)
                .title(rendered.title())
                .body(rendered.body())
                .data(data)
                .channel("in-app")
                .expiresAt(LocalDateTime.now().plusDays(90))
                .build());

        notificationDeliveryService.deliver(notificationId, userId, rendered.title(), rendered.body(), channels);
    }

    private void processChatCreated(JsonNode node) {
        JsonNode participants = firstPresent(node, "participants", "participantIds", "userIds", "memberIds");
        if (participants == null || !participants.isArray()) {
            log.warn("chat.created payload does not contain participants array");
            return;
        }

        for (JsonNode participant : participants) {
            UUID userId = tryParseUuid(participant.asText(null));
            if (userId == null) {
                continue;
            }
            createAndDeliver(
                    userId,
                    "chat.created",
                    "New Chat Created",
                    "You were added to a new chat",
                    objectToMap(node)
            );
        }
    }

    private JsonNode readPayload(String payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse event payload", e);
            return null;
        }
    }

    private UUID resolveTargetUserId(String topic, JsonNode node) {
        if (topic.equals(userRegisteredTopic) || topic.equals(onboardingCompletedTopic)) {
            return extractUuid(node, "userId");
        }
        if (topic.equals(messageSentTopic)) {
            return extractUuid(node, "recipientId", "userId");
        }
        if (topic.equals(groupCreatedTopic)) {
            return extractUuid(node, "creatorId", "userId");
        }
        if (topic.equals(memberJoinedTopic)) {
            return extractUuid(node, "memberId", "userId");
        }
        if (topic.equals(memberBannedTopic)) {
            return extractUuid(node, "bannedUserId", "userId", "memberId");
        }
        if (topic.equals(groupMessageSentTopic)) {
            return extractUuid(node, "recipientId", "userId", "memberId");
        }
        return extractUuid(node, "userId");
    }

    private String resolveType(String topic) {
        if (topic.equals(userRegisteredTopic)) {
            return "user.registered";
        }
        if (topic.equals(onboardingCompletedTopic)) {
            return "profile.onboarding.completed";
        }
        if (topic.equals(messageSentTopic)) {
            return "message.received";
        }
        if (topic.equals(chatCreatedTopic)) {
            return "chat.created";
        }
        if (topic.equals(groupCreatedTopic)) {
            return "group.created";
        }
        if (topic.equals(memberJoinedTopic)) {
            return "group.member.joined";
        }
        if (topic.equals(memberBannedTopic)) {
            return "group.member.banned";
        }
        if (topic.equals(groupMessageSentTopic)) {
            return "group.message.sent";
        }
        return topic;
    }

    private String buildTitle(String type, JsonNode node) {
        return switch (type) {
            case "user.registered" -> "Welcome to Tandem";
            case "profile.onboarding.completed" -> "Onboarding Completed";
            case "message.received" -> "New Message";
            case "group.created" -> "Group Created";
            case "group.member.joined" -> "New Group Member";
            case "group.member.banned" -> "Group Access Updated";
            case "group.message.sent" -> "New Group Message";
            case "chat.created" -> "New Chat Created";
            default -> "New Notification";
        };
    }

    private String buildBody(String type, JsonNode node) {
        return switch (type) {
            case "user.registered" -> "Your account has been created successfully.";
            case "profile.onboarding.completed" -> "You have completed onboarding. Explore next steps.";
            case "message.received" -> "You have received a new message.";
            case "group.created" -> "Your group was created successfully.";
            case "group.member.joined" -> "A new member joined your group.";
            case "group.member.banned" -> "A group moderation event requires your attention.";
            case "group.message.sent" -> "There is a new message in your group.";
            case "chat.created" -> "You were added to a new chat.";
            default -> Optional.ofNullable(node.path("message").asText(null)).orElse("You have a new notification.");
        };
    }

    private List<String> resolveChannels(NotificationPreferencesResponse preferences, String type, List<String> templateChannels) {
        List<String> channels = new ArrayList<>();
        if (Boolean.TRUE.equals(preferences.getChannels().getInApp())) {
            channels.add("in-app");
        }
        if (Boolean.TRUE.equals(preferences.getChannels().getPush())) {
            channels.add("push");
        }
        if (Boolean.TRUE.equals(preferences.getChannels().getEmail())) {
            channels.add("email");
        }
        if (Boolean.TRUE.equals(preferences.getChannels().getSms())) {
            channels.add("sms");
        }

        NotificationPreferencesResponse.Category category = categoryForType(preferences, type);
        if (category != null && Boolean.FALSE.equals(category.getEnabled())) {
            return List.of("in-app");
        }
        if (category != null && category.getChannels() != null && !category.getChannels().isEmpty()) {
            channels = channels.stream().filter(channel -> category.getChannels().contains(channel)).toList();
            if (!channels.contains("in-app")) {
                channels = new ArrayList<>(channels);
                channels.add("in-app");
            }
        }

        if (!isCriticalNotification(type) && isQuietHoursActive(preferences.getQuietHours())) {
            channels = channels.stream()
                    .filter(channel -> channel.equals("in-app"))
                    .toList();
        }

        if (templateChannels != null && !templateChannels.isEmpty()) {
            channels = channels.stream()
                    .filter(templateChannels::contains)
                    .toList();
        }

        if (isCriticalNotification(type)) {
            if (!channels.contains("in-app")) {
                channels = new ArrayList<>(channels);
                channels.add("in-app");
            }
            if (!channels.contains("email")) {
                channels = new ArrayList<>(channels);
                channels.add("email");
            }
        }

        return channels;
    }

    private boolean isCriticalNotification(String type) {
        return "system.security_alert".equals(type)
                || "group.member.banned".equals(type);
    }

    private NotificationPreferencesResponse.Category categoryForType(NotificationPreferencesResponse preferences, String type) {
        if (type.startsWith("message")) {
            return preferences.getCategories().getMessages();
        }
        if (type.startsWith("group") || type.startsWith("chat")) {
            return preferences.getCategories().getGroups();
        }
        return preferences.getCategories().getSystem();
    }

    private boolean isQuietHoursActive(NotificationPreferencesResponse.QuietHours quietHours) {
        if (quietHours == null || !Boolean.TRUE.equals(quietHours.getEnabled())) {
            return false;
        }
        LocalTime start = parseTime(quietHours.getStart());
        LocalTime end = parseTime(quietHours.getEnd());
        if (start == null || end == null) {
            return false;
        }

        LocalTime now = LocalTime.now();
        if (start.equals(end)) {
            return true;
        }
        if (start.isBefore(end)) {
            return !now.isBefore(start) && now.isBefore(end);
        }
        return !now.isBefore(start) || now.isBefore(end);
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(value, TIME_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private UUID extractUuid(JsonNode node, String... fields) {
        JsonNode fieldNode = firstPresent(node, fields);
        if (fieldNode == null) {
            return null;
        }
        return tryParseUuid(fieldNode.asText(null));
    }

    private JsonNode firstPresent(JsonNode node, String... fields) {
        for (String field : fields) {
            if (node.hasNonNull(field)) {
                return node.get(field);
            }
        }
        return null;
    }

    private UUID tryParseUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> objectToMap(JsonNode node) {
        return objectMapper.convertValue(node, Map.class);
    }
}
