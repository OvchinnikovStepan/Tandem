package com.tandem.interest_service.api.mapper;

import com.tandem.interest_service.api.model.request.GroupInterestCreateRequestJson;
import com.tandem.interest_service.api.model.response.GroupInterestResponseJson;
import com.tandem.interest_service.service.model.request.GroupInterestRequest;
import com.tandem.interest_service.service.model.response.GroupInterestResponse;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class GroupTagApiMapper {

    public List<GroupInterestRequest> toServiceModel(UUID groupId, GroupInterestCreateRequestJson request) {
        return request.getTagIds().stream()
                .map(tagId -> GroupInterestRequest.builder()
                        .groupId(groupId)
                        .tagId(tagId)
                        .build())
                .collect(Collectors.toList());
    }

    public GroupInterestResponseJson toJson(GroupInterestResponse response) {
        if (response == null) {
            return null;
        }

        return GroupInterestResponseJson.builder()
                .id(response.getId())
                .tag(TagApiMapper.mapToJson(response.getTag()))
                .createdAt(response.getCreatedAt())
                .build();
    }
}