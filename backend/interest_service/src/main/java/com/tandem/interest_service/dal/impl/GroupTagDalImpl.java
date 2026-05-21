package com.tandem.interest_service.dal.impl;

import com.tandem.interest_service.dal.GroupTagDal;
import com.tandem.interest_service.dal.mapper.GroupTagEntityMapper;
import com.tandem.interest_service.dal.mapper.TagEntityMapper;
import com.tandem.interest_service.dao.GroupTagDao;
import com.tandem.interest_service.dao.TagDao;
import com.tandem.interest_service.dao.TagStatsDao;
import com.tandem.interest_service.dao.model.GroupTagEntity;
import com.tandem.interest_service.dao.model.TagEntity;
import com.tandem.interest_service.dao.model.TagStatsEntity;
import com.tandem.interest_service.service.model.request.GroupInterestRequest;
import com.tandem.interest_service.service.model.response.GroupInterestResponse;
import com.tandem.interest_service.service.model.response.TagResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GroupTagDalImpl implements GroupTagDal {

    private final GroupTagDao groupTagDao;
    private final TagDao tagDao;
    private final TagStatsDao tagStatsDao;

    @Override
    @Transactional
    public List<GroupInterestResponse> insert(List<GroupInterestRequest> requests) {
        UUID groupId = requests.get(0).getGroupId();
        log.debug("Batch adding {} tags for group: {}", requests.size(), groupId);

        List<UUID> tagIds = requests.stream()
                .map(GroupInterestRequest::getTagId)
                .distinct()
                .toList();

        Map<UUID, TagResponse> tagResponses = new HashMap<>();
        for (UUID tagId : tagIds) {
            TagEntity entity = tagDao.findById(tagId)
                    .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));

            Optional<TagStatsEntity> statsOpt = tagStatsDao.findByTagId(tagId);
            TagStatsEntity stats = statsOpt.orElse(null);

            TagResponse tagResponse = TagEntityMapper.mapToResponse(entity, stats);
            tagResponses.put(tagId, tagResponse);
        }

        List<GroupTagEntity> existingEntities = groupTagDao.findByGroupId(groupId);
        Set<UUID> existingTagIds = existingEntities.stream()
                .map(GroupTagEntity::getTagId)
                .collect(Collectors.toSet());

        List<GroupInterestRequest> newRequests = requests.stream()
                .filter(request -> !existingTagIds.contains(request.getTagId()))
                .toList();

        if (newRequests.isEmpty()) {
            log.info("No new tags to add for group: {}", groupId);
            return List.of();
        }

        List<GroupTagEntity> entities = newRequests.stream()
                .map(GroupTagEntityMapper::toEntity)
                .collect(Collectors.toList());

        groupTagDao.insertBatch(entities);

        List<GroupInterestResponse> result = entities.stream()
                .map(entity -> GroupTagEntityMapper.toResponse(
                        entity,
                        tagResponses.get(entity.getTagId())
                ))
                .collect(Collectors.toList());

        log.debug("Successfully batch added {} tags for group: {}", result.size(), groupId);
        return result;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        groupTagDao.delete(id);
        log.debug("Deleted group tag with id: {}", id);
    }

    @Override
    public List<GroupInterestResponse> getGroupInterests(UUID groupId) {
        log.debug("Getting interests for group: {}", groupId);

        List<GroupTagEntity> entities = groupTagDao.findByGroupId(groupId);

        if (entities.isEmpty()) {
            return List.of();
        }

        List<GroupInterestResponse> result = new ArrayList<>();

        for (GroupTagEntity entity : entities) {
            try {
                TagEntity entityTag = tagDao.findById(entity.getTagId())
                        .orElseThrow(() -> new RuntimeException("Tag not found with id: " + entity.getTagId()));

                Optional<TagStatsEntity> statsOpt = tagStatsDao.findByTagId(entity.getTagId());
                TagStatsEntity stats = statsOpt.orElse(null);

                TagResponse tagResponse = TagEntityMapper.mapToResponse(entityTag, stats);
                result.add(GroupTagEntityMapper.toResponse(entity, tagResponse));
            } catch (Exception e) {
                log.error("Failed to get tag with id: {}, skipping", entity.getTagId(), e);
            }
        }

        return result;
    }

    @Override
    public GroupInterestResponse getGroupInterest(UUID groupId, UUID tagId) {
        log.debug("Getting interest for group: {} and tag: {}", groupId, tagId);

        TagEntity entityTag = tagDao.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));

        Optional<TagStatsEntity> statsOpt = tagStatsDao.findByTagId(tagId);
        TagStatsEntity stats = statsOpt.orElse(null);

        TagResponse tagResponse = TagEntityMapper.mapToResponse(entityTag, stats);

        GroupTagEntity entity = groupTagDao.findByGroupIdAndTagId(groupId, tagId)
                .orElseThrow(() -> new RuntimeException("Interest not found"));

        return GroupTagEntityMapper.toResponse(entity, tagResponse);
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
