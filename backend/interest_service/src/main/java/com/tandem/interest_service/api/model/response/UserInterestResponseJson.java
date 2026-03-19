package com.tandem.interest_service.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserInterestResponseJson {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("tag")
    private TagResponseJson tag;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
}