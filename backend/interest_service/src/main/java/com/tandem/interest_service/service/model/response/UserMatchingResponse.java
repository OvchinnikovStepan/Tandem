package com.tandem.interest_service.service.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UserMatchingResponse {
    private UUID userId;
    private List<String> matchingInterests;
    private Double matchScore; // Процент совпадения
}
