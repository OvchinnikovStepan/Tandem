package com.tandem.chat_service.dao.model;

import com.tandem.chat_service.dao.enums.ParticipantRole;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ChatParticipantEntity {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private UUID chatId;
    private UUID userId;
    @Builder.Default
    private ParticipantRole role = ParticipantRole.MEMBER;
    @Builder.Default
    private Boolean isMuted = false;
    @Builder.Default
    private Boolean isBanned = false;
    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();
    private LocalDateTime exitedAt;
}