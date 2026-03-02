package com.tandem.interest_service.dao.model;

import lombok.Getter;
import lombok.Builder;
import java.util.UUID;

@Getter
@Builder
public class TagStatsEntity {
    private UUID tagId;
    private String tagName;
    private Integer usageCount;
}
