package com.tandem.interest_service.service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class TagResponse {
    private UUID id;
    private String name;
    private String imageUrl;
    @Builder.Default
    private Integer usageCount = 0;
}

