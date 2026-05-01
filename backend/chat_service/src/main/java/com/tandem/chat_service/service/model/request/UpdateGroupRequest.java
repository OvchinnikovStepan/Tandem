package com.tandem.chat_service.service.model.request;

import com.tandem.chat_service.dao.enums.GroupVisibility;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateGroupRequest {
    private String name;
    private String description;
    private String avatarUrl;
    private GroupVisibility visibility;
}
