package com.tandem.interest_service.dal;

import com.tandem.interest_service.dal.impl.MatchingDalImpl;
import com.tandem.interest_service.dao.GroupTagDao;
import com.tandem.interest_service.dao.TagDao;
import com.tandem.interest_service.dao.TagStatsDao;
import com.tandem.interest_service.dao.UserInterestDao;
import com.tandem.interest_service.dao.model.TagEntity;
import com.tandem.interest_service.dao.model.TagStatsEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchingDalTest {
    @Mock
    private UserInterestDao userInterestDao;

    @Mock
    private TagDao tagDao;

    @Mock
    private TagStatsDao tagStatsDao;

    @Mock
    GroupTagDao groupTagDao;

    private MatchingDal matchingDal;

    // Тестовые данные
    private UUID userId;
    private UUID tagId1;
    private UUID tagId2;
    private UUID tagId3;

    @BeforeEach
    void setUp() {
        matchingDal = new MatchingDalImpl(userInterestDao, groupTagDao, tagDao, tagStatsDao);
        initializeTestData();
    }

    private void initializeTestData() {
        userId = UUID.randomUUID();
        tagId1 = UUID.randomUUID();
        tagId2 = UUID.randomUUID();
        tagId3 = UUID.randomUUID();
    }

    // getUsersWithCommonTagsCount
    @Test
    void getUsersWithCommonTagsCount_Success() {
        int minMatchCount = 2;
        UUID otherUserId1 = UUID.randomUUID();
        UUID otherUserId2 = UUID.randomUUID();

        List<Map<String, Object>> rows = new ArrayList<>();

        Map<String, Object> row1 = new HashMap<>();
        row1.put("user_id", otherUserId1);
        row1.put("common_count", 5);
        rows.add(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("user_id", otherUserId2);
        row2.put("common_count", 3);
        rows.add(row2);

        when(userInterestDao.findUsersWithCommonTagsCount(userId, minMatchCount))
                .thenReturn(rows);

        Map<UUID, Integer> result = matchingDal.getUsersWithCommonTagsCount(userId, minMatchCount);

        assertThat(result).hasSize(2);
        assertThat(result.get(otherUserId1)).isEqualTo(5);
        assertThat(result.get(otherUserId2)).isEqualTo(3);

        verify(userInterestDao).findUsersWithCommonTagsCount(userId, minMatchCount);
    }

    @Test
    void getUsersWithCommonTagsCount_ReturnsEmptyMap_WhenNoMatches() {
        int minMatchCount = 10;

        when(userInterestDao.findUsersWithCommonTagsCount(userId, minMatchCount))
                .thenReturn(Collections.emptyList());

        Map<UUID, Integer> result = matchingDal.getUsersWithCommonTagsCount(userId, minMatchCount);

        assertThat(result).isEmpty();
    }

    // getCountUserInterests
    @Test
    void getCountUserInterests_Success() {
        int expectedCount = 5;
        when(userInterestDao.countUserInterests(userId)).thenReturn(expectedCount);

        int result = matchingDal.getCountUserInterests(userId);

        assertThat(result).isEqualTo(expectedCount);
        verify(userInterestDao).countUserInterests(userId);
    }

    @Test
    void getCountUserInterests_ReturnsZero_WhenNoInterests() {
        when(userInterestDao.countUserInterests(userId)).thenReturn(0);

        int result = matchingDal.getCountUserInterests(userId);

        assertThat(result).isZero();
    }

    @Test
    void getCountUserInterests_ReturnsZero_WhenExceptionOccurs() {
        when(userInterestDao.countUserInterests(userId)).thenThrow(new RuntimeException("Database error"));

        int result = matchingDal.getCountUserInterests(userId);

        assertThat(result).isZero();
        verify(userInterestDao).countUserInterests(userId);
    }

    // getCommonTagIds
    @Test
    void getCommonTagIds_Success() {
        UUID otherUserId = UUID.randomUUID();
        List<UUID> expectedTagIds = Arrays.asList(tagId1, tagId2, tagId3);

        when(userInterestDao.findCommonTagIds(userId, otherUserId))
                .thenReturn(expectedTagIds);

        List<UUID> result = matchingDal.getCommonTagIds(userId, otherUserId);

        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(tagId1, tagId2, tagId3);

        verify(userInterestDao).findCommonTagIds(userId, otherUserId);
    }

    @Test
    void getCommonTagIds_ReturnsEmptyList_WhenNoCommonTags() {
        UUID otherUserId = UUID.randomUUID();

        when(userInterestDao.findCommonTagIds(userId, otherUserId))
                .thenReturn(Collections.emptyList());

        List<UUID> result = matchingDal.getCommonTagIds(userId, otherUserId);

        assertThat(result).isEmpty();
    }

    @Test
    void getCommonTagIds_ReturnsEmptyList_WhenExceptionOccurs() {
        UUID otherUserId = UUID.randomUUID();

        when(userInterestDao.findCommonTagIds(userId, otherUserId))
                .thenThrow(new RuntimeException("Database error"));

        List<UUID> result = matchingDal.getCommonTagIds(userId, otherUserId);

        assertThat(result).isEmpty();
        verify(userInterestDao).findCommonTagIds(userId, otherUserId);
    }

    // findTagById
    @Test
    void findTagById_Success_WithStats() {
        TagEntity tag = TagEntity.builder()
                .id(tagId1)
                .name("Java")
                .build();

        TagStatsEntity stats = TagStatsEntity.builder()
                .tagId(tagId1)
                .usageCount(15)
                .build();

        when(tagDao.findById(tagId1)).thenReturn(Optional.of(tag));
        when(tagStatsDao.findByTagId(tagId1)).thenReturn(Optional.of(stats));

        var result = matchingDal.findTagById(tagId1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(tagId1);
        assertThat(result.getName()).isEqualTo("Java");
        assertThat(result.getUsageCount()).isEqualTo(15L);
    }

    @Test
    void findTagById_ThrowsException_WhenTagNotFound() {
        when(tagDao.findById(tagId1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> matchingDal.findTagById(tagId1));

        assertThat(exception.getMessage()).contains("Tag not found");
    }

    // getGroupsWithCommonTagsCount
    @Test
    void getGroupsWithCommonTagsCount_Success() {
        int minMatchCount = 2;
        UUID groupId1 = UUID.randomUUID();
        UUID groupId2 = UUID.randomUUID();

        Map<String, Object> row1 = Map.of("group_id", groupId1, "common_count", 4);
        Map<String, Object> row2 = Map.of("group_id", groupId2, "common_count", 2);

        when(groupTagDao.findGroupsWithCommonTagsWithUserCount(userId, minMatchCount))
                .thenReturn(List.of(row1, row2));

        Map<UUID, Integer> result = matchingDal.getGroupsWithCommonTagsCount(userId, minMatchCount);

        assertThat(result).hasSize(2);
        assertThat(result.get(groupId1)).isEqualTo(4);
        assertThat(result.get(groupId2)).isEqualTo(2);

        verify(groupTagDao).findGroupsWithCommonTagsWithUserCount(userId, minMatchCount);
    }

    @Test
    void getGroupsWithCommonTagsCount_ReturnsEmptyMap_WhenNoMatches() {
        int minMatchCount = 5;

        when(groupTagDao.findGroupsWithCommonTagsWithUserCount(userId, minMatchCount))
                .thenReturn(Collections.emptyList());

        Map<UUID, Integer> result = matchingDal.getGroupsWithCommonTagsCount(userId, minMatchCount);

        assertThat(result).isEmpty();
    }

    // getCountGroupTags
    @Test
    void getCountGroupTags_Success() {
        UUID groupId = UUID.randomUUID();
        int expectedCount = 7;

        when(groupTagDao.countGroupTags(groupId)).thenReturn(expectedCount);

        int result = matchingDal.getCountGroupTags(groupId);

        assertThat(result).isEqualTo(expectedCount);
        verify(groupTagDao).countGroupTags(groupId);
    }

    // getCommonTagIdsUserGroup
    @Test
    void getCommonTagIdsUserGroup_Success() {
        UUID groupId = UUID.randomUUID();
        List<UUID> expectedTags = Arrays.asList(tagId1, tagId2);

        when(groupTagDao.findCommonTagIdsBetweenUserAndGroup(userId, groupId))
                .thenReturn(expectedTags);

        List<UUID> result = matchingDal.getCommonTagIdsUserGroup(userId, groupId);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(tagId1, tagId2);

        verify(groupTagDao).findCommonTagIdsBetweenUserAndGroup(userId, groupId);
    }
}
