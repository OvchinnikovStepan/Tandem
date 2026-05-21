package com.tandem.interest_service.dal;

import com.tandem.interest_service.service.model.response.GroupSearchResponse;
import com.tandem.interest_service.service.model.response.UserSearchResponse;

import java.util.List;
import java.util.UUID;

public interface DirectoryDal {

    void upsertUser(UUID userId, String displayName);

    void upsertGroup(UUID groupId, String name);

    List<UserSearchResponse> searchUsersByNameFragment(String fragment, int limit);

    List<GroupSearchResponse> searchGroupsByNameFragment(String fragment, int limit);
}
