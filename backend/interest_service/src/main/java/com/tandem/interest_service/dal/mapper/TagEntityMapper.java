package com.tandem.interest_service.dal.mapper;

import com.tandem.interest_service.dao.model.TagEntity;
import com.tandem.interest_service.dao.model.TagStatsEntity;
import com.tandem.interest_service.service.model.TagRequest;
import com.tandem.interest_service.service.model.TagResponse;
import io.micrometer.common.lang.Nullable;
import lombok.experimental.UtilityClass;
import com.tandem.interest_service.service.model.TagResponse.TagResponseBuilder;

import java.util.UUID;

@UtilityClass
public class TagEntityMapper {

    public TagEntity mapToEntity(TagRequest tagRequest) {
        return TagEntity.builder()
                .name(tagRequest.getName())
                .imageUrl(tagRequest.getImageUrl())
                .build();
    }

    public TagEntity mapToEntityWithId(UUID id, TagRequest tagRequest) {
        return TagEntity.builder()
                .id(id)
                .name(tagRequest.getName())
                .imageUrl(tagRequest.getImageUrl())
                .build();
    }

    public TagResponse mapToResponse(TagEntity entity, @Nullable TagStatsEntity statsEntity) {
        TagResponseBuilder builder = TagResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .imageUrl(entity.getImageUrl());

        if (statsEntity != null) {
            builder.usageCount(statsEntity.getUsageCount());
        }

        return builder.build();
    }
}