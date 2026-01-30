package com.tandem.profile_service.model;

import lombok.Setter;
import lombok.Getter;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
public class Poll {

    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String name;

    @Builder.Default
    private int version = 1;

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}