package com.tandem.profile_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileEventDto {

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("profileId")
    private String profileId;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    @JsonProperty("data")
    private Map<String, Object> data;
}
