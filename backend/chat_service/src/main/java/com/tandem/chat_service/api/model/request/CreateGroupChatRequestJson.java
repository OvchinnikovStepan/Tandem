package com.tandem.chat_service.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tandem.chat_service.dao.enums.GroupVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupChatRequestJson {

    @NotBlank(message = "Group name cannot be empty")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("avatarUrl")
    private String avatarUrl;

    @NotNull()
    @JsonProperty("visibility")
    private GroupVisibility visibility;

    @NotNull()
    @JsonProperty("groupInterests")
    private List<String> groupInterests;
}
