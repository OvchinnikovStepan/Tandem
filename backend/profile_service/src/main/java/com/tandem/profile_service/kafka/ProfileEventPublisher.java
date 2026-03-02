package com.tandem.profile_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.profile_service.config.TandemKafkaConfig;
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
public class ProfileEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Публикует событие создания профиля
     */
    public void publishProfileCreated(UUID userId, UUID profileId) {
        Map<String, Object> event = Map.of(
                "eventType", "profile.created",
                "userId", userId.toString(),
                "profileId", profileId.toString(),
                "timestamp", Instant.now().toString()
        );
        publishEvent(TandemKafkaConfig.TOPIC_PROFILE_CREATED, event);
    }

    /**
     * Публикует событие обновления профиля
     */
    public void publishProfileUpdated(UUID userId, UUID profileId, List<String> changedFields) {
        Map<String, Object> event = Map.of(
                "eventType", "profile.updated",
                "userId", userId.toString(),
                "profileId", profileId.toString(),
                "changedFields", changedFields,
                "timestamp", Instant.now().toString()
        );
        publishEvent(TandemKafkaConfig.TOPIC_PROFILE_UPDATED, event);
    }

    /**
     * Публикует событие завершения онбординга
     */
    public void publishOnboardingCompleted(UUID userId, UUID profileId,
                                           String name, String surname, List<String> interests) {
        Map<String, Object> data = Map.of(
                "name", name,
                "surname", surname,
                "interests", interests
        );
        Map<String, Object> event = Map.of(
                "eventType", "profile.onboarding.completed",
                "userId", userId.toString(),
                "profileId", profileId.toString(),
                "timestamp", Instant.now().toString(),
                "data", data
        );
        publishEvent(TandemKafkaConfig.TOPIC_ONBOARDING_COMPLETED, event);
    }

    /**
     * Универсальный метод публикации события
     */
    private void publishEvent(String topic, Map<String, Object> event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);

            Message<String> message = MessageBuilder
                    .withPayload(eventJson)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .build();

            kafkaTemplate.send(message);
            log.info("Event published to topic={}, eventType={}",
                    topic, event.get("eventType"));

        } catch (Exception e) {
            log.error("Failed to publish event to topic={}", topic, e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}