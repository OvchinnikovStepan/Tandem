package com.tandem.interest_service.service.model.request;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class GroupInterestRequest {
    private UUID groupId;
    private UUID tagId;
}