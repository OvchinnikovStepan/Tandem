package com.tandem.interest_service.dal;

import com.tandem.interest_service.dal.impl.TagDalImpl;
import com.tandem.interest_service.dao.TagDao;
import com.tandem.interest_service.dao.TagStatsDao;
import com.tandem.interest_service.dao.model.TagEntity;
import com.tandem.interest_service.dao.model.TagStatsEntity;
import com.tandem.interest_service.service.model.request.TagRequest;
import com.tandem.interest_service.service.model.response.TagResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
class TagDalTest {

    @Mock
    private TagDao tagDao;

    @Mock
    private TagStatsDao tagStatsDao;

    private TagDal tagDal;

    // Тестовые данные
    private UUID tagId;
    private UUID tagId2;
    private TagRequest tagRequest;
    private TagEntity tagEntity;
    private TagEntity tagEntity2;
    private TagStatsEntity tagStatsEntity;
    private TagStatsEntity tagStatsEntity2;

    @BeforeEach
    void setUp() {
        tagDal = new TagDalImpl(tagDao, tagStatsDao);
        initializeTestData();
    }

    private void initializeTestData() {
        tagId = UUID.randomUUID();
        tagId2 = UUID.randomUUID();

        tagRequest = TagRequest.builder()
                .name("gaming")
                .imageUrl(null)
                .build();

        tagEntity = TagEntity.builder()
                .id(tagId)
                .name("gaming")
                .imageUrl(null)
                .build();

        tagEntity2 = TagEntity.builder()
                .id(tagId2)
                .name("reading")
                .imageUrl("http://example.com/reading.jpg")
                .build();

        tagStatsEntity = TagStatsEntity.builder()
                .tagId(tagId)
                .usageCount(5)
                .build();

        tagStatsEntity2 = TagStatsEntity.builder()
                .tagId(tagId2)
                .usageCount(3)
                .build();
    }

    // insert
    @Test
    void insert_Success() {
        ArgumentCaptor<TagEntity> entityCaptor = ArgumentCaptor.forClass(TagEntity.class);

        doNothing().when(tagDao).insert(entityCaptor.capture());

        TagResponse result = tagDal.insert(tagRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("gaming");
        assertThat(result.getImageUrl()).isNull();
        assertThat(result.getUsageCount()).isZero();
        assertThat(result.getId()).isNotNull();

        TagEntity capturedEntity = entityCaptor.getValue();
        assertThat(capturedEntity.getName()).isEqualTo("gaming");
        assertThat(capturedEntity.getImageUrl()).isNull();

        verify(tagDao).insert(any(TagEntity.class));
        verify(tagStatsDao, never()).findByTagId(any());
    }

    @Test
    void insert_WithImageUrl_Success() {
        TagRequest requestWithImage = TagRequest.builder()
                .name("music")
                .imageUrl("http://example.com/music.jpg")
                .build();

        ArgumentCaptor<TagEntity> entityCaptor = ArgumentCaptor.forClass(TagEntity.class);
        doNothing().when(tagDao).insert(entityCaptor.capture());

        TagResponse result = tagDal.insert(requestWithImage);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("music");
        assertThat(result.getImageUrl()).isEqualTo("http://example.com/music.jpg");

        TagEntity capturedEntity = entityCaptor.getValue();
        assertThat(capturedEntity.getImageUrl()).isEqualTo("http://example.com/music.jpg");
    }

    // delete
    @Test
    void delete_Success() {
        doNothing().when(tagDao).delete(tagId);

        tagDal.delete(tagId);

        verify(tagDao).delete(tagId);
        verify(tagStatsDao, never()).findByTagId(any());
    }

    // get
    @Test
    void get_Success_WithStats() {
        when(tagDao.findById(tagId)).thenReturn(Optional.of(tagEntity));
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.of(tagStatsEntity));

        TagResponse result = tagDal.get(tagId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(tagId);
        assertThat(result.getName()).isEqualTo("gaming");
        assertThat(result.getUsageCount()).isEqualTo(5);

        verify(tagDao).findById(tagId);
        verify(tagStatsDao).findByTagId(tagId);
    }

    @Test
    void get_Success_WithoutStats() {
        when(tagDao.findById(tagId)).thenReturn(Optional.of(tagEntity));
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.empty());

        TagResponse result = tagDal.get(tagId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(tagId);
        assertThat(result.getName()).isEqualTo("gaming");
        assertThat(result.getUsageCount()).isZero();

        verify(tagDao).findById(tagId);
        verify(tagStatsDao).findByTagId(tagId);
    }

    @Test
    void get_ThrowsException_WhenTagNotFound() {
        when(tagDao.findById(tagId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagDal.get(tagId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tag not found with id: " + tagId);

        verify(tagDao).findById(tagId);
        verify(tagStatsDao, never()).findByTagId(any());
    }

    // getAll
    @Test
    void getAll_Success_WithMultipleTags() {
        List<TagEntity> entities = Arrays.asList(tagEntity, tagEntity2);
        when(tagDao.findAll()).thenReturn(entities);
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.of(tagStatsEntity));
        when(tagStatsDao.findByTagId(tagId2)).thenReturn(Optional.of(tagStatsEntity2));

        List<TagResponse> result = tagDal.getAll();

        assertThat(result).hasSize(2);

        TagResponse response1 = result.get(0);
        assertThat(response1.getId()).isEqualTo(tagId);
        assertThat(response1.getName()).isEqualTo("gaming");
        assertThat(response1.getUsageCount()).isEqualTo(5);

        TagResponse response2 = result.get(1);
        assertThat(response2.getId()).isEqualTo(tagId2);
        assertThat(response2.getName()).isEqualTo("reading");
        assertThat(response2.getUsageCount()).isEqualTo(3);

        verify(tagDao).findAll();
        verify(tagStatsDao, times(2)).findByTagId(any());
    }

    @Test
    void getAll_ReturnsEmptyList_WhenNoTags() {
        when(tagDao.findAll()).thenReturn(Collections.emptyList());

        List<TagResponse> result = tagDal.getAll();

        assertThat(result).isEmpty();
        verify(tagDao).findAll();
        verify(tagStatsDao, never()).findByTagId(any());
    }

    // getDefault
    @Test
    void getDefault_Success_WithMultipleTags() {
        List<TagEntity> defaultEntities = Arrays.asList(tagEntity, tagEntity2);
        when(tagDao.findDefault()).thenReturn(defaultEntities);
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.of(tagStatsEntity));
        when(tagStatsDao.findByTagId(tagId2)).thenReturn(Optional.of(tagStatsEntity2));

        List<TagResponse> result = tagDal.getDefault();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(tagId);
        assertThat(result.get(1).getId()).isEqualTo(tagId2);

        verify(tagDao).findDefault();
        verify(tagStatsDao, times(2)).findByTagId(any());
    }

    @Test
    void getDefault_ReturnsEmptyList_WhenNoDefaultTags() {
        when(tagDao.findDefault()).thenReturn(Collections.emptyList());

        List<TagResponse> result = tagDal.getDefault();

        assertThat(result).isEmpty();
        verify(tagDao).findDefault();
        verify(tagStatsDao, never()).findByTagId(any());
    }

    // update
    @Test
    void update_Success() {
        TagRequest updateRequest = TagRequest.builder()
                .name("new_gaming")
                .imageUrl("http://example.com/new.jpg")
                .build();

        when(tagDao.findById(tagId)).thenReturn(Optional.of(tagEntity));
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.empty());
        doNothing().when(tagDao).update(any(TagEntity.class));

        TagResponse result = tagDal.update(tagId, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getUsageCount()).isZero();

        verify(tagDao).update(any(TagEntity.class));
    }

    @Test
    void update_ThrowsException_WhenTagNotFound() {
        TagRequest updateRequest = TagRequest.builder()
                .name("new_gaming")
                .build();

        when(tagDao.findById(tagId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagDal.update(tagId, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tag not found with id: " + tagId);

        verify(tagDao).findById(tagId);
        verify(tagDao, never()).update(any());
        verify(tagStatsDao, never()).findByTagId(any());
    }

    // getByName
    @Test
    void getByName_Success_WithStats() {
        String tagName = "gaming";
        when(tagDao.findByName(tagName)).thenReturn(Optional.of(tagEntity));
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.of(tagStatsEntity));

        TagResponse result = tagDal.getByName(tagName);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("gaming");
        assertThat(result.getUsageCount()).isEqualTo(5);

        verify(tagDao).findByName(tagName);
        verify(tagStatsDao).findByTagId(tagId);
    }

    @Test
    void getByName_Success_WithoutStats() {
        String tagName = "gaming";
        when(tagDao.findByName(tagName)).thenReturn(Optional.of(tagEntity));
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.empty());

        TagResponse result = tagDal.getByName(tagName);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("gaming");
        assertThat(result.getUsageCount()).isZero();

        verify(tagDao).findByName(tagName);
        verify(tagStatsDao).findByTagId(tagId);
    }

    @Test
    void getByName_ThrowsException_WhenTagNotFound() {
        String tagName = "nonexistent";
        when(tagDao.findByName(tagName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagDal.getByName(tagName))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tag not found with name: " + tagName);

        verify(tagDao).findByName(tagName);
        verify(tagStatsDao, never()).findByTagId(any());
    }

    // searchByNamePrefix
    @Test
    void searchByNamePrefix_Success_WithMultipleResults() {
        String prefix = "ga";
        int limit = 10;
        List<TagEntity> entities = Arrays.asList(tagEntity, tagEntity2);

        when(tagDao.searchByNamePrefix(prefix, limit)).thenReturn(entities);
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.of(tagStatsEntity));
        when(tagStatsDao.findByTagId(tagId2)).thenReturn(Optional.of(tagStatsEntity2));

        List<TagResponse> result = tagDal.searchByNamePrefix(prefix, limit);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsageCount()).isGreaterThan(result.get(1).getUsageCount());

        verify(tagDao).searchByNamePrefix(prefix, limit);
        verify(tagStatsDao, times(2)).findByTagId(any());
    }

    @Test
    void searchByNamePrefix_ReturnsEmptyList_WhenNoMatches() {
        String prefix = "xyz";
        int limit = 10;

        when(tagDao.searchByNamePrefix(prefix, limit)).thenReturn(Collections.emptyList());

        List<TagResponse> result = tagDal.searchByNamePrefix(prefix, limit);

        assertThat(result).isEmpty();
        verify(tagDao).searchByNamePrefix(prefix, limit);
        verify(tagStatsDao, never()).findByTagId(any());
    }

    @Test
    void searchByNamePrefix_WithLimit() {
        String prefix = "g";
        int limit = 1;
        List<TagEntity> entities = List.of(tagEntity);

        when(tagDao.searchByNamePrefix(prefix, limit)).thenReturn(entities);
        when(tagStatsDao.findByTagId(tagId)).thenReturn(Optional.of(tagStatsEntity));

        List<TagResponse> result = tagDal.searchByNamePrefix(prefix, limit);

        assertThat(result).hasSize(1);
        verify(tagDao).searchByNamePrefix(prefix, limit);
    }

    @Test
    void searchByNamePrefix_WhenStatsMissing_SortsByZero() {
        String prefix = "g";
        int limit = 10;

        UUID tagId1 = UUID.randomUUID();
        UUID tagId2 = UUID.randomUUID();

        TagEntity tag1 = TagEntity.builder().id(tagId1).name("game1").build();
        TagEntity tag2 = TagEntity.builder().id(tagId2).name("game2").build();

        List<TagEntity> entities = Arrays.asList(tag1, tag2);

        when(tagDao.searchByNamePrefix(prefix, limit)).thenReturn(entities);
        when(tagStatsDao.findByTagId(tagId1)).thenReturn(Optional.empty());
        when(tagStatsDao.findByTagId(tagId2)).thenReturn(Optional.empty());

        List<TagResponse> result = tagDal.searchByNamePrefix(prefix, limit);

        assertThat(result).hasSize(2);
        // Оба имеют usageCount = 0, порядок может быть любым
        assertThat(result.get(0).getUsageCount()).isZero();
        assertThat(result.get(1).getUsageCount()).isZero();
    }
}
