package com.tandem.interest_service.dao.model;

import lombok.Getter;
import lombok.Builder;
import java.util.UUID;

@Getter
@Builder
public class TagEntity {
    @Builder.Default
    private UUID id = UUID.randomUUID();;
    private String name;
    @Builder.Default
    private String imageUrl = null;
}
