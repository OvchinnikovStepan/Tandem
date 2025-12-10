package com.tandem.profile_service.service;

import com.tandem.profile_service.dto.PrivacySettingsDto;
import com.tandem.profile_service.dto.ProfileRequest;
import com.tandem.profile_service.dto.ProfileResponse;
import com.tandem.profile_service.model.Profile;
import com.tandem.profile_service.repository.PrivacySettingsRepository;
import com.tandem.profile_service.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final PrivacySettingsRepository privacySettingsRepository;

    /**
     * Получить все профили (сортировка по дате создания)
     */
    @Transactional
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll("created_at DESC");
    }

    /**
     * Полное обновление профиля (PUT)
     */
    @Transactional
    public Profile updateProfile(UUID userId, ProfileRequest request) {
        Profile existingProfile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        existingProfile.setName(request.getName());
        existingProfile.setSurname(request.getSurname());
        existingProfile.setPhoneNumber(request.getPhoneNumber());
        existingProfile.setEmail(request.getEmail());
        existingProfile.setStatus(request.getStatus());
        existingProfile.setBirthday(request.getBirthday());
        existingProfile.setCity(request.getCity());
        existingProfile.setPlaceOfWork(request.getPlaceOfWork());
        existingProfile.setJobTitle(request.getJobTitle());
        existingProfile.setPersonalInterests(request.getPersonalInterests());
        existingProfile.setUpdatedAt(LocalDateTime.now());

        return profileRepository.save(existingProfile);
    }

    /**
     * Частичное обновление профиля (PATCH)
     */
    @Transactional
    public Profile patchProfile(UUID userId, ProfileRequest request) {
        Profile existingProfile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        // Автоматически копирует только ненулевые поля
        BeanUtils.copyProperties(request, existingProfile, getNullPropertyNames(request));
        existingProfile.setUpdatedAt(LocalDateTime.now());

        return profileRepository.save(existingProfile);
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }

        return emptyNames.toArray(new String[0]);
    }

    /**
     * Удаление профиля пользователя
     */
    @Transactional
    public void deleteProfile(UUID userId) {
        Profile existingProfile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        profileRepository.deleteByUserId(userId);
        privacySettingsRepository.deleteByUserId(userId);
    }

    /**
     * Получить настройки приватности пользователя
     * @throws RuntimeException если настройки не найдены (не должно происходить в нормальных условиях)
     */
    public PrivacySettingsDto getPrivacySettings(UUID userId) {
        return privacySettingsRepository.findByUserId(userId)
                .map(settings -> {
                    PrivacySettingsDto dto = new PrivacySettingsDto();
                    dto.setShowPhoneNumber(settings.isShowPhoneNumber());
                    dto.setShowEmail(settings.isShowEmail());
                    dto.setShowCity(settings.isShowCity());
                    dto.setShowPlaceOfWork(settings.isShowPlaceOfWork());
                    dto.setShowJobTitle(settings.isShowJobTitle());
                    dto.setShowBirthday(settings.isShowBirthday());
                    dto.setShowPersonalInterests(settings.isShowPersonalInterests());
                    return dto;
                })
                .orElseThrow(() -> new RuntimeException("Privacy settings not found for user: " + userId));
    }


    /**
     * Обновить настройки приватности
     */
    @Transactional
    public void updatePrivacySettings(UUID userId, PrivacySettingsDto request) {
        privacySettingsRepository.findByUserId(userId)
                .ifPresentOrElse(
                        settings -> {
                            settings.setShowPhoneNumber(request.isShowPhoneNumber());
                            settings.setShowEmail(request.isShowEmail());
                            settings.setShowCity(request.isShowCity());
                            settings.setShowPlaceOfWork(request.isShowPlaceOfWork());
                            settings.setShowJobTitle(request.isShowJobTitle());
                            settings.setShowBirthday(request.isShowBirthday());
                            settings.setShowPersonalInterests(request.isShowPersonalInterests());
                            settings.setUpdatedAt(LocalDateTime.now());
                            privacySettingsRepository.save(settings);
                        },
                        () -> {
                            // Если настроек нет - создаем новые
                            privacySettingsRepository.saveDefaultSettings(userId);
                            // И обновляем их
                            updatePrivacySettings(userId, request);
                        }
                );
    }

    /**
     * Получить профиль с учетом настроек приватности
     */
    public ProfileResponse getProfileWithPrivacy(UUID viewerId, UUID targetUserId) {
        Profile profile = profileRepository.findByUserId(targetUserId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + targetUserId));

        // Если смотрим свой профиль
        if (viewerId.equals(targetUserId)) {
            return ProfileResponse.forOwner(profile);
        }

        // Если смотрим чужой профиль
        PrivacySettingsDto privacy = getPrivacySettings(targetUserId);

        ProfileResponse.ProfileResponseBuilder builder = ProfileResponse.builderFromProfile(profile);

        // Bio с настройками приватности
        ProfileResponse.BioDto.BioDtoBuilder bioBuilder = ProfileResponse.BioDto.builder();

        if (privacy.isShowCity() && profile.getCity() != null) {
            bioBuilder.city(profile.getCity());
        }
        if (privacy.isShowPlaceOfWork() && profile.getPlaceOfWork() != null) {
            bioBuilder.placeOfWork(profile.getPlaceOfWork());
        }
        if (privacy.isShowJobTitle() && profile.getJobTitle() != null) {
            bioBuilder.jobTitle(profile.getJobTitle());
        }
        if (privacy.isShowPersonalInterests() && profile.getPersonalInterests() != null) {
            bioBuilder.personalInterests(profile.getPersonalInterests());
        }

        ProfileResponse.BioDto bio = bioBuilder.build();

        return builder
                .phoneNumber(privacy.isShowPhoneNumber() ? profile.getPhoneNumber() : null)
                .email(privacy.isShowEmail() ? profile.getEmail() : null)
                .birthday(privacy.isShowBirthday() ? profile.getBirthday() : null)
                .bio(bio.getCity() != null || bio.getPlaceOfWork() != null ||
                        bio.getJobTitle() != null || bio.getPersonalInterests() != null ? bio : null)
                .build();
    }


    /**
     * Завершить процесс онбординга
     */
    @Transactional
    public Profile completeOnboarding(UUID userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        if (!profile.isOnboardingCompleted()) {
            profile.setOnboardingCompleted(true);
            profile.setOnboardingCompletedAt(LocalDateTime.now());
            profile.setUpdatedAt(LocalDateTime.now());

            profile = profileRepository.save(profile);
        }

        return profile;
    }
}