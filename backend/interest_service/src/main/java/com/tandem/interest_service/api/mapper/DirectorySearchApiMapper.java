package com.tandem.interest_service.api.mapper;

import com.tandem.interest_service.api.model.response.GroupSearchResponseJson;
import com.tandem.interest_service.api.model.response.UserSearchResponseJson;
import com.tandem.interest_service.service.model.response.GroupSearchResponse;
import com.tandem.interest_service.service.model.response.UserSearchResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DirectorySearchApiMapper {

    public UserSearchResponseJson toUserSearchJson(UserSearchResponse response) {
        if (response == null) {
            return null;
        }

        return UserSearchResponseJson.builder()
                .userId(response.getUserId())
                .displayName(response.getDisplayName())
                .build();
    }

    public GroupSearchResponseJson toGroupSearchJson(GroupSearchResponse response) {
        if (response == null) {
            return null;
        }

        return GroupSearchResponseJson.builder()
                .groupId(response.getGroupId())
                .name(response.getName())
                .build();
    }
}
