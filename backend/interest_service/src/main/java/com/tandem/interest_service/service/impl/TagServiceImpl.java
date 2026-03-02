package com.tandem.interest_service.service.impl;

import com.tandem.interest_service.dal.TagDal;
import com.tandem.interest_service.service.TagService;
import com.tandem.interest_service.service.exception.TagAlreadyExistsException;
import com.tandem.interest_service.service.exception.TagNotFoundException;
import com.tandem.interest_service.service.model.TagRequest;
import com.tandem.interest_service.service.model.TagResponse;
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

        // Если имя меняется, проверяем уникальность
        if (request.getName()!= null && !request.getName().equals(existingTag.getName())) {
            if (existsByName(request.getName())) {
                throw new TagAlreadyExistsException(request.getName());
            }
        }
        // Если имя не меняется, устанавливаем старое значение
        else {
            request.setName(existingTag.getName());
        }
        TagResponse response = tagDal.update(id, request);

        log.info("Successfully updated tag with id: {}", id);
        return response;
    }

    @Override
    public boolean existsByName(String name) {
        // Проверка на уникальность имени
        try {
            tagDal.getByName(name);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public List<TagResponse> searchTagsByPrefix(String prefix, int limit) {
        log.debug("Searching tags by prefix: '{}', limit: {}", prefix, limit);

        List<TagResponse> tags = tagDal.searchByNamePrefix(prefix.trim(), limit);

        log.debug("Found {} tags by prefix '{}'", tags.size(), prefix);
        return tags;
    }
}
