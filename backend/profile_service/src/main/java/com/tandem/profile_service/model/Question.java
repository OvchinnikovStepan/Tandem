package com.tandem.profile_service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Setter
@Getter
@Builder
public class Question {

    @Builder.Default
    private UUID id = UUID.randomUUID();

    private UUID pollId;

    private int questionOrder;

    @NonNull
    private QuestionType questionType;

    @NonNull
    private String label;

    private String description;

    @Builder.Default
    private boolean isRequired = false;

    private Map<String, Object> validationRules; // JSONB мап

    private List<String> options; // Для select/multiselect

    private String profileField;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum QuestionType {
        TEXT("text"),
        TEXTAREA("textarea"),
        SELECT("select"),
        MULTISELECT("multiselect");

        private final String value;

        QuestionType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static QuestionType fromString(String value) {
            for (QuestionType type : QuestionType.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown question type: " + value);
        }
    }
}