package com.tandem.chat_service.integration;

import com.tandem.chat_service.dao.model.GroupEntity;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class InterestEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

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