package com.tandem.chat_service.dao;

import com.tandem.chat_service.dao.enums.GroupVisibility;
import com.tandem.chat_service.dao.model.GroupEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupDao {

    void insert(GroupEntity entity);
    void update(GroupEntity entity);
    void delete(UUID id);
    Optional<GroupEntity> findById(UUID id);
    List<GroupEntity> findByCreatorId(UUID creatorId);
    List<GroupEntity> findByVisibility(GroupVisibility visibility);
    Optional<GroupEntity> findByName(String name);
    List<GroupEntity> searchByNamePrefix(String prefix, int limit);
}