package com.tandem.interest_service.dal.mapper;

import com.tandem.interest_service.dao.model.GroupTagEntity;
import com.tandem.interest_service.service.model.request.GroupInterestRequest;
import com.tandem.interest_service.service.model.response.GroupInterestResponse;
import com.tandem.interest_service.service.model.response.TagResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GroupTagEntityMapper {

    public GroupTagEntity toEntity(GroupInterestRequest request) {
        return GroupTagEntity.builder()
                .groupId(request.getGroupId())
                .tagId(request.getTagId())
                .build();
    }

    public GroupInterestResponse toResponse(GroupTagEntity entity, TagResponse tagResponse) {
        return GroupInterestResponse.builder()
                .id(entity.getId())
                .groupId(entity.getGroupId())
                .tag(tagResponse)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}