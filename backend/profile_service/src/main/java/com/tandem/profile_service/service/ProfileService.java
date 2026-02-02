package com.tandem.profile_service.service;

import com.tandem.profile_service.dto.ProfileResponse;
import com.tandem.profile_service.dto.UpdateResponse;
import com.tandem.profile_service.dto.ProfileRequest;
import com.tandem.profile_service.dto.PrivacySettingsDto;
import com.tandem.profile_service.dto.ProfileEventDto;
import com.tandem.profile_service.exception.DataPersistenceException;
import com.tandem.profile_service.exception.ProfileNotFoundException;
import com.tandem.profile_service.exception.ResourceNotFoundException;
import com.tandem.profile_service.kafka.ProfileEventPublisher;
import com.tandem.profile_service.model.PrivacySettings;
import com.tandem.profile_service.model.Profile;
import com.tandem.profile_service.repository.PrivacySettingsRepository;
import com.tandem.profile_service.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final PrivacySettingsRepository privacySettingsRepository;
    private final ProfileEventPublisher profileEventPublisher;

    /**
     * Получить все профили (сортировка по дате создания)
     */
    @Transactional
    public List<Profile> getAllProfiles() {
        log.info("All profiles have been extracted");
        return profileRepository.findAll("created_at DESC");
    }

    /**
     * Полное обновление профиля (PUT)
     */
    @Transactional
    public UpdateResponse updateProfile(UUID userId, ProfileRequest request) {
        Profile existingProfile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        try {
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

            Profile updatedProfile = profileRepository.save(existingProfile);

            publishProfileUpdatedEvent(userId, updatedProfile, request);

            log.info("Profile successfully updated for userId={}", userId);

            ProfileResponse profileResponse = ProfileResponse.forOwner(updatedProfile);

            return UpdateResponse.success(profileResponse);
        } catch (Exception e) {
            log.error("Failed to update profile for userId={}", userId, e);
            throw new DataPersistenceException("Failed to update profile", e);
        }
    }

    /**
     * Частичное обновление профиля (PATCH)
     */
    @Transactional
    public UpdateResponse patchProfile(UUID userId, ProfileRequest request) {
        Profile existingProfile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        try {
            // Автоматически копирует только ненулевые поля
            BeanUtils.copyProperties(request, existingProfile, getNullPropertyNames(request));
            existingProfile.setUpdatedAt(LocalDateTime.now());

            Profile updatedProfile = profileRepository.save(existingProfile);

            publishProfileUpdatedEvent(userId, updatedProfile, request);

            log.info("Profile successfully patched for userId={}", userId);

            ProfileResponse profileResponse = ProfileResponse.forOwner(updatedProfile);

            return UpdateResponse.success(profileResponse);
        } catch (Exception e) {
            log.error("Failed to patch profile for userId={}", userId, e);
            throw new DataPersistenceException("Failed to patch profile", e);
        }
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
                .orElseThrow(() -> new ProfileNotFoundException(userId));
        try {
            profileRepository.deleteByUserId(userId);
            privacySettingsRepository.deleteByUserId(userId);
            log.info("Profile and privacy settings successfully deleted for userId={}", userId);
        } catch (Exception e) {
            log.error("Failed to delete profile for userId={}", userId, e);
            throw new DataPersistenceException("Failed to delete profile", e);
        }
    }

    /**
     * Получить настройки приватности пользователя
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
                .orElseThrow(() -> new ResourceNotFoundException("Privacy settings not found for user: " + userId));
    }

    /**
     * Обновить настройки приватности
     */
    @Transactional
    public UpdateResponse updatePrivacySettings(UUID userId, PrivacySettingsDto request) {
        PrivacySettings settings = privacySettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Privacy settings not found for userId= " + userId));

        try {
            settings.setShowPhoneNumber(request.isShowPhoneNumber());
            settings.setShowEmail(request.isShowEmail());
            settings.setShowCity(request.isShowCity());
            settings.setShowPlaceOfWork(request.isShowPlaceOfWork());
            settings.setShowJobTitle(request.isShowJobTitle());
            settings.setShowBirthday(request.isShowBirthday());
            settings.setShowPersonalInterests(request.isShowPersonalInterests());
            settings.setUpdatedAt(LocalDateTime.now());

            privacySettingsRepository.save(settings);

            log.info("Privacy settings successfully updated for userId={}", userId);

            return UpdateResponse.success(null);
        } catch (Exception e) {
            log.error("Failed to update privacy settings for userId={}", userId, e);
            throw new DataPersistenceException("Failed to update privacy settings", e);
        }
    }

    /**
     * Получить профиль с учетом настроек приватности
     */
    @Transactional
    public ProfileResponse getProfileWithPrivacy(UUID viewerId, UUID targetUserId) {
        Profile profile = profileRepository.findByUserId(targetUserId)
                .orElseThrow(() -> new ProfileNotFoundException(targetUserId));

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

        log.info("Profile response built for viewerId={} viewing targetUserId={}", viewerId, targetUserId);
        return builder
                .phoneNumber(privacy.isShowPhoneNumber() ? profile.getPhoneNumber() : null)
                .email(privacy.isShowEmail() ? profile.getEmail() : null)
                .birthday(privacy.isShowBirthday() ? profile.getBirthday() : null)
                .bio(bio.getCity() != null || bio.getPlaceOfWork() != null ||
                        bio.getJobTitle() != null || bio.getPersonalInterests() != null ? bio : null)
                .build();
    }

    /**
     Создает новый профиль
     */
    @Transactional
    public Profile createProfileFromRegistrationEvent(ProfileEventDto event) {
        try {
            UUID userId = UUID.fromString(event.getUserId());

            if (profileRepository.findByUserId(userId).isPresent()) {
                return profileRepository.findByUserId(userId).get();
            }

            String phoneNumber = null;
            String email = null;

            if (event.getMetadata() != null) {
                phoneNumber = (String) event.getMetadata().get("phoneNumber");
                email = (String) event.getMetadata().get("email");
            }

            Profile createdProfile = profileRepository.createNewUserProfile(userId, phoneNumber, email);

            privacySettingsRepository.createDefaultSettings(userId);

            log.info("Profile successfully created from registration event for userId={}", userId);
            return createdProfile;

        } catch (Exception e) {
            log.error("Failed to create profile from registration event: {}", event, e);
            throw new DataPersistenceException("Failed to create profile from registration event", e);
        }
    }

    public List<String> getChangedFields(ProfileRequest request) {
        List<String> changedFields = new ArrayList<>();

        if (request.getName() != null) {
            changedFields.add("name");
        }
        if (request.getSurname() != null) {
            changedFields.add("surname");
        }
        if (request.getPhoneNumber() != null) {
            changedFields.add("phoneNumber");
        }
        if (request.getEmail() != null) {
            changedFields.add("email");
        }
        if (request.getStatus() != null) {
            changedFields.add("status");
        }
        if (request.getBirthday() != null) {
            changedFields.add("birthday");
        }
        if (request.getCity() != null) {
            changedFields.add("city");
        }
        if (request.getPlaceOfWork() != null) {
            changedFields.add("placeOfWork");
        }
        if (request.getJobTitle() != null) {
            changedFields.add("jobTitle");
        }
        if (request.getPersonalInterests() != null) {
            changedFields.add("personalInterests");
        }

        log.info("Found {} changed fields: {}", changedFields.size(), changedFields);
        return changedFields;
    }

    /**
     * Публикация события в kafka об обновлении профиля
     */
    private void publishProfileUpdatedEvent(UUID userId, Profile updatedProfile, ProfileRequest request) {
        try {
            List<String> changedFields = getChangedFields(request);
            if (!changedFields.isEmpty()) {
                profileEventPublisher.publishProfileUpdated(
                        userId,
                        updatedProfile.getId(),
                        changedFields
                );
                log.info("Published profile updated event for userId={}, changedFields={}",
                        userId, changedFields);
            }
        } catch (Exception e) {
            log.error("Failed to publish profile updated event for userId={}", userId, e);
        }
    }
}
