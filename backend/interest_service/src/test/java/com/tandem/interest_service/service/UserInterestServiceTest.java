package com.tandem.interest_service.service;

import com.tandem.interest_service.dal.UserInterestDal;
import com.tandem.interest_service.integration.InterestEventPublisher;
import com.tandem.interest_service.service.impl.UserInterestServiceImpl;
import com.tandem.interest_service.service.model.request.UserInterestRequest;
import com.tandem.interest_service.service.model.response.TagResponse;
import com.tandem.interest_service.service.model.response.UserInterestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserInterestServiceTest {

    @Mock
    private UserInterestDal userInterestDal;

    @Mock
    private InterestEventPublisher eventPublisher;

    private UserInterestService userInterestService;

    // Тестовые данные
    private UUID userId;
    private UUID tagId1;
    private UUID tagId2;
    private UUID interestId1;
    private UUID interestId2;
    private TagResponse tagResponse1;
    private TagResponse tagResponse2;
    private UserInterestRequest request1;
    private UserInterestRequest request2;
    private UserInterestResponse response1;
    private UserInterestResponse response2;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        userInterestService = new UserInterestServiceImpl(userInterestDal, eventPublisher);
        initializeTestData();
    }

    private void initializeTestData() {
        userId = UUID.randomUUID();
        tagId1 = UUID.randomUUID();
        tagId2 = UUID.randomUUID();
        interestId1 = UUID.randomUUID();
        interestId2 = UUID.randomUUID();
        now = LocalDateTime.now();

        tagResponse1 = TagResponse.builder()
                .id(tagId1)
                .name("gaming")
                .imageUrl(null)
                .usageCount(5)
                .build();

        tagResponse2 = TagResponse.builder()
                .id(tagId2)
                .name("reading")
                .imageUrl("http://example.com/reading.jpg")
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
                .id(interestId1)
                .userId(userId)
                .tag(tagResponse1)
                .createdAt(now)
                .build();

        response2 = UserInterestResponse.builder()
                .id(interestId2)
                .userId(userId)
                .tag(tagResponse2)
                .createdAt(now)
                .build();
    }

    // addUserInterest
    @Test
    void addUserInterest_Success_WithSingleInterest() {
        List<UserInterestRequest> requests = List.of(request1);
        List<UserInterestResponse> expectedResponses = List.of(response1);

        when(userInterestDal.insert(requests)).thenReturn(expectedResponses);

        List<UserInterestResponse> result = userInterestService.addUserInterest(requests);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(interestId1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(0).getTag().getId()).isEqualTo(tagId1);

        verify(userInterestDal).insert(requests);
        verify(eventPublisher).publishInterestsUpdated(expectedResponses);
    }

    @Test
    void addUserInterest_Success_WithMultipleInterests() {
        List<UserInterestRequest> requests = Arrays.asList(request1, request2);
        List<UserInterestResponse> expectedResponses = Arrays.asList(response1, response2);

        when(userInterestDal.insert(requests)).thenReturn(expectedResponses);

        List<UserInterestResponse> result = userInterestService.addUserInterest(requests);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(response1, response2);

        verify(userInterestDal).insert(requests);
        verify(eventPublisher).publishInterestsUpdated(expectedResponses);
    }

    @Test
    void addUserInterest_ReturnsEmptyList_WhenRequestIsEmpty() {
        List<UserInterestResponse> result = userInterestService.addUserInterest(List.of());

        assertThat(result).isEmpty();
        verify(userInterestDal, never()).insert(anyList());
        verify(eventPublisher, never()).publishInterestsUpdated(anyList());
    }

    @Test
    void addUserInterest_ThrowsException_WhenUserIdIsNull() {
        UserInterestRequest invalidRequest = UserInterestRequest.builder()
                .userId(null)
                .tagId(tagId1)
                .build();
        List<UserInterestRequest> requests = List.of(invalidRequest);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userInterestService.addUserInterest(requests));

        assertThat(exception.getMessage()).isEqualTo("User ID cannot be null");
        verify(userInterestDal, never()).insert(anyList());
        verify(eventPublisher, never()).publishInterestsUpdated(anyList());
    }

    @Test
    void addUserInterest_ThrowsException_WhenTagIdIsNull() {
        UserInterestRequest invalidRequest = UserInterestRequest.builder()
                .userId(userId)
                .tagId(null)
                .build();
        List<UserInterestRequest> requests = List.of(invalidRequest);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userInterestService.addUserInterest(requests));

        assertThat(exception.getMessage()).isEqualTo("Tag ID cannot be null");
        verify(userInterestDal, never()).insert(anyList());
        verify(eventPublisher, never()).publishInterestsUpdated(anyList());
    }

    @Test
    void addUserInterest_ThrowsException_WhenMultipleRequestsAndOneIsInvalid() {
        UserInterestRequest invalidRequest = UserInterestRequest.builder()
                .userId(null)
                .tagId(tagId1)
                .build();
        List<UserInterestRequest> requests = Arrays.asList(request1, invalidRequest);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userInterestService.addUserInterest(requests));

        assertThat(exception.getMessage()).isEqualTo("User ID cannot be null");
        verify(userInterestDal, never()).insert(anyList());
        verify(eventPublisher, never()).publishInterestsUpdated(anyList());
    }

    @Test
    void addUserInterest_ShouldLogWarning_WhenEmptyList() {
        List<UserInterestResponse> result = userInterestService.addUserInterest(List.of());

        assertThat(result).isEmpty();
        verify(userInterestDal, never()).insert(any());
        verify(eventPublisher, never()).publishInterestsUpdated(any());
    }

    // removeUserInterest
    @Test
    void removeUserInterest_Success() {
        when(userInterestDal.getUserInterest(userId, tagId1)).thenReturn(response1);
        doNothing().when(userInterestDal).delete(interestId1);

        userInterestService.removeUserInterest(request1);

        verify(userInterestDal).getUserInterest(userId, tagId1);
        verify(userInterestDal).delete(interestId1);
    }

    @Test
    void removeUserInterest_ThrowsException_WhenUserIdIsNull() {
        UserInterestRequest invalidRequest = UserInterestRequest.builder()
                .userId(null)
                .tagId(tagId1)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userInterestService.removeUserInterest(invalidRequest));

        assertThat(exception.getMessage()).isEqualTo("User ID and Tag ID cannot be null");
        verify(userInterestDal, never()).getUserInterest(any(), any());
        verify(userInterestDal, never()).delete(any());
    }

    @Test
    void removeUserInterest_ThrowsException_WhenTagIdIsNull() {
        UserInterestRequest invalidRequest = UserInterestRequest.builder()
                .userId(userId)
                .tagId(null)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userInterestService.removeUserInterest(invalidRequest));

        assertThat(exception.getMessage()).isEqualTo("User ID and Tag ID cannot be null");
        verify(userInterestDal, never()).getUserInterest(any(), any());
        verify(userInterestDal, never()).delete(any());
    }


    // getUserInterests
    @Test
    void getUserInterests_Success_WithMultipleInterests() {
        List<UserInterestResponse> expectedInterests = Arrays.asList(response1, response2);
        when(userInterestDal.getUserInterests(userId)).thenReturn(expectedInterests);

        List<UserInterestResponse> result = userInterestService.getUserInterests(userId);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(response1, response2);
        verify(userInterestDal).getUserInterests(userId);
    }

    @Test
    void getUserInterests_ReturnsEmptyList_WhenUserHasNoInterests() {
        when(userInterestDal.getUserInterests(userId)).thenReturn(List.of());

        List<UserInterestResponse> result = userInterestService.getUserInterests(userId);

        assertThat(result).isEmpty();
        verify(userInterestDal).getUserInterests(userId);
    }

    @Test
    void getUserInterests_ThrowsException_WhenUserIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userInterestService.getUserInterests(null));

        assertThat(exception.getMessage()).isEqualTo("User ID cannot be null");
        verify(userInterestDal, never()).getUserInterests(any());
    }
}
