package com.tandem.interest_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.interest_service.service.model.response.TagResponse;
import com.tandem.interest_service.service.model.response.UserInterestResponse;
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
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterestEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Событие создания нового тега
     */
    public void publishTagCreated(TagResponse tag) {
        // Создаем Map с данными события
        Map<String, Object> event = Map.of(
                "eventType", "tag.created",
                "tagId", tag.getId().toString(),
                "tagName", tag.getName(),
                "timestamp", Instant.now().toString()
        );

        publishEvent("tag.created", event);
        log.info("Published tag.created event for tag: {} with name: {}", tag.getId(), tag.getName());
    }

    /**
     * Событие обновления интересов пользователя
     */
    public void publishInterestsUpdated(List<UserInterestResponse> interests) {
        List<String> tagIds = interests.stream()
                .map(interest -> interest.getTag().getId().toString())
                .collect(Collectors.toList());

        List<String> tagNames = interests.stream()
                .map(interest -> interest.getTag().getName())
                .collect(Collectors.toList());

        UUID userId = interests.get(0).getUserId();

        Map<String, Object> event = Map.of(
                "eventType", "interests.updated",
                "userId", userId.toString(),
                "tagIds", tagIds,
                "tagNames", tagNames,
                "timestamp", Instant.now().toString()
        );

        publishEvent("interests.updated", event);
        log.info("Published interests.updated event for user: {} with {} tags", userId, tagIds.size());
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