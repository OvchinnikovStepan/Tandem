package com.tandem.interest_service.api.mapper;

import com.tandem.interest_service.api.model.TagCreateRequestJson;
import com.tandem.interest_service.api.model.TagResponseJson;
import com.tandem.interest_service.api.model.TagUpdateRequestJson;
import com.tandem.interest_service.service.model.TagRequest;
import com.tandem.interest_service.service.model.TagResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TagApiMapper {

    public String mapToServiceModel(TagCreateRequestJson request) {
        return request.getName();
    }

    public TagRequest mapToTagRequest(TagUpdateRequestJson request) {
        return TagRequest.builder()
                .name(request.getName())
                .imageUrl(request.getImageUrl())
                .build();
    }

    public TagResponseJson mapToJson(TagResponse response) {
        if (response == null) {
            return null;
        }

        return TagResponseJson.builder()
                .id(response.getId())
                .name(response.getName())
                .imageUrl(response.getImageUrl())
                .usageCount(response.getUsageCount())
                .build();
    }
}