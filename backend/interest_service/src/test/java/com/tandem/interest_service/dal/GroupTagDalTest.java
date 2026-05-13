package com.tandem.interest_service.dal;

import com.tandem.interest_service.dal.impl.GroupTagDalImpl;
import com.tandem.interest_service.dao.GroupTagDao;
import com.tandem.interest_service.dao.TagDao;
import com.tandem.interest_service.dao.TagStatsDao;
import com.tandem.interest_service.dao.model.GroupTagEntity;
import com.tandem.interest_service.dao.model.TagEntity;
import com.tandem.interest_service.dao.model.TagStatsEntity;
import com.tandem.interest_service.service.model.request.GroupInterestRequest;
import com.tandem.interest_service.service.model.response.GroupInterestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupTagDalTest {

    @Mock
    private GroupTagDao groupTagDao;
    @Mock
    private TagDao tagDao;
    @Mock
    private TagStatsDao tagStatsDao;

    private GroupTagDal groupTagDal;

    private UUID groupId;
    private UUID tagId;
    private UUID entityId;
    private TagEntity tagEntity;
    private GroupTagEntity groupTagEntity;

    @BeforeEach
    void setUp() {
        groupTagDal = new GroupTagDalImpl(groupTagDao, tagDao, tagStatsDao);

        groupId = UUID.randomUUID();
        tagId = UUID.randomUUID();
        entityId = UUID.randomUUID();

        tagEntity = TagEntity.builder()
                .id(tagId)
                .name("Spring Boot")
                .build();

        groupTagEntity = GroupTagEntity.builder()
                .id(entityId)
                .groupId(groupId)
                .tagId(tagId)
                .build();
    }

    // insert
    @Test
    void insert_Success() {
        GroupInterestRequest request = GroupInterestRequest.builder()
                .groupId(groupId)
                .tagId(tagId)
                .build();

        when(tagDao.findById(tagId)).thenReturn(Optional.of(tagEntity));
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.empty());
        when(groupTagDao.findByGroupId(groupId)).thenReturn(List.of());

        List<GroupInterestResponse> result = groupTagDal.insert(List.of(request));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGroupId()).isEqualTo(groupId);
        assertThat(result.get(0).getTag().getName()).isEqualTo("Spring Boot");

        verify(groupTagDao).insertBatch(anyList());
    }

    @Test
    void insert_ReturnsEmpty_WhenTagsAlreadyExist() {
        GroupInterestRequest request = GroupInterestRequest.builder()
                .groupId(groupId)
                .tagId(tagId)
                .build();

        when(tagDao.findById(tagId)).thenReturn(Optional.of(tagEntity));
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.empty());
        when(groupTagDao.findByGroupId(groupId)).thenReturn(List.of(groupTagEntity));

        List<GroupInterestResponse> result = groupTagDal.insert(List.of(request));

        assertThat(result).isEmpty();
        verify(groupTagDao, never()).insertBatch(anyList());
    }

    @Test
    void insert_ThrowsException_WhenTagNotFoundInDb() {
        GroupInterestRequest request = GroupInterestRequest.builder()
                .groupId(groupId)
                .tagId(tagId)
                .build();

        when(tagDao.findById(tagId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> groupTagDal.insert(List.of(request)));

        assertThat(exception.getMessage()).contains("Tag not found");
        verify(groupTagDao, never()).insertBatch(anyList());
    }

    // delete
    @Test
    void delete_Success() {
        groupTagDal.delete(entityId);
        verify(groupTagDao).delete(entityId);
    }

    // getGroupInterests
    @Test
    void getGroupInterests_Success() {
        TagStatsEntity stats = TagStatsEntity.builder().tagId(tagId).usageCount(5).build();

        when(groupTagDao.findByGroupId(groupId)).thenReturn(List.of(groupTagEntity));
        when(tagDao.findById(tagId)).thenReturn(Optional.of(tagEntity));
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.of(stats));

        List<GroupInterestResponse> result = groupTagDal.getGroupInterests(groupId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTag().getName()).isEqualTo("Spring Boot");
        assertThat(result.get(0).getTag().getUsageCount()).isEqualTo(5L);
    }

    @Test
    void getGroupInterests_ReturnsEmpty_WhenNoTagsFound() {
        when(groupTagDao.findByGroupId(groupId)).thenReturn(List.of());

        List<GroupInterestResponse> result = groupTagDal.getGroupInterests(groupId);

        assertThat(result).isEmpty();
        verify(tagDao, never()).findById(any());
    }

    @Test
    void getGroupInterest_Success() {
        when(tagDao.findById(tagId)).thenReturn(Optional.of(tagEntity));
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.empty());
        when(groupTagDao.findByGroupIdAndTagId(groupId, tagId)).thenReturn(Optional.of(groupTagEntity));

        GroupInterestResponse result = groupTagDal.getGroupInterest(groupId, tagId);

        assertThat(result).isNotNull();
        assertThat(result.getGroupId()).isEqualTo(groupId);
        assertThat(result.getTag().getId()).isEqualTo(tagId);
    }

    @Test
    void getGroupInterest_ThrowsException_WhenRelationNotFound() {
        when(tagDao.findById(tagId)).thenReturn(Optional.of(tagEntity));
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.empty());
        when(groupTagDao.findByGroupIdAndTagId(groupId, tagId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> groupTagDal.getGroupInterest(groupId, tagId));

        assertThat(exception.getMessage()).contains("Interest not found");
    }
}