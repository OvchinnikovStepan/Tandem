package com.tandem.chat_service.dao.model;

import com.tandem.chat_service.dao.enums.GroupVisibility;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class GroupEntity {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String name;
    private String description;
    private String avatarUrl;
    private UUID creatorId;
    @Builder.Default
    private GroupVisibility visibility = GroupVisibility.PUBLIC;
}