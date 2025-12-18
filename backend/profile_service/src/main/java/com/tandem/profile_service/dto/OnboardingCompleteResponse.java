package com.tandem.profile_service.dto;

import com.tandem.profile_service.model.Profile;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OnboardingCompleteResponse {
    private Profile profile;
    private boolean onboardingCompleted;
    private UUID userId;
}
