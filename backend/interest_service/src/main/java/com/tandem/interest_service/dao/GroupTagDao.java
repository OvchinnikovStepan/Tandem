package com.tandem.interest_service.dao;

import com.tandem.interest_service.dao.model.GroupTagEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface GroupTagDao {
    void insertBatch(List<GroupTagEntity> entities);
    void delete(UUID id);
    List<GroupTagEntity> findByGroupId(UUID groupId);
    Optional<GroupTagEntity> findByGroupIdAndTagId(UUID groupId, UUID tagId);
    int countGroupTags(UUID groupId);
    List<Map<String, Object>> findGroupsWithCommonTagsWithUserCount(UUID userId, int minMatchCount);
    List<UUID> findCommonTagIdsBetweenUserAndGroup(UUID userId, UUID groupId);
}