package com.tandem.profile_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.profile_service.config.TandemKafkaConfig;
import com.tandem.profile_service.dto.ProfileEventDto;
import com.tandem.profile_service.kafka.ProfileEventListener;
import com.tandem.profile_service.service.ProfileService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ProfileEventListenerTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Acknowledgment acknowledgment;

    @Captor
    private ArgumentCaptor<ProfileEventDto> eventCaptor;

    private ProfileEventListener profileEventListener;

    @BeforeEach
    void setUp() {
        profileEventListener = new ProfileEventListener(profileService, objectMapper);
    }

    @Test
    void handleUserRegistered_Success() throws Exception {
        // Arrange
        String userId = UUID.randomUUID().toString();
        String eventJson = """
            {
                "eventType": "user.registered",
                "userId": "%s",
                "timestamp": "2024-01-01T12:00:00Z",
                "metadata": {
                    "phoneNumber": "1234567890",
                    "email": "test@example.com"
                }
            }
            """.formatted(userId);

        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                TandemKafkaConfig.TOPIC_USER_REGISTERED,
                0, 0L, "key", eventJson
        );

        ProfileEventDto event = ProfileEventDto.builder()
                .eventType("user.registered")
                .userId(userId)
                .timestamp("2024-01-01T12:00:00Z")
                .metadata(Map.of(
                        "phoneNumber", "1234567890",
                        "email", "test@example.com"
                ))
                .build();

        when(objectMapper.readValue(eq(eventJson), eq(ProfileEventDto.class))).thenReturn(event);

        // Act
        profileEventListener.handleUserRegistered(record, acknowledgment);

        // Assert
        verify(objectMapper).readValue(eq(eventJson), eq(ProfileEventDto.class));
        verify(profileService).createProfileFromRegistrationEvent(eventCaptor.capture());
        verify(acknowledgment).acknowledge();

        ProfileEventDto capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getEventType()).isEqualTo("user.registered");
        assertThat(capturedEvent.getUserId()).isEqualTo(userId);
        assertThat(capturedEvent.getMetadata())
                .containsEntry("phoneNumber", "1234567890")
                .containsEntry("email", "test@example.com");
    }

    @Test
    void handleUserRegistered_MissingMetadata() throws Exception {
        // Arrange
        String userId = UUID.randomUUID().toString();
        String eventJson = """
            {
                "eventType": "user.registered",
                "userId": "%s",
                "timestamp": "2024-01-01T12:00:00Z"
            }
            """.formatted(userId);

        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                TandemKafkaConfig.TOPIC_USER_REGISTERED,
                0, 0L, "key", eventJson
        );

        ProfileEventDto event = ProfileEventDto.builder()
                .eventType("user.registered")
                .userId(userId)
                .timestamp("2024-01-01T12:00:00Z")
                .build();

        when(objectMapper.readValue(eq(eventJson), eq(ProfileEventDto.class))).thenReturn(event);

        // Act
        profileEventListener.handleUserRegistered(record, acknowledgment);

        // Assert
        verify(profileService).createProfileFromRegistrationEvent(eventCaptor.capture());
        verify(acknowledgment).acknowledge();

        ProfileEventDto capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getMetadata()).isNull();
    }

    @Test
    void handleUserRegistered_EmptyMetadata() throws Exception {
        // Arrange
        String userId = UUID.randomUUID().toString();
        String eventJson = """
            {
                "eventType": "user.registered",
                "userId": "%s",
                "timestamp": "2024-01-01T12:00:00Z",
                "metadata": {}
            }
            """.formatted(userId);

        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                TandemKafkaConfig.TOPIC_USER_REGISTERED,
                0, 0L, "key", eventJson
        );

        ProfileEventDto event = ProfileEventDto.builder()
                .eventType("user.registered")
                .userId(userId)
                .timestamp("2024-01-01T12:00:00Z")
                .metadata(Map.of())
                .build();

        when(objectMapper.readValue(eq(eventJson), eq(ProfileEventDto.class))).thenReturn(event);

        // Act
        profileEventListener.handleUserRegistered(record, acknowledgment);

        // Assert
        verify(profileService).createProfileFromRegistrationEvent(eventCaptor.capture());
        verify(acknowledgment).acknowledge();

        ProfileEventDto capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getMetadata()).isEmpty();
    }

    @Test
    void handleUserRegistered_WithAdditionalMetadataFields() throws Exception {
        // Arrange
        String userId = UUID.randomUUID().toString();
        String eventJson = """
            {
                "eventType": "user.registered",
                "userId": "%s",
                "timestamp": "2024-01-01T12:00:00Z",
                "metadata": {
                    "phoneNumber": "1234567890",
                    "email": "test@example.com",
                    "firstName": "John",
                    "lastName": "Doe"
                }
            }
            """.formatted(userId);

        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                TandemKafkaConfig.TOPIC_USER_REGISTERED,
                0, 0L, "key", eventJson
        );

        ProfileEventDto event = ProfileEventDto.builder()
                .eventType("user.registered")
                .userId(userId)
                .timestamp("2024-01-01T12:00:00Z")
                .metadata(Map.of(
                        "phoneNumber", "1234567890",
                        "email", "test@example.com",
                        "firstName", "John",
                        "lastName", "Doe"
                ))
                .build();

        when(objectMapper.readValue(eq(eventJson), eq(ProfileEventDto.class))).thenReturn(event);

        // Act
        profileEventListener.handleUserRegistered(record, acknowledgment);

        // Assert
        verify(profileService).createProfileFromRegistrationEvent(eventCaptor.capture());
        verify(acknowledgment).acknowledge();

        ProfileEventDto capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getMetadata()).hasSize(4);
        assertThat(capturedEvent.getMetadata())
                .containsEntry("firstName", "John")
                .containsEntry("lastName", "Doe");
    }

    @Test
    void handleUserRegistered_JsonParsingError() throws Exception {
        // Arrange
        String invalidJson = "invalid json";
        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                TandemKafkaConfig.TOPIC_USER_REGISTERED,
                0, 0L, "key", invalidJson
        );

        when(objectMapper.readValue(eq(invalidJson), eq(ProfileEventDto.class)))
                .thenThrow(new RuntimeException("JSON parsing error"));

        // Act
        profileEventListener.handleUserRegistered(record, acknowledgment);

        // Assert
        verify(objectMapper).readValue(eq(invalidJson), eq(ProfileEventDto.class));
        verify(profileService, never()).createProfileFromRegistrationEvent(any());
        verify(acknowledgment, never()).acknowledge();
    }

    @Test
    void handleUserRegistered_ProfileServiceError() throws Exception {
        // Arrange
        String userId = UUID.randomUUID().toString();
        String eventJson = """
            {
                "eventType": "user.registered",
                "userId": "%s",
                "timestamp": "2024-01-01T12:00:00Z",
                "metadata": {
                    "email": "test@example.com"
                }
            }
            """.formatted(userId);

        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                TandemKafkaConfig.TOPIC_USER_REGISTERED,
                0, 0L, "key", eventJson
        );

        ProfileEventDto event = ProfileEventDto.builder()
                .eventType("user.registered")
                .userId(userId)
                .timestamp("2024-01-01T12:00:00Z")
                .metadata(Map.of("email", "test@example.com"))
                .build();

        when(objectMapper.readValue(eq(eventJson), eq(ProfileEventDto.class))).thenReturn(event);
        doThrow(new RuntimeException("Database error"))
                .when(profileService).createProfileFromRegistrationEvent(any(ProfileEventDto.class));

        // Act
        profileEventListener.handleUserRegistered(record, acknowledgment);

        // Assert
        verify(objectMapper).readValue(eq(eventJson), eq(ProfileEventDto.class));
        verify(profileService).createProfileFromRegistrationEvent(any(ProfileEventDto.class));
        verify(acknowledgment, never()).acknowledge();
    }

    @Test
    void handleUserRegistered_WrongEventType() throws Exception {
        // Arrange
        String userId = UUID.randomUUID().toString();
        String eventJson = """
            {
                "eventType": "user.updated",  // Wrong event type
                "userId": "%s",
                "timestamp": "2024-01-01T12:00:00Z",
                "metadata": {
                    "email": "test@example.com"
                }
            }
            """.formatted(userId);

        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                TandemKafkaConfig.TOPIC_USER_REGISTERED,
                0, 0L, "key", eventJson
        );

        ProfileEventDto event = ProfileEventDto.builder()
                .eventType("user.updated")
                .userId(userId)
                .timestamp("2024-01-01T12:00:00Z")
                .metadata(Map.of("email", "test@example.com"))
                .build();

        when(objectMapper.readValue(eq(eventJson), eq(ProfileEventDto.class))).thenReturn(event);

        // Act
        profileEventListener.handleUserRegistered(record, acknowledgment);

        // Assert
        verify(profileService).createProfileFromRegistrationEvent(eventCaptor.capture());
        ProfileEventDto capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getEventType()).isEqualTo("user.updated");
    }
}