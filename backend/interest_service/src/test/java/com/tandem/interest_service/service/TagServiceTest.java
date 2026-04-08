package com.tandem.interest_service.service;

import com.tandem.interest_service.dal.TagDal;
import com.tandem.interest_service.service.exception.TagAlreadyExistsException;
import com.tandem.interest_service.service.exception.TagNotFoundException;
import com.tandem.interest_service.service.impl.TagServiceImpl;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;


@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagDal tagDal;

    private TagService tagService;

    // Тестовые данные
    private UUID tagId1;
    private UUID tagId2;
    private TagResponse tagResponse1;
    private TagResponse tagResponse2;
    private TagResponse tagResponseWithImage;
    private TagRequest tagRequest;

    @BeforeEach
    void setUp() {
        tagService = new TagServiceImpl(tagDal);
        initializeTestData();
    }

    private void initializeTestData() {
        tagId1 = UUID.randomUUID();
        tagId2 = UUID.randomUUID();

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

        tagResponseWithImage = TagResponse.builder()
                .id(UUID.randomUUID())
                .name("music")
                .imageUrl("http://example.com/music.jpg")
                .usageCount(0)
                .build();

        tagRequest = TagRequest.builder()
                .name("gaming")
                .imageUrl(null)
                .build();
    }

    // createTag
    @Test
    void createTag_Success() {
        String tagName = "programming";
        UUID expectedId = UUID.randomUUID();

        // Ожидаемый ответ от DAL
        TagResponse expectedResponse = TagResponse.builder()
                .id(expectedId)
                .name(tagName)
                .usageCount(0)
                .build();

        when(tagDal.getByName(tagName)).thenThrow(new RuntimeException()); // Тега нет - можно создать
        when(tagDal.insert(any(TagRequest.class))).thenReturn(expectedResponse);

        TagResponse result = tagService.createTag(tagName);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectedId);
        assertThat(result.getName()).isEqualTo(tagName);
        assertThat(result.getUsageCount()).isZero();

        verify(tagDal).getByName(tagName);

        ArgumentCaptor<TagRequest> requestCaptor = ArgumentCaptor.forClass(TagRequest.class);
        verify(tagDal).insert(requestCaptor.capture());

        TagRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getName()).isEqualTo(tagName);
        assertThat(capturedRequest.getImageUrl()).isNull();
    }

    @Test
    void createTag_ThrowsException_TagAlreadyExists() {
        String tagName = "gaming";
        when(tagDal.getByName(tagName)).thenReturn(tagResponse1);

        TagAlreadyExistsException exception = assertThrows(TagAlreadyExistsException.class,
                () -> tagService.createTag(tagName));

        assertThat(exception.getMessage()).contains("Tag with name 'gaming' already exists");
        verify(tagDal).getByName(tagName);
        verify(tagDal, never()).insert(any());
    }

    // deleteTag
    @Test
    void deleteTag_Success() {
        when(tagDal.get(tagId1)).thenReturn(tagResponse1);
        doNothing().when(tagDal).delete(tagId1);

        tagService.deleteTag(tagId1);

        verify(tagDal).get(tagId1);
        verify(tagDal).delete(tagId1);
    }

    @Test
    void deleteTag_ThrowsException_TagNotFound() {
        when(tagDal.get(tagId1)).thenThrow(new RuntimeException("Tag not found"));

        TagNotFoundException exception = assertThrows(TagNotFoundException.class,
                () -> tagService.deleteTag(tagId1));

        assertThat(exception.getMessage()).contains("Tag not found with id: " + tagId1);
        verify(tagDal).get(tagId1);
        verify(tagDal, never()).delete(any());
    }

    // getTag
    @Test
    void getTag_Success() {
        when(tagDal.get(tagId1)).thenReturn(tagResponse1);

        TagResponse result = tagService.getTag(tagId1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(tagId1);
        assertThat(result.getName()).isEqualTo("gaming");
        verify(tagDal).get(tagId1);
    }

    @Test
    void getTag_ThrowsException_TagNotFound() {
        when(tagDal.get(tagId1)).thenThrow(new RuntimeException("Tag not found"));

        TagNotFoundException exception = assertThrows(TagNotFoundException.class,
                () -> tagService.getTag(tagId1));

        assertThat(exception.getMessage()).contains("Tag not found with id: " + tagId1);
        verify(tagDal).get(tagId1);
    }

    // getAllTags
    @Test
    void getAllTags_Success() {
        List<TagResponse> expectedTags = Arrays.asList(tagResponse1, tagResponse2);
        when(tagDal.getAll()).thenReturn(expectedTags);

        List<TagResponse> result = tagService.getAllTags();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(tagResponse1, tagResponse2);
        verify(tagDal).getAll();
    }

    @Test
    void getAllTags_ReturnsEmptyList() {
        when(tagDal.getAll()).thenReturn(Collections.emptyList());

        List<TagResponse> result = tagService.getAllTags();

        assertThat(result).isEmpty();
        verify(tagDal).getAll();
    }

    // getDefaultTags
    @Test
    void getDefaultTags_Success() {
        List<TagResponse> expectedTags = Arrays.asList(tagResponse2, tagResponseWithImage);
        when(tagDal.getDefault()).thenReturn(expectedTags);

        List<TagResponse> result = tagService.getDefaultTags();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getImageUrl()).isNotNull();
        assertThat(result.get(1).getImageUrl()).isNotNull();
        verify(tagDal).getDefault();
    }

    @Test
    void getDefaultTags_ReturnsEmptyList() {
        when(tagDal.getDefault()).thenReturn(Collections.emptyList());

        List<TagResponse> result = tagService.getDefaultTags();

        assertThat(result).isEmpty();
        verify(tagDal).getDefault();
    }

    // updateTag
    @Test
    void updateTag_Success_UpdateBothFields() {
        TagRequest updateRequest = TagRequest.builder()
                .name("new_gaming")
                .imageUrl("http://example.com/new.jpg")
                .build();

        TagResponse updatedResponse = TagResponse.builder()
                .id(tagId1)
                .name("new_gaming")
                .imageUrl("http://example.com/new.jpg")
                .usageCount(5)
                .build();

        when(tagDal.get(tagId1)).thenReturn(tagResponse1);
        when(tagDal.getByName("new_gaming")).thenThrow(new RuntimeException());
        when(tagDal.update(eq(tagId1), any(TagRequest.class))).thenReturn(updatedResponse);

        TagResponse result = tagService.updateTag(tagId1, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("new_gaming");
        assertThat(result.getImageUrl()).isEqualTo("http://example.com/new.jpg");
        verify(tagDal).get(tagId1);
        verify(tagDal).getByName("new_gaming");
        verify(tagDal).update(eq(tagId1), any(TagRequest.class));
    }

    @Test
    void updateTag_Success_UpdateOnlyName() {
        TagRequest updateRequest = TagRequest.builder()
                .name("new_gaming")
                .build();

        TagResponse updatedResponse = TagResponse.builder()
                .id(tagId1)
                .name("new_gaming")
                .imageUrl(null)
                .usageCount(5)
                .build();

        when(tagDal.get(tagId1)).thenReturn(tagResponse1);
        when(tagDal.getByName("new_gaming")).thenThrow(new RuntimeException());
        when(tagDal.update(eq(tagId1), any(TagRequest.class))).thenReturn(updatedResponse);

        TagResponse result = tagService.updateTag(tagId1, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("new_gaming");
        assertThat(result.getImageUrl()).isNull();
        verify(tagDal).update(eq(tagId1), any(TagRequest.class));
    }

    @Test
    void updateTag_Success_UpdateOnlyImageUrl() {
        TagRequest updateRequest = TagRequest.builder()
                .imageUrl("http://example.com/new.jpg")
                .build();

        TagResponse updatedResponse = TagResponse.builder()
                .id(tagId1)
                .name("gaming")
                .imageUrl("http://example.com/new.jpg")
                .usageCount(5)
                .build();

        when(tagDal.get(tagId1)).thenReturn(tagResponse1);
        when(tagDal.update(eq(tagId1), any(TagRequest.class))).thenReturn(updatedResponse);

        TagResponse result = tagService.updateTag(tagId1, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("gaming");
        assertThat(result.getImageUrl()).isEqualTo("http://example.com/new.jpg");
        verify(tagDal, never()).getByName(anyString());
        verify(tagDal).update(eq(tagId1), any(TagRequest.class));
    }

    @Test
    void updateTag_ThrowsException_TagNotFound() {
        when(tagDal.get(tagId1)).thenThrow(new RuntimeException("Tag not found"));

        TagNotFoundException exception = assertThrows(TagNotFoundException.class,
                () -> tagService.updateTag(tagId1, tagRequest));

        assertThat(exception.getMessage()).contains("Tag not found with id: " + tagId1);
        verify(tagDal).get(tagId1);
        verify(tagDal, never()).update(any(), any());
    }

    @Test
    void updateTag_ThrowsException_NameAlreadyExists() {
        TagRequest updateRequest = TagRequest.builder()
                .name("reading")
                .build();

        when(tagDal.get(tagId1)).thenReturn(tagResponse1);
        when(tagDal.getByName("reading")).thenReturn(tagResponse2);

        TagAlreadyExistsException exception = assertThrows(TagAlreadyExistsException.class,
                () -> tagService.updateTag(tagId1, updateRequest));

        assertThat(exception.getMessage()).contains("Tag with name 'reading' already exists");
        verify(tagDal).get(tagId1);
        verify(tagDal).getByName("reading");
        verify(tagDal, never()).update(any(), any());
    }

    // existsByName
    @Test
    void existsByName_ReturnsTrue_TagExists() {
        String tagName = "gaming";
        when(tagDal.getByName(tagName)).thenReturn(tagResponse1);

        boolean result = tagService.existsByName(tagName);

        assertThat(result).isTrue();
        verify(tagDal).getByName(tagName);
    }

    @Test
    void existsByName_ReturnsFalse_TagDoesNotExist() {
        String tagName = "nonexistent";
        when(tagDal.getByName(tagName)).thenThrow(new RuntimeException("Tag not found"));

        boolean result = tagService.existsByName(tagName);

        assertThat(result).isFalse();
        verify(tagDal).getByName(tagName);
    }

    // searchTagsByPrefix
    @Test
    void searchTagsByPrefix_Success() {
        String prefix = "ga";
        int limit = 10;
        List<TagResponse> expectedTags = Arrays.asList(tagResponse1, tagResponse2);

        when(tagDal.searchByNamePrefix(prefix, limit)).thenReturn(expectedTags);

        List<TagResponse> result = tagService.searchTagsByPrefix(prefix, limit);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(tagResponse1, tagResponse2);
        verify(tagDal).searchByNamePrefix(prefix, limit);
    }

    @Test
    void searchTagsByPrefix_ReturnsEmptyList() {
        String prefix = "xyz";
        int limit = 10;

        when(tagDal.searchByNamePrefix(prefix, limit)).thenReturn(Collections.emptyList());

        List<TagResponse> result = tagService.searchTagsByPrefix(prefix, limit);

        assertThat(result).isEmpty();
        verify(tagDal).searchByNamePrefix(prefix, limit);
    }

    // findByName
    @Test
    void findByName_Success_WhenTagExists() {
        String tagName = "gaming";
        when(tagDal.getByName(tagName)).thenReturn(tagResponse1);

        TagResponse result = tagService.findByName(tagName);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(tagId1);
        assertThat(result.getName()).isEqualTo(tagName);
        verify(tagDal).getByName(tagName);
    }

    @Test
    void findByName_ReturnsNull_WhenTagDoesNotExist() {
        String tagName = "nonexistent";
        when(tagDal.getByName(tagName)).thenThrow(new RuntimeException("Tag not found"));

        TagResponse result = tagService.findByName(tagName);

        assertThat(result).isNull();
        verify(tagDal).getByName(tagName);
    }
}
