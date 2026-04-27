package com.tandem.chat_service.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tandem.chat_service.dao.enums.GroupVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateGroupRequestJson {
    @NotBlank(message = "Name cannot be empty")
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("avatarUrl")
    private String avatarUrl;

    @NotNull(message = "Visibility must be specified")
    @JsonProperty("visibility")
    private GroupVisibility visibility;
}
