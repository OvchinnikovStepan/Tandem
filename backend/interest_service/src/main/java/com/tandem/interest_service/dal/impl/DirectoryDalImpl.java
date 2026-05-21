package com.tandem.interest_service.dal.impl;

import com.tandem.interest_service.dal.DirectoryDal;
import com.tandem.interest_service.dao.DirectoryDao;
import com.tandem.interest_service.dao.model.GroupDirectoryEntity;
import com.tandem.interest_service.dao.model.UserDirectoryEntity;
import com.tandem.interest_service.service.model.response.GroupSearchResponse;
import com.tandem.interest_service.service.model.response.UserSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectoryDalImpl implements DirectoryDal {

    private final DirectoryDao directoryDao;

    @Override
    public void upsertUser(UUID userId, String displayName) {
        directoryDao.upsertUser(userId, displayName);
        log.debug("Upserted user_directory row for user {}", userId);
    }

    @Override
    public void upsertGroup(UUID groupId, String name) {
        directoryDao.upsertGroup(groupId, name);
        log.debug("Upserted group_directory row for group {}", groupId);
    }

    @Override
    public List<UserSearchResponse> searchUsersByNameFragment(String fragment, int limit) {
        List<UserDirectoryEntity> rows = directoryDao.searchUsersByNameFragment(fragment, limit);
        return rows.stream()
                .map(e -> UserSearchResponse.builder()
                        .userId(e.getUserId())
                        .displayName(e.getDisplayName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupSearchResponse> searchGroupsByNameFragment(String fragment, int limit) {
        List<GroupDirectoryEntity> rows = directoryDao.searchGroupsByNameFragment(fragment, limit);
        return rows.stream()
                .map(e -> GroupSearchResponse.builder()
                        .groupId(e.getGroupId())
                        .name(e.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
