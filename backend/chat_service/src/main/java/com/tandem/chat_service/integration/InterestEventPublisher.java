package com.tandem.chat_service.integration;

import com.tandem.chat_service.dao.model.GroupEntity;
import com.tandem.chat_service.dao.model.MessageEntity;
import com.tandem.chat_service.dao.enums.GroupRequestStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterestEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String KEY_EVENT_TYPE = "eventType";
    private static final String KEY_TIMESTAMP = "timestamp";

    // Названия топиков и событий
    public static final String EVENT_MESSAGE_SENT = "message.sent";
    public static final String EVENT_CHAT_CREATED = "chat.created";
    public static final String EVENT_GROUP_REQUEST_UPDATED = "group.request.updated";
    public static final String EVENT_GROUP_CREATED = "group.created";
    public static final String EVENT_GROUP_USER_JOINED = "group.user.joined";

    /**
     * Событие отправки сообщения
     */
    public void publishMessageSent(MessageEntity message, List<UUID> recipientIds) {
        for (UUID recipientId : recipientIds) {
            Map<String, Object> event = Map.of(
                    KEY_EVENT_TYPE, EVENT_MESSAGE_SENT,
                    "messageId", message.getId().toString(),
                    "chatId", message.getChatId().toString(),
                    "senderId", message.getSenderId().toString(),
                    "recipientId", recipientId.toString(),
                    KEY_TIMESTAMP, Instant.now().toString()
            );
            publishEvent(EVENT_MESSAGE_SENT, event);
        }
    }

    /**
     * Событие создания личного чата
     */
    public void publishChatCreated(UUID chatId, List<UUID> participantIds) {
        Map<String, Object> event = Map.of(
                KEY_EVENT_TYPE, EVENT_CHAT_CREATED,
                "chatId", chatId.toString(),
                "participantIds", participantIds.stream().map(UUID::toString).toList(),
                KEY_TIMESTAMP, Instant.now().toString()
        );
        publishEvent(EVENT_CHAT_CREATED, event);
    }

    /**
     * Событие по заявке в группу (создание или смена статуса)
     */
    public void publishGroupRequestEvent(UUID requestId, UUID groupId, UUID targetUserId, GroupRequestStatus status) {
        Map<String, Object> event = Map.of(
                KEY_EVENT_TYPE, EVENT_GROUP_REQUEST_UPDATED,
                "requestId", requestId.toString(),
                "groupId", groupId.toString(),
                "targetUserId", targetUserId.toString(),
                "status", status.getValue(),
                KEY_TIMESTAMP, Instant.now().toString()
        );
        publishEvent(EVENT_GROUP_REQUEST_UPDATED, event);
    }

    /**
     * Событие вступления в публичную группу (для владельца)
     */
    public void publishUserJoinedGroup(UUID groupId, UUID ownerId, UUID joinedUserId) {
        Map<String, Object> event = Map.of(
                KEY_EVENT_TYPE, EVENT_GROUP_USER_JOINED,
                "groupId", groupId.toString(),
                "ownerId", ownerId.toString(),
                "joinedUserId", joinedUserId.toString(),
                KEY_TIMESTAMP, Instant.now().toString()
        );
        publishEvent(EVENT_GROUP_USER_JOINED, event);
    }

    /**
     * Событие создания группы с интересами
     */
    public void publishGroupCreated(GroupEntity group, List<String> groupInterests) {
        Map<String, Object> event = Map.of(
                KEY_EVENT_TYPE, EVENT_GROUP_CREATED,
                "groupId", group.getId().toString(),
                "groupName", group.getName(),
                "creatorId", group.getCreatorId().toString(),
                "interestTags", groupInterests.toString(),
                "visibility", group.getVisibility().toString(),
                KEY_TIMESTAMP, Instant.now().toString()
        );

        publishEvent(EVENT_GROUP_CREATED, event);
        log.info("Published group.created event for group: {} with name: {}", group.getId(), group.getName());
    }

    private void publishEvent(String topic, Map<String, Object> event) {
        try {
            Message<Map<String, Object>> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .build();

            kafkaTemplate.send(message);
            log.debug("Event published to topic={}, eventType={}", topic, event.get("eventType"));

        } catch (Exception e) {
            log.error("Failed to publish event to topic={}", topic, e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}