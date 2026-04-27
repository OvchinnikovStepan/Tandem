package com.tandem.chat_service.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tandem.chat_service.dao.enums.ParticipantRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ChatParticipantDtoJson {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("chatId")
    private UUID chatId;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("role")
    private ParticipantRole role;

    @JsonProperty("isMuted")
    private Boolean isMuted;

    @JsonProperty("isBanned")
    private Boolean isBanned;

    @JsonProperty("joinedAt")
    private LocalDateTime joinedAt;

    @JsonProperty("exitedAt")
    private LocalDateTime exitedAt;
}