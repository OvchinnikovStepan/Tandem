package com.tandem.interest_service.api.mapper;

import com.tandem.interest_service.api.model.response.GroupMatchingResponseJson;
import com.tandem.interest_service.api.model.response.UserMatchingResponseJson;
import com.tandem.interest_service.service.model.response.GroupMatchingResponse;
import com.tandem.interest_service.service.model.response.UserMatchingResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MatchingApiMapper {

    public UserMatchingResponseJson toUserMatchingResponseJson(UserMatchingResponse response) {
        if (response == null) {
            return null;
        }

        return UserMatchingResponseJson.builder()
                .userId(response.getUserId())
                .matchingInterests(response.getMatchingInterests())
                .matchScore(response.getMatchScore())
                .build();
    }

    public GroupMatchingResponseJson toGroupMatchingResponseJson(GroupMatchingResponse response) {
        if (response == null) {
            return null;
        }

        return GroupMatchingResponseJson.builder()
                .groupId(response.getGroupId())
                .matchingInterests(response.getMatchingInterests())
                .matchScore(response.getMatchScore())
                .build();
    }
}
