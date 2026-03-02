package com.tandem.profile_service.model;

import lombok.Setter;
import lombok.Getter;
import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
public class OnboardingResponse {

    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NonNull
    private UUID userId;

    private UUID pollId;

    private UUID questionId;

    private String answerText; // Для TEXT и TEXTAREA

    private List<String> answerArray; // Для MULTISELECT

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public Object getAnswer() {
        if (answerText != null) {
            return answerText;
        } else if (answerArray != null && !answerArray.isEmpty()) {
            return answerArray;
        }
        return null;
    }

    public void setAnswer(Object answer) {
        if (answer == null) {
            this.answerText = null;
            this.answerArray = null;
        } else if (answer instanceof String) {
            this.answerText = (String) answer;
            this.answerArray = null;
        } else if (answer instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) answer;
            this.answerArray = list;
            this.answerText = null;
        } else {
            throw new IllegalArgumentException("Answer must be String or List<String>");
        }
    }
}