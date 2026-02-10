package com.tandem.profile_service.controller;

import com.tandem.profile_service.dto.ProfileResponse;
import com.tandem.profile_service.dto.UpdateResponse;
import com.tandem.profile_service.dto.ProfileRequest;
import com.tandem.profile_service.dto.PrivacySettingsDto;
import com.tandem.profile_service.model.Profile;
import com.tandem.profile_service.security.SecurityUtils;
import com.tandem.profile_service.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

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
        UUID currentUserId = SecurityUtils.getCurrentUserIdOrThrow();

        ProfileResponse response = profileService.getProfileWithPrivacy(currentUserId, currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/profiles/me
     * Удаляет профиль текущего пользователя
     */
    @DeleteMapping("/profile/me")
    public ResponseEntity<Void> deleteProfile() {
        UUID currentUserId = SecurityUtils.getCurrentUserIdOrThrow();

        profileService.deleteProfile(currentUserId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/profile/{userId}
     * Возвращает профиль другого пользователя, с настройками конфиденциальности
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable UUID userId) {
        UUID currentUserId = SecurityUtils.getCurrentUserIdOrThrow();

        ProfileResponse response = profileService.getProfileWithPrivacy(currentUserId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/profile/me
     * Полностью обновляет профиль текущего пользователя
     */
    @PutMapping("/profile/me")
    public ResponseEntity<UpdateResponse> updateMyProfile(
            @Valid @RequestBody ProfileRequest request) {

        UUID currentUserId = SecurityUtils.getCurrentUserIdOrThrow();

        UpdateResponse response = profileService.updateProfile(currentUserId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/profile/me
     * Частично обновляет профиль текущего пользователя, изменяя только указанные поля.
     */
    @PatchMapping("/profile/me")
    public ResponseEntity<UpdateResponse> patchMyProfile(@RequestBody ProfileRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserIdOrThrow();

        UpdateResponse response = profileService.patchProfile(currentUserId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/profile/me/privacy
     * Возвращает текущие настройки конфиденциальности пользователя.
     */
    @GetMapping("/profile/me/privacy")
    public ResponseEntity<PrivacySettingsDto> getMyPrivacySettings() {
        UUID currentUserId = SecurityUtils.getCurrentUserIdOrThrow();

        PrivacySettingsDto privacy = profileService.getPrivacySettings(currentUserId);

        return ResponseEntity.ok(privacy);
    }

    /**
     * PUT /api/profile/me/privacy
     * Обновляет настройки конфиденциальности текущего пользователя.
     */
    @PutMapping("/profile/me/privacy")
    public ResponseEntity<UpdateResponse> updateMyPrivacySettings(@RequestBody PrivacySettingsDto request) {
        UUID currentUserId = SecurityUtils.getCurrentUserIdOrThrow();

        UpdateResponse response = profileService.updatePrivacySettings(currentUserId, request);
        return ResponseEntity.ok(response);

    }
}
