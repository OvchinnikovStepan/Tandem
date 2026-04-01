package com.tandem.interest_service.service.model.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class TagResponse {
    private UUID id;
    private String name;
    private String imageUrl;
    @Builder.Default
    private Integer usageCount = 0;
}

