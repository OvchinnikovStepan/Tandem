package com.tandem.interest_service.service.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UserInterestRequest {
    private UUID userId;
    private UUID tagId;
}