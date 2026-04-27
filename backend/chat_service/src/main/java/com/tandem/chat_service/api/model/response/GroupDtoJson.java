package com.tandem.chat_service.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tandem.chat_service.dao.enums.GroupVisibility;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GroupDtoJson {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("avatarUrl")
    private String avatarUrl;

    @JsonProperty("creatorId")
    private UUID creatorId;

    @JsonProperty("visibility")
    private GroupVisibility visibility;
}