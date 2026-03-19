package com.tandem.interest_service.dao;

import com.tandem.interest_service.dao.model.UserInterestEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserInterestDao {
    void insertBatch(List<UserInterestEntity> entities);
    void delete(UUID id);
    List<UserInterestEntity> findByUserId(UUID userId);
    Optional<UserInterestEntity> findByUserIdAndTagId(UUID userId, UUID tagId);
    List<Map<String, Object>> findUsersWithCommonTagsCount(UUID currentUserId, int minMatchCount);
    int countUserInterests(UUID userId);
    List<UUID> findCommonTagIds(UUID userId1, UUID userId2);
}
