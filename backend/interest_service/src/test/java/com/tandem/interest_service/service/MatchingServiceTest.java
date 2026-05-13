package com.tandem.interest_service.service;

import com.tandem.interest_service.dal.MatchingDal;
import com.tandem.interest_service.service.impl.MatchingServiceImpl;
import com.tandem.interest_service.service.model.response.TagResponse;
import com.tandem.interest_service.service.model.response.UserMatchingResponse;
import com.tandem.interest_service.service.model.response.GroupMatchingResponse;
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
    private MatchingDal matchingDal;

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

    private UUID groupId1;
    private UUID groupId2;
    private UUID groupId3;

    @BeforeEach
    void setUp() {
        matchingService = new MatchingServiceImpl(matchingDal);
        initializeTestData();
    }

    private void initializeTestData() {
        currentUserId = UUID.randomUUID();
        otherUserId1 = UUID.randomUUID();
        otherUserId2 = UUID.randomUUID();
        otherUserId3 = UUID.randomUUID();
        groupId1 = UUID.randomUUID();
        groupId2 = UUID.randomUUID();
        groupId3 = UUID.randomUUID();

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
        when(matchingDal.getCountUserInterests(currentUserId))
                .thenReturn(currentUserInterestsCount);
        when(matchingDal.getCountUserInterests(otherUserId1))
                .thenReturn(4);
        when(matchingDal.getCountUserInterests(otherUserId2))
                .thenReturn(6);
        when(matchingDal.getCountUserInterests(otherUserId3))
                .thenReturn(3);

        when(matchingDal.getUsersWithCommonTagsCount(currentUserId, minMatchCount))
                .thenReturn(matchingData);

        // Общие теги для каждого пользователя
        when(matchingDal.getCommonTagIds(currentUserId, otherUserId1))
                .thenReturn(List.of(tagId1, tagId2, tagId3));
        when(matchingDal.getCommonTagIds(currentUserId, otherUserId2))
                .thenReturn(List.of(tagId1, tagId2, tagId3, tagId4));
        when(matchingDal.getCommonTagIds(currentUserId, otherUserId3))
                .thenReturn(List.of(tagId1, tagId2));

        // Получение названий тегов
        when(matchingDal.findTagById(tagId1)).thenReturn(tagResponse1);
        when(matchingDal.findTagById(tagId2)).thenReturn(tagResponse2);
        when(matchingDal.findTagById(tagId3)).thenReturn(tagResponse3);
        when(matchingDal.findTagById(tagId4)).thenReturn(tagResponse4);

        List<UserMatchingResponse> result = matchingService.getMatchingUsers(
                currentUserId, limit, minMatchCount);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);

        // Все пользователи присутствуют в ответе
        List<UUID> resultUserIds = result.stream()
                .map(UserMatchingResponse::getUserId)
                .toList();
        assertThat(resultUserIds).containsExactlyInAnyOrder(otherUserId1, otherUserId2, otherUserId3);

        verify(matchingDal).getUsersWithCommonTagsCount(currentUserId, minMatchCount);
        verify(matchingDal, times(4)).getCountUserInterests(any(UUID.class));
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

        when(matchingDal.getCountUserInterests(currentUserId))
                .thenReturn(currentUserInterestsCount);
        when(matchingDal.getCountUserInterests(otherUserId1))
                .thenReturn(4);
        when(matchingDal.getCountUserInterests(otherUserId2))
                .thenReturn(6);
        when(matchingDal.getCountUserInterests(otherUserId3))
                .thenReturn(3);

        when(matchingDal.getUsersWithCommonTagsCount(currentUserId, minMatchCount))
                .thenReturn(matchingData);

        when(matchingDal.getCommonTagIds(eq(currentUserId), any(UUID.class)))
                .thenReturn(List.of(tagId1, tagId2));

        when(matchingDal.findTagById(any(UUID.class))).thenReturn(tagResponse1);

        List<UserMatchingResponse> result = matchingService.getMatchingUsers(
                currentUserId, limit, minMatchCount);

        assertThat(result).hasSize(limit);
        assertThat(result.size()).isEqualTo(limit);

        verify(matchingDal).getUsersWithCommonTagsCount(currentUserId, minMatchCount);
    }

    @Test
    void getMatchingUsers_ReturnsEmptyList_WhenNoMatches() {
        int limit = 10;
        int minMatchCount = 1;

        when(matchingDal.getUsersWithCommonTagsCount(currentUserId, minMatchCount))
                .thenReturn(new HashMap<>());

        List<UserMatchingResponse> result = matchingService.getMatchingUsers(
                currentUserId, limit, minMatchCount);

        assertThat(result).isEmpty();
        verify(matchingDal).getUsersWithCommonTagsCount(currentUserId, minMatchCount);
        verify(matchingDal, never()).getCountUserInterests(any());
        verify(matchingDal, never()).getCommonTagIds(any(), any());
    }

    @Test
    void getMatchingUsers_CalculatesCorrectMatchScore() {
        int limit = 10;
        int minMatchCount = 1;
        int currentUserInterestsCount = 10;
        int otherUserInterestsCount = 5;
        int commonTagsCount = 3;

        double expectedScore = 25.0;

        Map<UUID, Integer> matchingData = new HashMap<>();
        matchingData.put(otherUserId1, commonTagsCount);

        when(matchingDal.getCountUserInterests(currentUserId))
                .thenReturn(currentUserInterestsCount);
        when(matchingDal.getCountUserInterests(otherUserId1))
                .thenReturn(otherUserInterestsCount);
        when(matchingDal.getUsersWithCommonTagsCount(currentUserId, minMatchCount))
                .thenReturn(matchingData);
        when(matchingDal.getCommonTagIds(currentUserId, otherUserId1))
                .thenReturn(List.of(tagId1, tagId2, tagId3));
        when(matchingDal.findTagById(any(UUID.class))).thenReturn(tagResponse1);

        List<UserMatchingResponse> result = matchingService.getMatchingUsers(
                currentUserId, limit, minMatchCount);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMatchScore()).isEqualTo(expectedScore);
    }

    // getMatchingGroups
    @Test
    void getMatchingGroups_Success_WithMultipleMatches() {
        int limit = 3;
        int minMatchCount = 1;
        int currentUserInterestsCount = 5;

        Map<UUID, Integer> matchingData = new HashMap<>();
        matchingData.put(groupId1, 3); // 3 общих тега
        matchingData.put(groupId2, 4); // 4 общих тега
        matchingData.put(groupId3, 2); // 2 общих тега

        when(matchingDal.getCountUserInterests(currentUserId))
                .thenReturn(currentUserInterestsCount);
        when(matchingDal.getCountGroupTags(groupId1))
                .thenReturn(4);
        when(matchingDal.getCountGroupTags(groupId2))
                .thenReturn(6);
        when(matchingDal.getCountGroupTags(groupId3))
                .thenReturn(3);

        when(matchingDal.getGroupsWithCommonTagsCount(currentUserId, minMatchCount))
                .thenReturn(matchingData);

        when(matchingDal.getCommonTagIdsUserGroup(currentUserId, groupId1))
                .thenReturn(List.of(tagId1, tagId2, tagId3));
        when(matchingDal.getCommonTagIdsUserGroup(currentUserId, groupId2))
                .thenReturn(List.of(tagId1, tagId2, tagId3, tagId4));
        when(matchingDal.getCommonTagIdsUserGroup(currentUserId, groupId3))
                .thenReturn(List.of(tagId1, tagId2));

        when(matchingDal.findTagById(tagId1)).thenReturn(tagResponse1);
        when(matchingDal.findTagById(tagId2)).thenReturn(tagResponse2);
        when(matchingDal.findTagById(tagId3)).thenReturn(tagResponse3);
        when(matchingDal.findTagById(tagId4)).thenReturn(tagResponse4);

        List<GroupMatchingResponse> result = matchingService.getMatchingGroups(
                currentUserId, limit, minMatchCount);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);

        assertThat(result.get(0).getMatchScore()).isGreaterThanOrEqualTo(result.get(1).getMatchScore());

        verify(matchingDal).getGroupsWithCommonTagsCount(currentUserId, minMatchCount);
        verify(matchingDal, times(3)).getCountGroupTags(any(UUID.class));
    }

    @Test
    void getMatchingGroups_ReturnsEmptyList_WhenNoMatches() {
        int limit = 10;
        int minMatchCount = 2;

        when(matchingDal.getGroupsWithCommonTagsCount(currentUserId, minMatchCount))
                .thenReturn(new HashMap<>());

        List<GroupMatchingResponse> result = matchingService.getMatchingGroups(
                currentUserId, limit, minMatchCount);

        assertThat(result).isEmpty();
        verify(matchingDal).getGroupsWithCommonTagsCount(currentUserId, minMatchCount);
        verify(matchingDal, never()).getCountGroupTags(any());
        verify(matchingDal, never()).getCommonTagIdsUserGroup(any(), any());
    }

    @Test
    void getMatchingGroups_CalculatesCorrectMatchScore() {
        int limit = 10;
        int minMatchCount = 1;
        int currentUserInterestsCount = 10;
        int groupTagsCount = 5;
        int commonTagsCount = 3;

        double expectedScore = 25.0;

        Map<UUID, Integer> matchingData = new HashMap<>();
        matchingData.put(groupId1, commonTagsCount);

        when(matchingDal.getCountUserInterests(currentUserId))
                .thenReturn(currentUserInterestsCount);
        when(matchingDal.getCountGroupTags(groupId1))
                .thenReturn(groupTagsCount);
        when(matchingDal.getGroupsWithCommonTagsCount(currentUserId, minMatchCount))
                .thenReturn(matchingData);
        when(matchingDal.getCommonTagIdsUserGroup(currentUserId, groupId1))
                .thenReturn(List.of(tagId1, tagId2, tagId3));
        when(matchingDal.findTagById(any(UUID.class))).thenReturn(tagResponse1);

        List<GroupMatchingResponse> result = matchingService.getMatchingGroups(
                currentUserId, limit, minMatchCount);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMatchScore()).isEqualTo(expectedScore);
    }
}