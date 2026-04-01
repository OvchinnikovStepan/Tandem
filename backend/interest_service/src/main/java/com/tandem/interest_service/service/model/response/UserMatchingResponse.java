package com.tandem.interest_service.service.model.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class UserMatchingResponse {
    private UUID userId;
    private List<String> matchingInterests;
    private Double matchScore; // Процент совпадения
}
