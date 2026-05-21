package com.tandem.interest_service.service.model.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GroupInterestResponse {
    private UUID id;
    private UUID groupId;
    private TagResponse tag;
    private LocalDateTime createdAt;
}
