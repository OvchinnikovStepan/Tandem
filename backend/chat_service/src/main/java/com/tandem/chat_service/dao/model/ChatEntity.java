package com.tandem.chat_service.dao.model;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ChatEntity {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private UUID groupId;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastMessageAt;
}