package com.tandem.interest_service.dal;

import com.tandem.interest_service.service.model.response.TagResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MatchingDal {
    Map<UUID, Integer> getUsersWithCommonTagsCount(UUID currentUserId, int minMatchCount);
    int getCountUserInterests(UUID userId);
    List<UUID> getCommonTagIds(UUID userId1, UUID userId2);
    TagResponse findTagById(UUID tagId);
    Map<UUID, Integer> getGroupsWithCommonTagsCount(UUID userId, int minMatchCount);
    int getCountGroupTags(UUID groupId);
    List<UUID> getCommonTagIdsUserGroup(UUID userId, UUID groupId);
}
