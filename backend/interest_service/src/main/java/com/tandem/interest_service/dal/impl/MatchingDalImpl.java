package com.tandem.interest_service.dal.impl;

import com.tandem.interest_service.dal.MatchingDal;
import com.tandem.interest_service.dal.mapper.TagEntityMapper;
import com.tandem.interest_service.dao.GroupTagDao;
import com.tandem.interest_service.dao.TagDao;
import com.tandem.interest_service.dao.TagStatsDao;
import com.tandem.interest_service.dao.UserInterestDao;
import com.tandem.interest_service.dao.model.TagEntity;
import com.tandem.interest_service.dao.model.TagStatsEntity;
import com.tandem.interest_service.service.model.response.TagResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MatchingDalImpl implements MatchingDal {
    private final UserInterestDao userInterestDao;
    private final GroupTagDao groupTagDao;
    private final TagDao tagDao;
    private final TagStatsDao tagStatsDao;

    @Override
    public Map<UUID, Integer> getUsersWithCommonTagsCount(UUID currentUserId, int minMatchCount) {
        log.debug("Getting matching with users for user: {} ", currentUserId);

        List<Map<String, Object>> rows =
                userInterestDao.findUsersWithCommonTagsCount(currentUserId, minMatchCount);

        Map<UUID, Integer> result = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Object userIdObj = row.get("user_id");
            UUID userId = (UUID) userIdObj;
            Integer commonCount = ((Number) row.get("common_count")).intValue();
            result.put(userId, commonCount);
        }
        return result;
    }

    @Override
    public int getCountUserInterests(UUID userId) {
        log.debug("Getting interests count for user: {}", userId);
        try {
            int count = userInterestDao.countUserInterests(userId);
            log.debug("User {} has {} interests", userId, count);
            return count;
        } catch (Exception e) {
            log.error("Failed to get interests count for user: {}", userId, e);
            return 0;
        }
    }

    @Override
    public List<UUID> getCommonTagIds(UUID userId1, UUID userId2) {
        log.debug("Getting common tag IDs between user: {} and user: {}", userId1, userId2);

        try {
            List<UUID> commonTagIds = userInterestDao.findCommonTagIds(userId1, userId2);
            log.debug("Found {} common tags between user: {} and user: {}",
                    commonTagIds.size(), userId1, userId2);
            return commonTagIds;
        } catch (Exception e) {
            log.error("Failed to get common tags between user: {} and user: {}",
                    userId1, userId2, e);
            return List.of();
        }
    }

    @Override
    public TagResponse findTagById(UUID tagId) {
        log.debug("Finding tag by id: {}", tagId);

        TagEntity entity = tagDao.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));

        TagStatsEntity stats = tagStatsDao.findByTagId(tagId)
                .orElse(null);

        return TagEntityMapper.mapToResponse(entity, stats);
    }

    @Override
    public Map<UUID, Integer> getGroupsWithCommonTagsCount(UUID userId, int minMatchCount) {
        log.debug("Getting matching groups for user: {}", userId);

        List<Map<String, Object>> rows = groupTagDao.findGroupsWithCommonTagsWithUserCount(userId, minMatchCount);

        Map<UUID, Integer> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            UUID groupId = (UUID) row.get("group_id");
            Integer commonCount = ((Number) row.get("common_count")).intValue();
            result.put(groupId, commonCount);
        }
        return result;
    }

    @Override
    public int getCountGroupTags(UUID groupId) {
        log.debug("Getting tag count for group: {}", groupId);
        return groupTagDao.countGroupTags(groupId);
    }

    @Override
    public List<UUID> getCommonTagIdsUserGroup(UUID userId, UUID groupId) {
        log.debug("Getting common tag IDs between user: {} and group: {}", userId, groupId);

        return groupTagDao.findCommonTagIdsBetweenUserAndGroup(userId, groupId);
    }
}
