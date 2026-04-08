package com.tandem.interest_service.dal.mapper;

import com.tandem.interest_service.dao.model.UserInterestEntity;
import com.tandem.interest_service.service.model.request.UserInterestRequest;
import com.tandem.interest_service.service.model.response.UserInterestResponse;
import com.tandem.interest_service.service.model.response.TagResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserInterestEntityMapper {

    public UserInterestEntity toEntity(UserInterestRequest request) {
        return UserInterestEntity.builder()
                .userId(request.getUserId())
                .tagId(request.getTagId())
                .build();
    }

    public UserInterestResponse toResponse(UserInterestEntity entity, TagResponse tagResponse) {
        return UserInterestResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .tag(tagResponse)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
