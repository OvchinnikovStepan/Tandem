package com.tandem.profile_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OnboardingEventData {
    private UUID userId;
    private UUID profileId;
    private String name;
    private String surname;
    private List<String> interests;

    public static OnboardingEventData from(
            UUID userId,
            OnboardingCompleteResponse response,
            List<String> interests) {
        return OnboardingEventData.builder()
                .userId(userId)
                .profileId(response.getProfile().getId())
                .name(response.getProfile().getName())
                .surname(response.getProfile().getSurname())
                .interests(interests)
                .build();
    }
}
