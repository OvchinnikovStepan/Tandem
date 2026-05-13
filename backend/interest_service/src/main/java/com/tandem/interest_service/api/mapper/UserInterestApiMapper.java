package com.tandem.interest_service.api.mapper;

import com.tandem.interest_service.api.model.request.UserInterestCreateRequestJson;
import com.tandem.interest_service.api.model.request.UserInterestDeleteRequestJson;
import com.tandem.interest_service.api.model.response.UserInterestResponseJson;
import com.tandem.interest_service.api.model.response.UserMatchingResponseJson;
import com.tandem.interest_service.service.model.request.UserInterestRequest;
import com.tandem.interest_service.service.model.response.UserInterestResponse;
import com.tandem.interest_service.service.model.response.UserMatchingResponse;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class UserInterestApiMapper {

    /**
     * Маппит запрос на создание в сервисную модель
     */
    public List<UserInterestRequest> toServiceModel(UUID userId, UserInterestCreateRequestJson request) {
        return request.getTagIds().stream()
                .map(tagId -> UserInterestRequest.builder()
                        .userId(userId)
                        .tagId(tagId)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Маппит запрос на удаление в сервисную модель
     */
    public UserInterestRequest toDeleteServiceModel(UUID userId, UserInterestDeleteRequestJson request) {
        return UserInterestRequest.builder()
                .userId(userId)
                .tagId(request.getTagId())
                .build();
    }

    /**
     * Маппит ответ сервиса в JSON для API
     */
    public UserInterestResponseJson toJson(UserInterestResponse response) {
        if (response == null) {
            return null;
        }

        return UserInterestResponseJson.builder()
                .id(response.getId())
                .tag(TagApiMapper.mapToJson(response.getTag()))
                .createdAt(response.getCreatedAt())
                .build();
    }
}