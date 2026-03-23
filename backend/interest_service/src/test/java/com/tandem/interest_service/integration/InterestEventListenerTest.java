package com.tandem.interest_service.integration;

import com.tandem.interest_service.service.UserInterestService;
import com.tandem.interest_service.service.model.request.UserInterestRequest;
import com.tandem.interest_service.service.model.response.TagResponse;
import com.tandem.interest_service.service.model.response.UserInterestResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterestEventListenerTest {

    @Mock
    private UserInterestService userInterestService;

    @Mock
    private OnboardingEventParser eventParser;

    @Mock
    private Acknowledgment acknowledgment;

    private InterestEventListener interestEventListener;

    private UUID userId;
    private UUID tagId1;
    private UUID tagId2;
    private UserInterestRequest request1;
    private UserInterestRequest request2;
    private UserInterestResponse response1;
    private UserInterestResponse response2;
    private TagResponse tagResponse1;
    private TagResponse tagResponse2;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        interestEventListener = new InterestEventListener(userInterestService, eventParser);
        initializeTestData();
    }

    private void initializeTestData() {
        userId = UUID.randomUUID();
        tagId1 = UUID.randomUUID();
        tagId2 = UUID.randomUUID();
        now = LocalDateTime.now();

        tagResponse1 = TagResponse.builder()
                .id(tagId1)
                .name("gaming")
                .usageCount(5)
                .build();

        tagResponse2 = TagResponse.builder()
                .id(tagId2)
                .name("reading")
                .usageCount(3)
                .build();

        request1 = UserInterestRequest.builder()
                .userId(userId)
                .tagId(tagId1)
                .build();

        request2 = UserInterestRequest.builder()
                .userId(userId)
                .tagId(tagId2)
                .build();

        response1 = UserInterestResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tag(tagResponse1)
                .createdAt(now)
                .build();

        response2 = UserInterestResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tag(tagResponse2)
                .createdAt(now)
                .build();
    }

    @Test
    void handleOnboardingCompleted_Success() {
        String message = "{\"userId\":\"" + userId + "\",\"data\":{\"interests\":[\"gaming\",\"reading\"]}}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0, "key", message);

        List<UserInterestRequest> requests = Arrays.asList(request1, request2);
        List<UserInterestResponse> responses = Arrays.asList(response1, response2);

        when(eventParser.parseToUserInterestRequests(message)).thenReturn(requests);
        when(userInterestService.addUserInterest(requests)).thenReturn(responses);

        interestEventListener.handleOnboardingCompleted(record, acknowledgment);

        verify(eventParser).parseToUserInterestRequests(message);
        verify(userInterestService).addUserInterest(requests);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void handleOnboardingCompleted_WhenNoValidInterests_ShouldAcknowledge() {
        String message = "{\"userId\":\"" + userId + "\",\"data\":{\"interests\":[]}}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0, "key", message);

        when(eventParser.parseToUserInterestRequests(message)).thenReturn(List.of());

        interestEventListener.handleOnboardingCompleted(record, acknowledgment);

        verify(eventParser).parseToUserInterestRequests(message);
        verify(userInterestService, never()).addUserInterest(anyList());
        verify(acknowledgment).acknowledge();
    }

    @Test
    void handleOnboardingCompleted_WithMultipleInterests() {
        String message = "{\"userId\":\"" + userId + "\",\"data\":{\"interests\":[\"gaming\",\"reading\",\"music\"]}}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0, "key", message);

        List<UserInterestRequest> requests = Arrays.asList(request1, request2);
        List<UserInterestResponse> responses = Arrays.asList(response1, response2);

        when(eventParser.parseToUserInterestRequests(message)).thenReturn(requests);
        when(userInterestService.addUserInterest(requests)).thenReturn(responses);

        interestEventListener.handleOnboardingCompleted(record, acknowledgment);

        ArgumentCaptor<List<UserInterestRequest>> captor = ArgumentCaptor.forClass(List.class);
        verify(userInterestService).addUserInterest(captor.capture());

        List<UserInterestRequest> capturedRequests = captor.getValue();
        assertThat(capturedRequests).hasSize(2);
        verify(acknowledgment).acknowledge();
    }
}
