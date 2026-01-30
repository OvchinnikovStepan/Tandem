package com.tandem.profile_service.model;

import lombok.Setter;
import lombok.Getter;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
public class Profile {
    private UUID id;
    private UUID userId;
    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private String status;
    private LocalDate birthday;
    private String city;
    private String placeOfWork;
    private String jobTitle;
    private String personalInterests;

    @Builder.Default
    private boolean onboardingCompleted = false;

    private LocalDateTime onboardingCompletedAt;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
