package com.tandem.chat_service.service.model.response;

import com.tandem.chat_service.dao.enums.ParticipantRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ChatParticipantDto {
    private UUID id;
    private UUID chatId;
    private UUID userId;
    private ParticipantRole role;
    private Boolean isMuted;
    private Boolean isBanned;
    private LocalDateTime joinedAt;
    private LocalDateTime exitedAt;
}
