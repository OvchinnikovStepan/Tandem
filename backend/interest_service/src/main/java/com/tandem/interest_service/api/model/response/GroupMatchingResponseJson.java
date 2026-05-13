package com.tandem.interest_service.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class GroupMatchingResponseJson {
    @JsonProperty("id")
    private UUID groupId;

    @JsonProperty("matchingInterests")
    private List<String> matchingInterests;

    @JsonProperty("matchScore")
    private Double matchScore;
}
