package com.tandem.interest_service.api.impl;

import com.tandem.interest_service.api.TagApi;
import com.tandem.interest_service.api.mapper.TagApiMapper;
import com.tandem.interest_service.api.model.TagCreateRequestJson;
import com.tandem.interest_service.api.model.TagResponseJson;
import com.tandem.interest_service.api.model.TagUpdateRequestJson;
import com.tandem.interest_service.service.TagService;
import com.tandem.interest_service.service.model.TagRequest;
import com.tandem.interest_service.service.model.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TagApiImpl implements TagApi {

    private final TagService tagService;

    @Override
    public ResponseEntity<List<TagResponseJson>> getAllTags() {
        List<TagResponse> tags = tagService.getAllTags();
        List<TagResponseJson> response = tags.stream()
                .map(TagApiMapper::mapToJson)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<TagResponseJson>> getDefaultTags() {
        List<TagResponse> tags = tagService.getDefaultTags();
        List<TagResponseJson> response = tags.stream()
                .map(TagApiMapper::mapToJson)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<TagResponseJson> createTag(TagCreateRequestJson request) {
        String name = TagApiMapper.mapToServiceModel(request);
        TagResponse createdTag = tagService.createTag(name);
        TagResponseJson response = TagApiMapper.mapToJson(createdTag);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<TagResponseJson> updateTag(UUID id, TagUpdateRequestJson request) {
        TagRequest tagRequest = TagApiMapper.mapToTagRequest(request);
        TagResponse updatedTag = tagService.updateTag(id, tagRequest);

        TagResponseJson response = TagApiMapper.mapToJson(updatedTag);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> deleteTag(UUID id) {
        tagService.deleteTag(id);

        String message = String.format("Тег с ID %s успешно удален", id);
        return ResponseEntity.ok(message);
    }

    @Override
    public ResponseEntity<TagResponseJson> getTag(UUID id) {
        TagResponse tag = tagService.getTag(id);
        TagResponseJson response = TagApiMapper.mapToJson(tag);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<TagResponseJson>> searchTags(String search, int limit) {
        List<TagResponse> tags = tagService.searchTagsByPrefix(search, limit);

        List<TagResponseJson> response = tags.stream()
                .map(TagApiMapper::mapToJson)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}