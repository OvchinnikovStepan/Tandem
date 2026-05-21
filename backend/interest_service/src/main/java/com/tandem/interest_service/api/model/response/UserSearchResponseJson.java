package com.tandem.interest_service.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserSearchResponseJson {

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("displayName")
    private String displayName;
}
