package com.tandem.chat_service.service.model.response;

import com.tandem.chat_service.dao.enums.GroupVisibility;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GroupDto {
    private UUID id;
    private String name;
    private String description;
    private String avatarUrl;
    private UUID creatorId;
    private GroupVisibility visibility;
}
