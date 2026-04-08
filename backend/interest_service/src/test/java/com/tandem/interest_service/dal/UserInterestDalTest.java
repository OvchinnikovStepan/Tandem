package com.tandem.interest_service.dal;

import com.tandem.interest_service.dal.impl.UserInterestDalImpl;
import com.tandem.interest_service.dao.TagStatsDao;
import com.tandem.interest_service.dao.TagDao;
import com.tandem.interest_service.dao.UserInterestDao;
import com.tandem.interest_service.dao.model.TagEntity;
import com.tandem.interest_service.dao.model.TagStatsEntity;
import com.tandem.interest_service.dao.model.UserInterestEntity;
import com.tandem.interest_service.integration.InterestEventPublisher;
import com.tandem.interest_service.service.model.request.UserInterestRequest;
import com.tandem.interest_service.service.model.response.UserInterestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInterestDalTest {

    @Mock
    private UserInterestDao userInterestDao;

    @Mock
    private TagDao tagDao;

    @Mock
    private TagStatsDao tagStatsDao;

    @Mock
    private InterestEventPublisher eventPublisher;

    private UserInterestDal userInterestDal;

    // Тестовые данные
    private UUID userId;
    private UUID tagId1;
    private UUID tagId2;
    private UUID tagId3;
    private UUID interestId1;
    private UUID interestId2;
    private LocalDateTime now;

    private TagEntity tagEntity1;
    private TagEntity tagEntity2;
    private TagEntity tagEntity3;
    private TagStatsEntity tagStatsEntity1;
    private TagStatsEntity tagStatsEntity2;

    private UserInterestRequest request1;
    private UserInterestRequest request2;
    private UserInterestRequest request3;

    private UserInterestEntity entity1;
    private UserInterestEntity entity2;
    private UserInterestEntity entity3;

    @BeforeEach
    void setUp() {
        userInterestDal = new UserInterestDalImpl(userInterestDao, tagDao, tagStatsDao, eventPublisher);
        initializeTestData();
    }

    private void initializeTestData() {
        userId = UUID.randomUUID();
        tagId1 = UUID.randomUUID();
        tagId2 = UUID.randomUUID();
        tagId3 = UUID.randomUUID();
        interestId1 = UUID.randomUUID();
        interestId2 = UUID.randomUUID();
        now = LocalDateTime.now();

        tagEntity1 = TagEntity.builder()
                .id(tagId1)
                .name("gaming")
                .build();

        tagEntity2 = TagEntity.builder()
                .id(tagId2)
                .name("reading")
                .build();

        tagEntity3 = TagEntity.builder()
                .id(tagId3)
                .name("music")
                .build();

        tagStatsEntity1 = TagStatsEntity.builder()
                .tagId(tagId1)
                .usageCount(5)
                .build();

        tagStatsEntity2 = TagStatsEntity.builder()
                .tagId(tagId2)
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

        request3 = UserInterestRequest.builder()
                .userId(userId)
                .tagId(tagId3)
                .build();

        entity1 = UserInterestEntity.builder()
                .id(interestId1)
                .userId(userId)
                .tagId(tagId1)
                .createdAt(now)
                .build();

        entity2 = UserInterestEntity.builder()
                .id(interestId2)
                .userId(userId)
                .tagId(tagId2)
                .createdAt(now)
                .build();

        entity3 = UserInterestEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tagId(tagId3)
                .createdAt(now)
                .build();
    }

    // insert
    @Test
    void insert_Success_WithNewInterests() {
        List<UserInterestRequest> requests = Arrays.asList(request1, request2);

        when(tagDao.findById(tagId1)).thenReturn(Optional.of(tagEntity1));
        when(tagDao.findById(tagId2)).thenReturn(Optional.of(tagEntity2));
        when(tagStatsDao.findByTagId(tagId1)).thenReturn(Optional.of(tagStatsEntity1));
        when(tagStatsDao.findByTagId(tagId2)).thenReturn(Optional.of(tagStatsEntity2));
        when(userInterestDao.findByUserId(userId)).thenReturn(Collections.emptyList());
        doNothing().when(userInterestDao).insertBatch(anyList());
        doNothing().when(tagStatsDao).refreshMaterializedView();

        List<UserInterestResponse> result = userInterestDal.insert(requests);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(0).getTag().getId()).isEqualTo(tagId1);
        assertThat(result.get(1).getTag().getId()).isEqualTo(tagId2);

        verify(tagDao, times(2)).findById(any(UUID.class));
        verify(userInterestDao).findByUserId(userId);
        verify(userInterestDao).insertBatch(anyList());
        verify(tagStatsDao).refreshMaterializedView();
    }

    @Test
    void insert_Success_WithPartialExistingInterests() {
        List<UserInterestRequest> requests = Arrays.asList(request1, request2, request3);

        // У пользователя уже есть tagId1
        List<UserInterestEntity> existingEntities = List.of(entity1);

        when(tagDao.findById(tagId1)).thenReturn(Optional.of(tagEntity1));
        when(tagDao.findById(tagId2)).thenReturn(Optional.of(tagEntity2));
        when(tagDao.findById(tagId3)).thenReturn(Optional.of(tagEntity3));
        when(tagStatsDao.findByTagId(tagId1)).thenReturn(Optional.of(tagStatsEntity1));
        when(tagStatsDao.findByTagId(tagId2)).thenReturn(Optional.of(tagStatsEntity2));
        when(tagStatsDao.findByTagId(tagId3)).thenReturn(Optional.empty());
        when(userInterestDao.findByUserId(userId)).thenReturn(existingEntities);
        doNothing().when(userInterestDao).insertBatch(anyList());
        doNothing().when(tagStatsDao).refreshMaterializedView();

        List<UserInterestResponse> result = userInterestDal.insert(requests);

        // Должны добавиться только новые теги (tagId2 и tagId3)
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTag().getId()).isEqualTo(tagId2);
        assertThat(result.get(1).getTag().getId()).isEqualTo(tagId3);

        ArgumentCaptor<List<UserInterestEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(userInterestDao).insertBatch(captor.capture());

        List<UserInterestEntity> insertedEntities = captor.getValue();
        assertThat(insertedEntities).hasSize(2);
        assertThat(insertedEntities.get(0).getTagId()).isEqualTo(tagId2);
        assertThat(insertedEntities.get(1).getTagId()).isEqualTo(tagId3);
    }

    @Test
    void insert_ReturnsEmptyList_WhenAllInterestsAlreadyExist() {
        List<UserInterestRequest> requests = Arrays.asList(request1, request2);
        List<UserInterestEntity> existingEntities = Arrays.asList(entity1, entity2);

        when(tagDao.findById(tagId1)).thenReturn(Optional.of(tagEntity1));
        when(tagDao.findById(tagId2)).thenReturn(Optional.of(tagEntity2));
        when(tagStatsDao.findByTagId(tagId1)).thenReturn(Optional.of(tagStatsEntity1));
        when(tagStatsDao.findByTagId(tagId2)).thenReturn(Optional.of(tagStatsEntity2));
        when(userInterestDao.findByUserId(userId)).thenReturn(existingEntities);

        List<UserInterestResponse> result = userInterestDal.insert(requests);

        assertThat(result).isEmpty();
        verify(userInterestDao, never()).insertBatch(anyList());
        verify(tagStatsDao, never()).refreshMaterializedView();
    }

    @Test
    void insert_ShouldRefreshMaterializedView_AfterInsert() {
        List<UserInterestRequest> requests = List.of(request1);

        when(tagDao.findById(tagId1)).thenReturn(Optional.of(tagEntity1));
        when(tagStatsDao.findByTagId(tagId1)).thenReturn(Optional.of(tagStatsEntity1));
        when(userInterestDao.findByUserId(userId)).thenReturn(Collections.emptyList());
        doNothing().when(userInterestDao).insertBatch(anyList());
        doNothing().when(tagStatsDao).refreshMaterializedView();

        userInterestDal.insert(requests);

        verify(tagStatsDao).refreshMaterializedView();
    }

    @Test
    void insert_ThrowsException_WhenTagNotFound() {
        List<UserInterestRequest> requests = List.of(request1);

        when(tagDao.findById(tagId1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userInterestDal.insert(requests))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tag not found with id: " + tagId1);

        verify(userInterestDao, never()).insertBatch(anyList());
        verify(tagStatsDao, never()).refreshMaterializedView();
    }

    // delete
    @Test
    void delete_Success() {
        UUID interestId = UUID.randomUUID();
        doNothing().when(userInterestDao).delete(interestId);
        doNothing().when(tagStatsDao).refreshMaterializedView();

        userInterestDal.delete(interestId);

        verify(userInterestDao).delete(interestId);
        verify(tagStatsDao).refreshMaterializedView();
    }

    @Test
    void delete_ShouldRefreshMaterializedView_AfterDelete() {
        UUID interestId = UUID.randomUUID();
        doNothing().when(userInterestDao).delete(interestId);
        doNothing().when(tagStatsDao).refreshMaterializedView();

        userInterestDal.delete(interestId);

        verify(tagStatsDao).refreshMaterializedView();
    }

    // getUserInterests
    @Test
    void getUserInterests_Success_WithMultipleInterests() {
        List<UserInterestEntity> entities = Arrays.asList(entity1, entity2);

        when(userInterestDao.findByUserId(userId)).thenReturn(entities);
        when(tagDao.findById(tagId1)).thenReturn(Optional.of(tagEntity1));
        when(tagDao.findById(tagId2)).thenReturn(Optional.of(tagEntity2));
        when(tagStatsDao.findByTagId(tagId1)).thenReturn(Optional.of(tagStatsEntity1));
        when(tagStatsDao.findByTagId(tagId2)).thenReturn(Optional.of(tagStatsEntity2));

        List<UserInterestResponse> result = userInterestDal.getUserInterests(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(0).getTag().getName()).isEqualTo("gaming");
        assertThat(result.get(1).getTag().getName()).isEqualTo("reading");

        verify(userInterestDao).findByUserId(userId);
        verify(tagDao, times(2)).findById(any(UUID.class));
        verify(tagStatsDao, times(2)).findByTagId(any(UUID.class));
    }

    @Test
    void getUserInterests_ReturnsEmptyList_WhenNoInterests() {
        when(userInterestDao.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<UserInterestResponse> result = userInterestDal.getUserInterests(userId);

        assertThat(result).isEmpty();
        verify(userInterestDao).findByUserId(userId);
        verify(tagDao, never()).findById(any());
    }

    @Test
    void getUserInterests_SkipsInvalidTags() {
        List<UserInterestEntity> entities = Arrays.asList(entity1, entity2);

        when(userInterestDao.findByUserId(userId)).thenReturn(entities);
        when(tagDao.findById(tagId1)).thenReturn(Optional.empty());
        when(tagDao.findById(tagId2)).thenReturn(Optional.of(tagEntity2));
        when(tagStatsDao.findByTagId(tagId2)).thenReturn(Optional.of(tagStatsEntity2));

        List<UserInterestResponse> result = userInterestDal.getUserInterests(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTag().getId()).isEqualTo(tagId2);
    }

    // getUserInterest
    @Test
    void getUserInterest_Success() {
        when(tagDao.findById(tagId1)).thenReturn(Optional.of(tagEntity1));
        when(tagStatsDao.findByTagId(tagId1)).thenReturn(Optional.of(tagStatsEntity1));
        when(userInterestDao.findByUserIdAndTagId(userId, tagId1))
                .thenReturn(Optional.of(entity1));

        UserInterestResponse result = userInterestDal.getUserInterest(userId, tagId1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(interestId1);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTag().getId()).isEqualTo(tagId1);

        verify(tagDao).findById(tagId1);
        verify(userInterestDao).findByUserIdAndTagId(userId, tagId1);
    }

    @Test
    void getUserInterest_ThrowsException_WhenTagNotFound() {
        when(tagDao.findById(tagId1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userInterestDal.getUserInterest(userId, tagId1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tag not found with id: " + tagId1);

        verify(userInterestDao, never()).findByUserIdAndTagId(any(), any());
    }

    @Test
    void getUserInterest_ThrowsException_WhenInterestNotFound() {
        when(tagDao.findById(tagId1)).thenReturn(Optional.of(tagEntity1));
        when(tagStatsDao.findByTagId(tagId1)).thenReturn(Optional.of(tagStatsEntity1));
        when(userInterestDao.findByUserIdAndTagId(userId, tagId1))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userInterestDal.getUserInterest(userId, tagId1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Interest not found for user " + userId + " and tag " + tagId1);
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

        Map<UUID, Integer> result = userInterestDal.getUsersWithCommonTagsCount(userId, minMatchCount);

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

        Map<UUID, Integer> result = userInterestDal.getUsersWithCommonTagsCount(userId, minMatchCount);

        assertThat(result).isEmpty();
    }

    // getCountUserInterests
    @Test
    void getCountUserInterests_Success() {
        int expectedCount = 5;
        when(userInterestDao.countUserInterests(userId)).thenReturn(expectedCount);

        int result = userInterestDal.getCountUserInterests(userId);

        assertThat(result).isEqualTo(expectedCount);
        verify(userInterestDao).countUserInterests(userId);
    }

    @Test
    void getCountUserInterests_ReturnsZero_WhenNoInterests() {
        when(userInterestDao.countUserInterests(userId)).thenReturn(0);

        int result = userInterestDal.getCountUserInterests(userId);

        assertThat(result).isZero();
    }

    @Test
    void getCountUserInterests_ReturnsZero_WhenExceptionOccurs() {
        when(userInterestDao.countUserInterests(userId)).thenThrow(new RuntimeException("Database error"));

        int result = userInterestDal.getCountUserInterests(userId);

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

        List<UUID> result = userInterestDal.getCommonTagIds(userId, otherUserId);

        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(tagId1, tagId2, tagId3);

        verify(userInterestDao).findCommonTagIds(userId, otherUserId);
    }

    @Test
    void getCommonTagIds_ReturnsEmptyList_WhenNoCommonTags() {
        UUID otherUserId = UUID.randomUUID();

        when(userInterestDao.findCommonTagIds(userId, otherUserId))
                .thenReturn(Collections.emptyList());

        List<UUID> result = userInterestDal.getCommonTagIds(userId, otherUserId);

        assertThat(result).isEmpty();
    }

    @Test
    void getCommonTagIds_ReturnsEmptyList_WhenExceptionOccurs() {
        UUID otherUserId = UUID.randomUUID();

        when(userInterestDao.findCommonTagIds(userId, otherUserId))
                .thenThrow(new RuntimeException("Database error"));

        List<UUID> result = userInterestDal.getCommonTagIds(userId, otherUserId);

        assertThat(result).isEmpty();
        verify(userInterestDao).findCommonTagIds(userId, otherUserId);
    }
}