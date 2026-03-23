package com.tandem.interest_service.service;

import com.tandem.interest_service.dal.TagDal;
import com.tandem.interest_service.dal.UserInterestDal;
import com.tandem.interest_service.service.impl.MatchingServiceImpl;
import com.tandem.interest_service.service.model.response.TagResponse;
import com.tandem.interest_service.service.model.response.UserMatchingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private UserInterestDal userInterestDal;

    @Mock
    private TagDal tagDal;

    private MatchingService matchingService;

    // Тестовые данные
    private UUID currentUserId;
    private UUID otherUserId1;
    private UUID otherUserId2;
    private UUID otherUserId3;
    private UUID tagId1;
    private UUID tagId2;
    private UUID tagId3;
    private UUID tagId4;

    private TagResponse tagResponse1;
    private TagResponse tagResponse2;
    private TagResponse tagResponse3;
    private TagResponse tagResponse4;

    @BeforeEach
    void setUp() {
        matchingService = new MatchingServiceImpl(userInterestDal, tagDal);
        initializeTestData();
    }

    private void initializeTestData() {
        currentUserId = UUID.randomUUID();
        otherUserId1 = UUID.randomUUID();
        otherUserId2 = UUID.randomUUID();
        otherUserId3 = UUID.randomUUID();

        tagId1 = UUID.randomUUID();
        tagId2 = UUID.randomUUID();
        tagId3 = UUID.randomUUID();
        tagId4 = UUID.randomUUID();

        tagResponse1 = TagResponse.builder()
                .id(tagId1)
                .name("gaming")
                .build();

        tagResponse2 = TagResponse.builder()
                .id(tagId2)
                .name("reading")
                .build();

        tagResponse3 = TagResponse.builder()
                .id(tagId3)
                .name("music")
                .build();

        tagResponse4 = TagResponse.builder()
                .id(tagId4)
                .name("sports")
                .build();
    }

    // getMatchingUsers

    @Test
    void getMatchingUsers_Success_WithMultipleMatches() {
        int limit = 3;
        int minMatchCount = 1;
        int currentUserInterestsCount = 5;

        Map<UUID, Integer> matchingData = new HashMap<>();
        matchingData.put(otherUserId1, 3); // 3 общих тега
        matchingData.put(otherUserId2, 4); // 4 общих тега
        matchingData.put(otherUserId3, 2); // 2 общих тега

        // Количество интересов у других пользователей
        when(userInterestDal.getCountUserInterests(currentUserId))
                .thenReturn(currentUserInterestsCount);
        when(userInterestDal.getCountUserInterests(otherUserId1))
                .thenReturn(4);
        when(userInterestDal.getCountUserInterests(otherUserId2))
                .thenReturn(6);
        when(userInterestDal.getCountUserInterests(otherUserId3))
                .thenReturn(3);

        when(userInterestDal.getUsersWithCommonTagsCount(currentUserId, minMatchCount))
                .thenReturn(matchingData);

        // Общие теги для каждого пользователя
        when(userInterestDal.getCommonTagIds(currentUserId, otherUserId1))
                .thenReturn(List.of(tagId1, tagId2, tagId3));
        when(userInterestDal.getCommonTagIds(currentUserId, otherUserId2))
                .thenReturn(List.of(tagId1, tagId2, tagId3, tagId4));
        when(userInterestDal.getCommonTagIds(currentUserId, otherUserId3))
                .thenReturn(List.of(tagId1, tagId2));

        // Получение названий тегов
        when(tagDal.get(tagId1)).thenReturn(tagResponse1);
        when(tagDal.get(tagId2)).thenReturn(tagResponse2);
        when(tagDal.get(tagId3)).thenReturn(tagResponse3);
        when(tagDal.get(tagId4)).thenReturn(tagResponse4);

        List<UserMatchingResponse> result = matchingService.getMatchingUsers(
                currentUserId, limit, minMatchCount);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);

        // Все пользователи присутствуют в ответе
        List<UUID> resultUserIds = result.stream()
                .map(UserMatchingResponse::getUserId)
                .toList();
        assertThat(resultUserIds).containsExactlyInAnyOrder(otherUserId1, otherUserId2, otherUserId3);

        verify(userInterestDal).getUsersWithCommonTagsCount(currentUserId, minMatchCount);
        verify(userInterestDal, times(4)).getCountUserInterests(any(UUID.class));
    }

    @Test
    void getMatchingUsers_Success_WithLimitLessThanAvailable() {
        int limit = 2;
        int minMatchCount = 1;
        int currentUserInterestsCount = 5;

        Map<UUID, Integer> matchingData = new HashMap<>();
        matchingData.put(otherUserId1, 3);
        matchingData.put(otherUserId2, 4);
        matchingData.put(otherUserId3, 2);

        when(userInterestDal.getCountUserInterests(currentUserId))
                .thenReturn(currentUserInterestsCount);
        when(userInterestDal.getCountUserInterests(otherUserId1))
                .thenReturn(4);
        when(userInterestDal.getCountUserInterests(otherUserId2))
                .thenReturn(6);
        when(userInterestDal.getCountUserInterests(otherUserId3))
                .thenReturn(3);

        when(userInterestDal.getUsersWithCommonTagsCount(currentUserId, minMatchCount))
                .thenReturn(matchingData);

        when(userInterestDal.getCommonTagIds(eq(currentUserId), any(UUID.class)))
                .thenReturn(List.of(tagId1, tagId2));

        when(tagDal.get(any(UUID.class))).thenReturn(tagResponse1);

        List<UserMatchingResponse> result = matchingService.getMatchingUsers(
                currentUserId, limit, minMatchCount);

        assertThat(result).hasSize(limit);
        assertThat(result.size()).isEqualTo(limit);

        verify(userInterestDal).getUsersWithCommonTagsCount(currentUserId, minMatchCount);
    }

    @Test
    void getMatchingUsers_ReturnsEmptyList_WhenNoMatches() {
        int limit = 10;
        int minMatchCount = 1;

        when(userInterestDal.getUsersWithCommonTagsCount(currentUserId, minMatchCount))
                .thenReturn(new HashMap<>());

        List<UserMatchingResponse> result = matchingService.getMatchingUsers(
                currentUserId, limit, minMatchCount);

        assertThat(result).isEmpty();
        verify(userInterestDal).getUsersWithCommonTagsCount(currentUserId, minMatchCount);
        verify(userInterestDal, never()).getCountUserInterests(any());
        verify(userInterestDal, never()).getCommonTagIds(any(), any());
    }

    @Test
    void getMatchingUsers_CalculatesCorrectMatchScore() {
        int limit = 10;
        int minMatchCount = 1;
        int currentUserInterestsCount = 10;
        int otherUserInterestsCount = 5;
        int commonTagsCount = 3;

        // Расчет ожидаемого процента:
        double expectedScore = 25.0;

        Map<UUID, Integer> matchingData = new HashMap<>();
        matchingData.put(otherUserId1, commonTagsCount);

        when(userInterestDal.getCountUserInterests(currentUserId))
                .thenReturn(currentUserInterestsCount);
        when(userInterestDal.getCountUserInterests(otherUserId1))
                .thenReturn(otherUserInterestsCount);
        when(userInterestDal.getUsersWithCommonTagsCount(currentUserId, minMatchCount))
                .thenReturn(matchingData);
        when(userInterestDal.getCommonTagIds(currentUserId, otherUserId1))
                .thenReturn(List.of(tagId1, tagId2, tagId3));
        when(tagDal.get(any(UUID.class))).thenReturn(tagResponse1);

        List<UserMatchingResponse> result = matchingService.getMatchingUsers(
                currentUserId, limit, minMatchCount);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMatchScore()).isEqualTo(expectedScore);
    }
}