package com.tandem.chat_service.service.model.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ChatResponse {
    private UUID id;
    private GroupDto group;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private List<ChatParticipantDto> participants;
}
