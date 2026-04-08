package com.tandem.interest_service.dal.impl;

import com.tandem.interest_service.dal.TagDal;
import com.tandem.interest_service.dal.mapper.TagEntityMapper;
import com.tandem.interest_service.dao.TagDao;
import com.tandem.interest_service.dao.TagStatsDao;
import com.tandem.interest_service.dao.model.TagEntity;
import com.tandem.interest_service.dao.model.TagStatsEntity;
import com.tandem.interest_service.integration.InterestEventPublisher;
import com.tandem.interest_service.service.model.request.TagRequest;
import com.tandem.interest_service.service.model.response.TagResponse;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TagDalImpl implements TagDal {

    private final TagDao tagDao;
    private final TagStatsDao tagStatsDao;
    private final InterestEventPublisher eventPublisher;

    @Nullable
    private TagStatsEntity getStats(UUID tagId) {
        Optional<TagStatsEntity> stats = tagStatsDao.findByTagId(tagId);

        return stats.orElse(null);
    }

    @Override
    @Transactional
    public TagResponse insert(TagRequest tagRequest) {
        TagEntity entity = TagEntityMapper.mapToEntity(tagRequest);

        tagDao.insert(entity);
        log.info("Inserted tag with id: {}", entity.getId());

        TagResponse response = TagEntityMapper.mapToResponse(entity, null);
        eventPublisher.publishTagCreated(response); // публикация события в кафку
        return response;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        tagDao.delete(id);
        log.info("Deleted tag with id: {}", id);
    }

    @Override
    @Transactional
    public TagResponse update(UUID id, TagRequest tagRequest) {
        TagEntity existingEntity = tagDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));

        TagEntity updatedEntity = TagEntityMapper.mapToEntityWithId(id, tagRequest);

        tagDao.update(updatedEntity);
        TagStatsEntity stats = getStats(id);
        log.info("Updated tag with id: {}", id);

        return TagEntityMapper.mapToResponse(updatedEntity, stats);
    }

    @Override
    public TagResponse get(UUID id) {
        TagEntity entity = tagDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));

        TagStatsEntity stats = getStats(id);

        return TagEntityMapper.mapToResponse(entity, stats);
    }

    @Override
    public List<TagResponse> getAll() {
        List<TagEntity> entities = tagDao.findAll();

        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        List<TagResponse> result = new ArrayList<>();

        for (TagEntity entity : entities) {
            TagStatsEntity stats = getStats(entity.getId());
            TagResponse response = TagEntityMapper.mapToResponse(entity, stats);
            result.add(response);
        }

        return result;
    }

    @Override
    public List<TagResponse> getDefault() {
        List<TagEntity> defaultEntities = tagDao.findDefault();

        if (defaultEntities.isEmpty()) {
            return Collections.emptyList();
        }

        List<TagResponse> result = new ArrayList<>();

        for (TagEntity entity : defaultEntities) {
            TagStatsEntity stats = getStats(entity.getId());
            TagResponse response = TagEntityMapper.mapToResponse(entity, stats);
            result.add(response);
        }

        return result;
    }

    @Override
    public TagResponse getByName(String name) {
        TagEntity entity = tagDao.findByName(name)
                .orElseThrow(() -> new RuntimeException("Tag not found with name: " + name));

        TagStatsEntity stats = getStats(entity.getId());

        return TagEntityMapper.mapToResponse(entity, stats);
    }

    @Override
    public List<TagResponse> searchByNamePrefix(String prefix, int limit) {
        log.debug("Searching tags by prefix: '{}', limit: {}", prefix, limit);

        List<TagEntity> entities = tagDao.searchByNamePrefix(prefix, limit);

        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        List<TagResponse> result = new ArrayList<>();

        for (TagEntity entity : entities) {
            TagStatsEntity stats = getStats(entity.getId());
            TagResponse response = TagEntityMapper.mapToResponse(entity, stats);
            result.add(response);
        }

        result.sort((a, b) -> Integer.compare(b.getUsageCount(), a.getUsageCount()));

        return result;
    }
}