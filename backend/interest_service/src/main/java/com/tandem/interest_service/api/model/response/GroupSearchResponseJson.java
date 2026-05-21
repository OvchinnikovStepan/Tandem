package com.tandem.interest_service.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GroupSearchResponseJson {

    @JsonProperty("groupId")
    private UUID groupId;

    @JsonProperty("name")
    private String name;
}
