package com.tandem.interest_service.dao.model;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserInterestEntity {
    @Builder.Default
    private UUID id = UUID.randomUUID();;
    private UUID userId;
    private UUID tagId;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();;
}
