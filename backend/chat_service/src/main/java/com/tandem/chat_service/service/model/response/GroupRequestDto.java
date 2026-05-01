package com.tandem.chat_service.service.model.response;

import com.tandem.chat_service.dao.enums.GroupRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GroupRequestDto {
    private UUID id;
    private UUID groupId;
    private UUID userId;
    private GroupRequestStatus status;
    private String message;
    private LocalDateTime createdAt;
}
