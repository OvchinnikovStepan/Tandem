package com.tandem.profile_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tandem.profile_service.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponse {

    // Основная информация
    private UUID userId;
    private String name;
    private String surname;
    private String status;

    private String phoneNumber;
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private BioDto bio;

    private Boolean onboardingCompleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BioDto {
        private String city;
        private String placeOfWork;
        private String jobTitle;
        private String personalInterests;
    }


    /**
     * Возвращает полную информацию профиля (для просмотра своего профил)
     */
    public static ProfileResponse forOwner(Profile profile) {
        if (profile == null) {
            return null;
        }

        return ProfileResponse.builder()
                .userId(profile.getUserId())
                .name(profile.getName())
                .surname(profile.getSurname())
                .phoneNumber(profile.getPhoneNumber())
                .email(profile.getEmail())
                .status(profile.getStatus())
                .birthday(profile.getBirthday())
                .bio(buildBioDto(profile))
                .onboardingCompleted(profile.isOnboardingCompleted())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    private static BioDto buildBioDto(Profile profile) {
        return BioDto.builder()
                .city(profile.getCity())
                .placeOfWork(profile.getPlaceOfWork())
                .jobTitle(profile.getJobTitle())
                .personalInterests(profile.getPersonalInterests())
                .build();
    }

    /**
     * Для просмотра чужих профилей, зависит от настроек приватности
     */
    public static ProfileResponseBuilder builderFromProfile(Profile profile) {
        return ProfileResponse.builder()
                .userId(profile.getUserId())
                .name(profile.getName())
                .surname(profile.getSurname())
                .status(profile.getStatus());
    }
}