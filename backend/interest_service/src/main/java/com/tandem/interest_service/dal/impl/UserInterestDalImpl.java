package com.tandem.interest_service.dal.impl;

import com.tandem.interest_service.dal.UserInterestDal;
import com.tandem.interest_service.dal.mapper.TagEntityMapper;
import com.tandem.interest_service.dal.mapper.UserInterestEntityMapper;
import com.tandem.interest_service.dao.TagStatsDao;
import com.tandem.interest_service.dao.TagDao;
import com.tandem.interest_service.dao.UserInterestDao;
import com.tandem.interest_service.dao.model.TagEntity;
import com.tandem.interest_service.dao.model.TagStatsEntity;
import com.tandem.interest_service.dao.model.UserInterestEntity;
import com.tandem.interest_service.integration.InterestEventPublisher;
import com.tandem.interest_service.service.model.response.TagResponse;
import com.tandem.interest_service.service.model.request.UserInterestRequest;
import com.tandem.interest_service.service.model.response.UserInterestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserInterestDalImpl implements UserInterestDal {

    private final UserInterestDao userInterestDao;
    private final TagDao tagDao;
    private final TagStatsDao tagStatsDao;
    private final InterestEventPublisher eventPublisher;

    @Override
    @Transactional
    public List<UserInterestResponse> insert(List<UserInterestRequest> requests) {
        UUID userId = requests.get(0).getUserId();
        log.debug("Batch adding {} interests for user: {}", requests.size(), userId);

        List<UUID> tagIds = requests.stream()
                .map(UserInterestRequest::getTagId)
                .distinct()
                .toList();

        Map<UUID, TagResponse> tagResponses = new HashMap<>();
        for (UUID tagId : tagIds) {
            try {
                TagEntity entity = tagDao.findById(tagId)
                        .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));

                Optional<TagStatsEntity> statsOpt = tagStatsDao.findByTagId(tagId);
                TagStatsEntity stats = statsOpt.orElse(null);

                TagResponse tagResponse = TagEntityMapper.mapToResponse(entity, stats);
                tagResponses.put(tagId, tagResponse); // добавляем только существующие теги
            } catch (Exception e) {
                throw new RuntimeException("Tag not found with id: " + tagId);
            }
        }

        List<UserInterestEntity> existingEntities = userInterestDao.findByUserId(userId);
        Set<UUID> existingTagIds = existingEntities.stream()
                .map(UserInterestEntity::getTagId)
                .collect(Collectors.toSet());

        List<UserInterestRequest> newRequests = requests.stream()
                .filter(request -> !existingTagIds.contains(request.getTagId()))
                .toList(); // только новые теги

        if (newRequests.isEmpty()) {
            log.info("No new interests to add for user: {}", userId);
            return List.of();
        }

        List<UserInterestEntity> entities = newRequests.stream()
                .map(UserInterestEntityMapper::toEntity)
                .collect(Collectors.toList());

        userInterestDao.insertBatch(entities);
        tagStatsDao.refreshMaterializedView();

        List<UserInterestResponse> result = entities.stream()
                .map(entity -> UserInterestEntityMapper.toResponse(
                        entity,
                        tagResponses.get(entity.getTagId())
                ))
                .collect(Collectors.toList());

        log.debug("Successfully batch added {} interests for user: {}", result.size(), userId);
        eventPublisher.publishInterestsUpdated(result);
        return result;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        userInterestDao.delete(id);
        tagStatsDao.refreshMaterializedView();
        log.debug("Deleted user interest with id: {}", id);
    }


    @Override
    public List<UserInterestResponse> getUserInterests(UUID userId) {
        log.debug("Getting interests for user: {}", userId);

        List<UserInterestEntity> entities = userInterestDao.findByUserId(userId);

        if (entities.isEmpty()) {
            log.debug("No interests found for user: {}", userId);
            return List.of();
        }

        List<UserInterestResponse> result = new ArrayList<>();

        for (UserInterestEntity entity : entities) {
            try {
                TagEntity entityTag = tagDao.findById(entity.getTagId())
                        .orElseThrow(() -> new RuntimeException("Tag not found with id: " + entity.getTagId()));

                Optional<TagStatsEntity> statsOpt = tagStatsDao.findByTagId(entity.getTagId());
                TagStatsEntity stats = statsOpt.orElse(null);

                TagResponse tagResponse = TagEntityMapper.mapToResponse(entityTag, stats);

                result.add(UserInterestEntityMapper.toResponse(entity, tagResponse));
            } catch (Exception e) {
                log.error("Failed to get tag with id: {}, skipping this interest",
                        entity.getTagId(), e);
            }
        }

        log.debug("Found {} interests for user: {}", result.size(), userId);
        return result;
    }

    @Override
    public UserInterestResponse getUserInterest(UUID userId, UUID tagId) {
        log.debug("Getting interest for user: {} and tag: {}", userId, tagId);

        TagResponse tagResponse;
        try {
            TagEntity entity = tagDao.findById(tagId)
                    .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));

            Optional<TagStatsEntity> statsOpt = tagStatsDao.findByTagId(tagId);
            TagStatsEntity stats = statsOpt.orElse(null);

            tagResponse = TagEntityMapper.mapToResponse(entity, stats);
        } catch (Exception e) {
            log.error("Tag not found with id: {}", tagId);
            throw new RuntimeException("Tag not found with id: " + tagId);
        }

        UserInterestEntity entity = userInterestDao.findByUserIdAndTagId(userId, tagId)
                .orElseThrow(() -> {
                    log.error("Interest not found for user: {} and tag: {}", userId, tagId);
                    return new RuntimeException(
                            String.format("Interest not found for user %s and tag %s",
                                    userId, tagId));
                });

        return UserInterestEntityMapper.toResponse(entity, tagResponse);
    }



    @Override
    public TagResponse findTagByName(String name) {
        log.debug("Finding tag by name: {}", name);

        TagEntity entity = tagDao.findByName(name)
                .orElseThrow(() -> new RuntimeException("Tag not found with name: " + name));

        TagStatsEntity stats = tagStatsDao.findByTagId(entity.getId())
                .orElse(null);

        return TagEntityMapper.mapToResponse(entity, stats);
    }
}
