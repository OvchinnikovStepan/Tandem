package com.tandem.chat_service.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ChatResponseJson {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("group")
    private GroupDtoJson group;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("lastMessageAt")
    private LocalDateTime lastMessageAt;

    @JsonProperty("participants")
    private List<ChatParticipantDtoJson> participants;
}