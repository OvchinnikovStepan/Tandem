package com.tandem.chat_service.dao;

import com.tandem.chat_service.dao.enums.GroupRequestStatus;
import com.tandem.chat_service.dao.model.GroupRequestEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRequestDao {
    void insert(GroupRequestEntity entity);
    void updateStatus(UUID id, GroupRequestStatus status, LocalDateTime reviewedAt, UUID reviewedBy);
    Optional<GroupRequestEntity> findById(UUID id);
    List<GroupRequestEntity> findPendingByGroupId(UUID groupId);
    List<GroupRequestEntity> findByUserId(UUID userId);
}
