package com.tandem.chat_service.dao.model;

import com.tandem.chat_service.dao.enums.GroupRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GroupRequestEntity {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private UUID groupId;
    private UUID userId;
    private UUID requestedBy;
    @Builder.Default
    private GroupRequestStatus status = GroupRequestStatus.PENDING;
    private String message;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt;
    private LocalDateTime reviewedAt;
    private UUID reviewedBy;
}