package com.tandem.interest_service.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tandem.interest_service.service.model.response.TagResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Builder
public class GroupInterestResponseJson {
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("tag")
    private TagResponseJson tag;
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

}