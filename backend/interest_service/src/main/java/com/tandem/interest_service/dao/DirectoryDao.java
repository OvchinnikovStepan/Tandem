package com.tandem.interest_service.dao;

import com.tandem.interest_service.dao.model.GroupDirectoryEntity;
import com.tandem.interest_service.dao.model.UserDirectoryEntity;

import java.util.List;
import java.util.UUID;

public interface DirectoryDao {

    void upsertUser(UUID userId, String displayName);

    void upsertGroup(UUID groupId, String name);

    List<UserDirectoryEntity> searchUsersByNameFragment(String fragment, int limit);

    List<GroupDirectoryEntity> searchGroupsByNameFragment(String fragment, int limit);
}
