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

    /**
     * Событие отправки сообщения
     */
    public void publishMessageSent(MessageEntity message, List<UUID> recipientIds) {
        for (UUID recipientId : recipientIds) {
            Map<String, Object> event = Map.of(
                    "eventType", "message.sent",
                    "messageId", message.getId().toString(),
                    "chatId", message.getChatId().toString(),
                    "senderId", message.getSenderId().toString(),
                    "recipientId", recipientId.toString(),
                    "timestamp", Instant.now().toString()
            );
            publishEvent("message.sent", event);
        }
    }

    /**
     * Событие создания личного чата
     */
    public void publishChatCreated(UUID chatId, List<UUID> participantIds) {
        Map<String, Object> event = Map.of(
                "eventType", "chat.created",
                "chatId", chatId.toString(),
                "participantIds", participantIds.stream().map(UUID::toString).toList(),
                "timestamp", Instant.now().toString()
        );
        publishEvent("chat.created", event);
    }

    /**
     * Событие по заявке в группу (создание или смена статуса)
     */
    public void publishGroupRequestEvent(UUID requestId, UUID groupId, UUID targetUserId, GroupRequestStatus status) {
        Map<String, Object> event = Map.of(
                "eventType", "group.request.updated",
                "requestId", requestId.toString(),
                "groupId", groupId.toString(),
                "targetUserId", targetUserId.toString(),
                "status", status.getValue(),
                "timestamp", Instant.now().toString()
        );
        publishEvent("group.request.updated", event);
    }

    /**
     * Событие вступления в публичную группу (для владельца)
     */
    public void publishUserJoinedGroup(UUID groupId, UUID ownerId, UUID joinedUserId) {
        Map<String, Object> event = Map.of(
                "eventType", "group.user.joined",
                "groupId", groupId.toString(),
                "ownerId", ownerId.toString(),
                "joinedUserId", joinedUserId.toString(),
                "timestamp", Instant.now().toString()
        );
        publishEvent("group.events", event);
    }

    /**
     * Событие создания группы с интересами
     */
    public void publishGroupCreated(GroupEntity group, List<String> groupInterests) {
        Map<String, Object> event = Map.of(
                "eventType", "group.created",
                "groupId", group.getId().toString(),
                "groupName", group.getName(),
                "creatorId", group.getCreatorId().toString(),
                "interestTags", groupInterests.toString(),
                "visibility", group.getVisibility().toString(),
                "timestamp", Instant.now().toString()
        );

        publishEvent("group.created", event);
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