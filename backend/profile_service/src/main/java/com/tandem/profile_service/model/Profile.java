package com.tandem.profile_service.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public Profile(UUID userId, String name, String surname) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
