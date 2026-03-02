package com.tandem.profile_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class OnboardingQuestionsResponse {
    private UUID pollId;
    private List<QuestionDto> questions;

    @Data
    @Builder
    public static class QuestionDto {
        private UUID id;
        private String type; // text | textarea | select | multiselect
        private String label;
        private boolean required;
        private List<String> options;
        private Map<String, Object> validation;
    }
}
