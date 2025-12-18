package com.tandem.profile_service.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OnboardingCompleteRequest {

    private List<ResponseItem> responses;

    @Data
    public static class ResponseItem {
        private UUID questionId;
        private Object answer;
    }
}
