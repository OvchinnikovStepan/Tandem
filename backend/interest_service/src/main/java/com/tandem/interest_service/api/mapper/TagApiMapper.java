package com.tandem.interest_service.api.mapper;

import com.tandem.interest_service.api.model.request.TagCreateRequestJson;
import com.tandem.interest_service.api.model.response.TagResponseJson;
import com.tandem.interest_service.api.model.request.TagUpdateRequestJson;
import com.tandem.interest_service.service.model.request.TagRequest;
import com.tandem.interest_service.service.model.response.TagResponse;
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