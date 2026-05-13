package com.tandem.interest_service.dao.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GroupTagEntity {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private UUID groupId;
    private UUID tagId;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}