package com.tandem.interest_service.service.impl;

import com.tandem.interest_service.dal.TagDal;
import com.tandem.interest_service.service.TagService;
import com.tandem.interest_service.service.exception.TagAlreadyExistsException;
import com.tandem.interest_service.service.exception.TagNotFoundException;
import com.tandem.interest_service.service.model.request.TagRequest;
import com.tandem.interest_service.service.model.response.TagResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagDal tagDal;

    @Override
    public TagResponse createTag(String name) {
        if (existsByName(name)) {
            throw new TagAlreadyExistsException(name);
        }

        TagRequest request = TagRequest.builder()
                .name(name)
                .build();

        TagResponse response = tagDal.insert(request);

        log.info("Successfully created tag with id: {} and name: {}", response.getId(), response.getName());
        return response;
    }

    @Override
    public void deleteTag(UUID id) {
        try {
            tagDal.get(id);
        } catch (RuntimeException e) {
            throw new TagNotFoundException(id);
        }

        tagDal.delete(id);
        log.info("Successfully deleted tag with id: {}", id);
    }

    @Override
    public TagResponse getTag(UUID id) {
        try {
            return tagDal.get(id);
        } catch (RuntimeException e) {
            throw new TagNotFoundException(id);
        }
    }

    @Override
    public List<TagResponse> getAllTags() {
        List<TagResponse> tags = tagDal.getAll();

        if (tags.isEmpty()) {
            log.debug("No tags found in database");
        }

        return tags;
    }

    @Override
    public List<TagResponse> getDefaultTags() {
        log.debug("Fetching default tags");
        return tagDal.getDefault();
    }

    @Override
    public TagResponse updateTag(UUID id, TagRequest request) {
        TagResponse existingTag;
        try {
            existingTag = tagDal.get(id);
        } catch (RuntimeException e) {
            throw new TagNotFoundException(id);
        }

        String newName;

        // Если имя меняется, проверяем уникальность
        if (request.getName() != null && !request.getName().equals(existingTag.getName())) {
            if (existsByName(request.getName())) {
                throw new TagAlreadyExistsException(request.getName());
            }
            newName = request.getName();
        }
        // Если имя не меняется, устанавливаем старое значение
        else {
            newName = existingTag.getName();
        }

        TagRequest newRequest = TagRequest.builder()
                .name(newName)
                .imageUrl(request.getImageUrl())
                .build();

        TagResponse response = tagDal.update(id, newRequest);

        log.info("Successfully updated tag with id: {}", id);
        return response;
    }

    @Override
    public TagResponse findByName(String name) {
        try {
            return tagDal.getByName(name);
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public boolean existsByName(String name) {
        return findByName(name) != null;
    }

    @Override
    public List<TagResponse> searchTagsByPrefix(String prefix, int limit) {
        log.debug("Searching tags by prefix: '{}', limit: {}", prefix, limit);

        List<TagResponse> tags = tagDal.searchByNamePrefix(prefix.trim(), limit);

        log.debug("Found {} tags by prefix '{}'", tags.size(), prefix);
        return tags;
    }
}
