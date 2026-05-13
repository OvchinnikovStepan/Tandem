package com.tandem.interest_service.integration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreatedEvent {

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("groupName")
    private String groupName;

    @JsonProperty("creatorId")
    private String creatorId;

    @JsonProperty("interestTags")
    private String interestTags;

    @JsonProperty("visibility")
    private String visibility;

    @JsonProperty("timestamp")
    private String timestamp;

    public UUID getGroupIdAsUUID() {
        return UUID.fromString(groupId);
    }

    public List<String> getInterests() {
        if (interestTags == null || interestTags.length() <= 2) {
            return List.of();
        }

        String cleaned = interestTags.substring(1, interestTags.length() - 1);
        return Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
