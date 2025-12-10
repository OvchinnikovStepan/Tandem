package com.tandem.profile_service.controller;

import com.tandem.profile_service.dto.*;
import com.tandem.profile_service.model.Profile;
import com.tandem.profile_service.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Метод для извлечения userId из JWT токена
     */
    private UUID getCurrentUserId() {
        // TODO: Реализовать
        // Пока заглушка, возвращает тестовый userId
        return UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
    }

    /**
     * GET /api/profiles
     * Возвращает список всех профилей в системе.
     */
    @GetMapping("/profiles")
    public ResponseEntity<List<Profile>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getAllProfiles());
    }

    /**
     * GET /api/profile/me
     * Возвращает полную информацию о профиле текущего пользователя
     */
    @GetMapping("/profile/me")
    public ResponseEntity<ProfileResponse> getMyProfile() {

        UUID currentUserId = getCurrentUserId();

        try {
            ProfileResponse response = profileService.getProfileWithPrivacy(currentUserId, currentUserId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/profiles/me
     * Удаляет профиль текущего пользователя
     */
    @DeleteMapping("/profile/me")
    public ResponseEntity<Void> deleteProfile() {

        UUID currentUserId = getCurrentUserId();

        try {
            profileService.deleteProfile(currentUserId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/profile/{userId}
     * Возвращает профиль другого пользователя, с настройками конфиденциальности
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable UUID userId) {

        UUID currentUserId = getCurrentUserId();

        try {
            ProfileResponse response = profileService.getProfileWithPrivacy(currentUserId, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PUT /api/profile/me
     * Полностью обновляет профиль текущего пользователя
     */
    @PutMapping("/profile/me")
    public ResponseEntity<UpdateResponse> updateMyProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody ProfileRequest request) {

        UUID currentUserId = getCurrentUserId();

        try {
            Profile updatedProfile = profileService.updateProfile(currentUserId, request);
            ProfileResponse profileResponse = ProfileResponse.forOwner(updatedProfile);

            // TODO: Публикация события profile.updated
            // kafkaTemplate.send("profile.updated", updatedProfile);

            return ResponseEntity.ok(UpdateResponse.success(profileResponse));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(UpdateResponse.failure());
        }
    }

    /**
     * PATCH /api/profile/me
     * Частично обновляет профиль текущего пользователя, изменяя только указанные поля.
     */
    @PatchMapping("/profile/me")
    public ResponseEntity<UpdateResponse> patchMyProfile(@RequestBody ProfileRequest request) {

        UUID currentUserId = getCurrentUserId();

        try {
            Profile updatedProfile = profileService.patchProfile(currentUserId, request);
            ProfileResponse profileResponse = ProfileResponse.forOwner(updatedProfile);

            // TODO: Публикация события profile.updated
            // kafkaTemplate.send("profile.updated", updatedProfile);

            return ResponseEntity.ok(UpdateResponse.success(profileResponse));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(UpdateResponse.failure());
        }
    }

    /**
     * GET /api/profile/me/privacy
     * Возвращает текущие настройки конфиденциальности пользователя.
     */
    @GetMapping("/profile/me/privacy")
    public ResponseEntity<PrivacySettingsDto> getMyPrivacySettings() {

        UUID currentUserId = getCurrentUserId();
        PrivacySettingsDto privacy = profileService.getPrivacySettings(currentUserId);

        return ResponseEntity.ok(privacy);
    }

    /**
     * PUT /api/profile/me/privacy
     * Обновляет настройки конфиденциальности текущего пользователя.
     */
    @PutMapping("/profile/me/privacy")
    public ResponseEntity<UpdateResponse> updateMyPrivacySettings(@RequestBody PrivacySettingsDto request) {

        UUID currentUserId = getCurrentUserId();

        try {
            profileService.updatePrivacySettings(currentUserId, request);
            return ResponseEntity.ok(UpdateResponse.success(null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(UpdateResponse.failure());
        }
    }

    /**
     * POST /api/profile/me/complete-onboarding
     * Отмечает процесс онбординга пользователя как завершенный.
     */
    @PostMapping("/profile/me/complete-onboarding")
    public ResponseEntity<UpdateResponse> completeMyOnboarding() {

        UUID currentUserId = getCurrentUserId();

        try {
            Profile updatedProfile = profileService.completeOnboarding(currentUserId);
            ProfileResponse profileResponse = ProfileResponse.forOwner(updatedProfile);
            return ResponseEntity.ok(UpdateResponse.success(profileResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(UpdateResponse.failure());
        }
    }
}