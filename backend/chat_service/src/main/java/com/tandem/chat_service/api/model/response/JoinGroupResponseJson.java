package com.tandem.chat_service.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tandem.chat_service.dao.enums.GroupRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class JoinGroupResponseJson {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("groupId")
    private UUID groupId;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("status")
    private GroupRequestStatus status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
}