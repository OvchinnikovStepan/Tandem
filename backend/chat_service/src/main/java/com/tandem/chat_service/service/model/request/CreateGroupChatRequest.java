package com.tandem.chat_service.service.model.request;

import com.tandem.chat_service.dao.enums.GroupVisibility;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CreateGroupChatRequest {
    private String name;
    private String description;
    private String avatarUrl;
    private UUID creatorId;
    private GroupVisibility visibility;
    private List<String> groupInterests;
}
