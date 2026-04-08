package com.tandem.interest_service.integration;

import com.tandem.interest_service.service.model.response.TagResponse;
import com.tandem.interest_service.service.model.response.UserInterestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterestEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private InterestEventPublisher eventPublisher;

    private UUID tagId;
    private UUID userId;
    private TagResponse tagResponse;
    private UserInterestResponse userInterestResponse;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        eventPublisher = new InterestEventPublisher(kafkaTemplate);
        initializeTestData();
    }

    private void initializeTestData() {
        tagId = UUID.randomUUID();
        userId = UUID.randomUUID();
        now = LocalDateTime.now();

        tagResponse = TagResponse.builder()
                .id(tagId)
                .name("gaming")
                .usageCount(5)
                .build();

        userInterestResponse = UserInterestResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tag(tagResponse)
                .createdAt(now)
                .build();
    }

    // publishTagCreated
    @Test
    void publishTagCreated_Success() {
        when(kafkaTemplate.send(any(Message.class))).thenReturn(null);

        eventPublisher.publishTagCreated(tagResponse);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate).send(messageCaptor.capture());

        Message<Map<String, Object>> capturedMessage = messageCaptor.getValue();
        Map<String, Object> payload = capturedMessage.getPayload();

        assertThat(payload).containsKey("eventType");
        assertThat(payload.get("eventType")).isEqualTo("tag.created");
        assertThat(payload.get("tagId")).isEqualTo(tagId.toString());
        assertThat(payload.get("tagName")).isEqualTo("gaming");
        assertThat(payload).containsKey("timestamp");
        assertThat(payload.get("timestamp")).isNotNull();
    }


    @Test
    void publishTagCreated_VerifyMessageHeaders() {
        when(kafkaTemplate.send(any(Message.class))).thenReturn(null);

        eventPublisher.publishTagCreated(tagResponse);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate).send(messageCaptor.capture());

        Message<Map<String, Object>> capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getHeaders().get("kafka_topic")).isEqualTo("tag.created");
    }

    @Test
    void publishTagCreated_ShouldContainAllRequiredFields() {
        when(kafkaTemplate.send(any(Message.class))).thenReturn(null);

        eventPublisher.publishTagCreated(tagResponse);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate).send(messageCaptor.capture());

        Map<String, Object> payload = (Map<String, Object>) messageCaptor.getValue().getPayload();

        assertThat(payload).containsOnlyKeys("eventType", "tagId", "tagName", "timestamp");
        assertThat(payload.get("eventType")).isEqualTo("tag.created");
        assertThat(payload.get("tagId")).isEqualTo(tagId.toString());
        assertThat(payload.get("tagName")).isEqualTo("gaming");
    }

    // publishInterestsUpdated
    @Test
    void publishInterestsUpdated_Success_WithSingleInterest() {
        List<UserInterestResponse> interests = List.of(userInterestResponse);
        when(kafkaTemplate.send(any(Message.class))).thenReturn(null);

        eventPublisher.publishInterestsUpdated(interests);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate).send(messageCaptor.capture());

        Message<Map<String, Object>> capturedMessage = messageCaptor.getValue();
        Map<String, Object> payload = capturedMessage.getPayload();

        assertThat(payload.get("eventType")).isEqualTo("interests.updated");
        assertThat(payload.get("userId")).isEqualTo(userId.toString());
        assertThat(payload.get("tagIds")).isInstanceOf(List.class);
        assertThat(payload.get("tagNames")).isInstanceOf(List.class);

        List<String> tagIds = (List<String>) payload.get("tagIds");
        List<String> tagNames = (List<String>) payload.get("tagNames");

        assertThat(tagIds).hasSize(1);
        assertThat(tagIds.get(0)).isEqualTo(tagId.toString());
        assertThat(tagNames).hasSize(1);
        assertThat(tagNames.get(0)).isEqualTo("gaming");
        assertThat(payload).containsKey("timestamp");
    }

    @Test
    void publishInterestsUpdated_Success_WithMultipleInterests() {
        UUID tagId2 = UUID.randomUUID();
        TagResponse tagResponse2 = TagResponse.builder()
                .id(tagId2)
                .name("reading")
                .usageCount(3)
                .build();

        UserInterestResponse interest2 = UserInterestResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tag(tagResponse2)
                .createdAt(now)
                .build();

        List<UserInterestResponse> interests = Arrays.asList(userInterestResponse, interest2);
        when(kafkaTemplate.send(any(Message.class))).thenReturn(null);

        eventPublisher.publishInterestsUpdated(interests);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate).send(messageCaptor.capture());

        Message<Map<String, Object>> capturedMessage = messageCaptor.getValue();
        Map<String, Object> payload = capturedMessage.getPayload();

        List<String> tagIds = (List<String>) payload.get("tagIds");
        List<String> tagNames = (List<String>) payload.get("tagNames");

        assertThat(tagIds).hasSize(2);
        assertThat(tagIds).containsExactly(tagId.toString(), tagId2.toString());
        assertThat(tagNames).hasSize(2);
        assertThat(tagNames).containsExactly("gaming", "reading");
    }

    @Test
    void publishInterestsUpdated_WhenListIsEmpty_ShouldThrowException() {
        List<UserInterestResponse> interests = List.of();

        assertThatThrownBy(() -> eventPublisher.publishInterestsUpdated(interests))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void publishInterestsUpdated_VerifyMessageHeaders() {
        List<UserInterestResponse> interests = List.of(userInterestResponse);
        when(kafkaTemplate.send(any(Message.class))).thenReturn(null);

        eventPublisher.publishInterestsUpdated(interests);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate).send(messageCaptor.capture());

        Message<Map<String, Object>> capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getHeaders().get("kafka_topic")).isEqualTo("interests.updated");
    }

    @Test
    void publishInterestsUpdated_ShouldContainAllRequiredFields() {
        List<UserInterestResponse> interests = List.of(userInterestResponse);
        when(kafkaTemplate.send(any(Message.class))).thenReturn(null);

        eventPublisher.publishInterestsUpdated(interests);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate).send(messageCaptor.capture());

        Map<String, Object> payload = (Map<String, Object>) messageCaptor.getValue().getPayload();

        assertThat(payload).containsOnlyKeys("eventType", "userId", "tagIds", "tagNames", "timestamp");
        assertThat(payload.get("eventType")).isEqualTo("interests.updated");
        assertThat(payload.get("userId")).isEqualTo(userId.toString());
    }
}
