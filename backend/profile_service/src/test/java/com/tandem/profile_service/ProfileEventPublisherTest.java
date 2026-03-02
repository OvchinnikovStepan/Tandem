package com.tandem.profile_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.profile_service.config.TandemKafkaConfig;
import com.tandem.profile_service.kafka.ProfileEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ProfileEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<Message<String>> messageCaptor;

    @Captor
    private ArgumentCaptor<Map<String, Object>> mapCaptor;

    private ProfileEventPublisher profileEventPublisher;

    @BeforeEach
    void setUp() {
        profileEventPublisher = new ProfileEventPublisher(kafkaTemplate, objectMapper);
    }

    @Test
    void publishProfileCreated_Success() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        String expectedJson = "{\"eventType\":\"profile.created\"}";

        when(objectMapper.writeValueAsString(any(Map.class))).thenReturn(expectedJson);

        // Act
        profileEventPublisher.publishProfileCreated(userId, profileId);

        // Assert
        verify(objectMapper).writeValueAsString(mapCaptor.capture());
        Map<String, Object> capturedMap = mapCaptor.getValue();

        assertThat(capturedMap.get("eventType")).isEqualTo("profile.created");
        assertThat(capturedMap.get("userId")).isEqualTo(userId.toString());
        assertThat(capturedMap.get("profileId")).isEqualTo(profileId.toString());

        verify(kafkaTemplate).send(messageCaptor.capture());
        Message<String> capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getHeaders().get("kafka_topic"))
                .isEqualTo(TandemKafkaConfig.TOPIC_PROFILE_CREATED);
    }

    @Test
    void publishOnboardingCompleted_Success() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        String name = "John";
        String surname = "Doe";
        List<String> interests = Arrays.asList("gaming", "reading");
        String expectedJson = "{\"eventType\":\"profile.onboarding.completed\"}";

        when(objectMapper.writeValueAsString(any(Map.class))).thenReturn(expectedJson);

        // Act
        profileEventPublisher.publishOnboardingCompleted(userId, profileId, name, surname, interests);

        // Assert
        verify(objectMapper).writeValueAsString(mapCaptor.capture());
        Map<String, Object> capturedMap = mapCaptor.getValue();

        assertThat(capturedMap.get("eventType")).isEqualTo("profile.onboarding.completed");

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) capturedMap.get("data");
        assertThat(data.get("name")).isEqualTo(name);
        assertThat(data.get("surname")).isEqualTo(surname);
        assertThat(data.get("interests")).isEqualTo(interests);

        verify(kafkaTemplate).send(any(Message.class));
    }

    @Test
    void publishEvent_JsonProcessingException() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        when(objectMapper.writeValueAsString(any(Map.class)))
                .thenThrow(new JsonProcessingException("JSON error") {});

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> profileEventPublisher.publishProfileCreated(userId, profileId)
        );

        assertThat(exception.getMessage()).contains("Failed to publish event");
        verify(kafkaTemplate, never()).send(any(Message.class));
    }
}