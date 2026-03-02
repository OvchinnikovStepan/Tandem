package com.tandem.profile_service.model;

import lombok.Setter;
import lombok.Getter;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
public class PrivacySettings {
    private UUID id;
    private UUID userId;

    @Builder.Default
    private boolean showPhoneNumber = false;

    @Builder.Default
    private boolean showEmail = false;

    @Builder.Default
    private boolean showCity = true;

    @Builder.Default
    private boolean showPlaceOfWork = true;

    @Builder.Default
    private boolean showJobTitle = true;

    @Builder.Default
    private boolean showBirthday = false;

    @Builder.Default
    private boolean showPersonalInterests = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}