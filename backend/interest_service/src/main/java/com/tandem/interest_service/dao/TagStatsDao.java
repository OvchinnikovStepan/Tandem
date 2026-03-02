package com.tandem.interest_service.dao;

import com.tandem.interest_service.dao.model.TagStatsEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagStatsDao {
    Optional<TagStatsEntity> findByTagId(UUID tagId);
    Optional<Integer> findUsageCountByTagId(UUID tagId);
    List<TagStatsEntity> findTopTags(int limit);
    void refreshMaterializedView();
}