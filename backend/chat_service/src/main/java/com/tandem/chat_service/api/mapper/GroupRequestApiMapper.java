package com.tandem.chat_service.api.mapper;

import com.tandem.chat_service.api.model.response.JoinGroupResponseJson;
import com.tandem.chat_service.service.model.response.GroupRequestDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GroupRequestApiMapper {

    public JoinGroupResponseJson toJson(GroupRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return JoinGroupResponseJson.builder()
                .id(dto.getId())
                .groupId(dto.getGroupId())
                .userId(dto.getUserId())
                .status(dto.getStatus())
                .message(dto.getMessage())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}