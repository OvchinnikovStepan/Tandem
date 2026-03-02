package com.tandem.profile_service;

import com.tandem.profile_service.dto.ProfileResponse;
import com.tandem.profile_service.dto.UpdateResponse;
import com.tandem.profile_service.dto.ProfileRequest;
import com.tandem.profile_service.dto.PrivacySettingsDto;
import com.tandem.profile_service.exception.DataPersistenceException;
import com.tandem.profile_service.exception.ProfileNotFoundException;
import com.tandem.profile_service.exception.ResourceNotFoundException;
import com.tandem.profile_service.kafka.ProfileEventPublisher;
import com.tandem.profile_service.model.PrivacySettings;
import com.tandem.profile_service.model.Profile;
import com.tandem.profile_service.repository.PrivacySettingsRepository;
import com.tandem.profile_service.repository.ProfileRepository;
import com.tandem.profile_service.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private PrivacySettingsRepository privacySettingsRepository;

    @Mock
    private ProfileEventPublisher profileEventPublisher;

    private ProfileService profileService;

    // Тестовые данные
    private UUID userId1;
    private UUID userId2;
    private UUID profileId1;
    private UUID profileId2;
    private Profile testProfile1;
    private Profile testProfile2;
    private PrivacySettings privacySettings1;
    private PrivacySettings privacySettings2;

    @BeforeEach
    void setUp() {
        profileService = new ProfileService(profileRepository, privacySettingsRepository, profileEventPublisher);
        initializeTestData();
    }

    private void initializeTestData() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        profileId1 = UUID.randomUUID();
        profileId2 = UUID.randomUUID();

        testProfile1 = Profile.builder()
                .id(profileId1)
                .userId(userId1)
                .name("John")
                .surname("Doe")
                .phoneNumber("89991234567")
                .email("john@example.com")
                .status("online")
                .birthday(LocalDate.of(1990, 1, 15))
                .city("Omsk")
                .placeOfWork("NanoTech Corp")
                .jobTitle("Senior Researcher")
                .personalInterests("Nanotechnology, Materials Science")
                .onboardingCompleted(true)
                .onboardingCompletedAt(LocalDateTime.now().minusDays(10))
                .createdAt(LocalDateTime.now().minusDays(30))
                .updatedAt(LocalDateTime.now().minusDays(5))
                .build();

        testProfile2 = Profile.builder()
                .id(profileId2)
                .userId(userId2)
                .name("Jane")
                .surname("Smith")
                .phoneNumber("89995555555")
                .email("jane@example.com")
                .status("away")
                .birthday(LocalDate.of(1995, 6, 20))
                .city("Moscow")
                .placeOfWork("Quantum Labs")
                .jobTitle("Junior Engineer")
                .personalInterests("Quantum Computing")
                .onboardingCompleted(false)
                .createdAt(LocalDateTime.now().minusDays(7))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        privacySettings1 = PrivacySettings.builder()
                .userId(userId1)
                .showPhoneNumber(true)
                .showEmail(true)
                .showCity(true)
                .showPlaceOfWork(true)
                .showJobTitle(true)
                .showBirthday(false)
                .showPersonalInterests(true)
                .createdAt(LocalDateTime.now().minusDays(30))
                .updatedAt(LocalDateTime.now().minusDays(5))
                .build();

        privacySettings2 = PrivacySettings.builder()
                .userId(userId2)
                .showPhoneNumber(false)
                .showEmail(false)
                .showCity(true)
                .showPlaceOfWork(false)
                .showJobTitle(false)
                .showBirthday(false)
                .showPersonalInterests(false)
                .createdAt(LocalDateTime.now().minusDays(7))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    // getAllProfiles
    @Test
    void getAllProfiles_Success() {
        List<Profile> profiles = Arrays.asList(testProfile2, testProfile1);

        when(profileRepository.findAll("created_at DESC")).thenReturn(profiles);

        List<Profile> result = profileService.getAllProfiles();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Jane");
        assertThat(result.get(1).getName()).isEqualTo("John");
        verify(profileRepository).findAll("created_at DESC");
    }

    // updateProfile
    @Test
    void updateProfile_Success() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileRequest request = new ProfileRequest();
        request.setName("John");
        request.setSurname("Doe");
        request.setPhoneNumber("123456789");
        request.setEmail("john.doe@example.com");
        request.setStatus("online");
        request.setBirthday(LocalDate.of(1990, 1, 1));
        request.setCity("Omsk");
        request.setPlaceOfWork("TechCorp");
        request.setJobTitle("Engineer");
        request.setPersonalInterests("Nanotechnology");

        UpdateResponse result = profileService.updateProfile(userId1, request);

        assertThat(result.isUpdated()).isTrue();
        assertThat(result.getProfile()).isNotNull();
        assertThat(result.getProfile().getName()).isEqualTo("John");
        assertThat(result.getProfile().getSurname()).isEqualTo("Doe");
        verify(profileRepository).save(testProfile1);
        verify(profileEventPublisher).publishProfileUpdated(any(), any(), any());
    }

    @Test
    void updateProfile_ProfileNotFound() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.empty());

        ProfileRequest request = new ProfileRequest();

        ProfileNotFoundException exception = assertThrows(ProfileNotFoundException.class,
                () -> profileService.updateProfile(userId1, request));

        assertThat(exception.getMessage()).contains("Profile not found for user:");
        verify(profileRepository, never()).save(any());
    }

    // patchProfile
    @Test
    void patchProfile_Success() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileRequest request = new ProfileRequest();
        request.setName("NewName");
        request.setSurname("NewSurname");

        UpdateResponse result = profileService.patchProfile(userId1, request);

        assertThat(result.isUpdated()).isTrue();
        assertThat(result.getProfile()).isNotNull();
        assertThat(result.getProfile().getName()).isEqualTo("NewName");
        assertThat(result.getProfile().getSurname()).isEqualTo("NewSurname");
        verify(profileRepository).save(testProfile1);
        verify(profileEventPublisher).publishProfileUpdated(any(), any(), any());
    }

    @Test
    void patchProfile_ProfileNotFound() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.empty());

        ProfileRequest request = new ProfileRequest();

        ProfileNotFoundException exception = assertThrows(ProfileNotFoundException.class,
                () -> profileService.patchProfile(userId1, request));

        assertThat(exception.getMessage()).contains("Profile not found for user:");
        verify(profileRepository, never()).save(any());
    }

    // deleteProfile
    @Test
    void deleteProfile_Success() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));

        profileService.deleteProfile(userId1);

        verify(profileRepository).deleteByUserId(userId1);
        verify(privacySettingsRepository).deleteByUserId(userId1);
    }

    @Test
    void deleteProfile_ProfileNotFound() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.empty());

        ProfileNotFoundException exception = assertThrows(ProfileNotFoundException.class,
                () -> profileService.deleteProfile(userId1));

        assertThat(exception.getMessage()).contains("Profile not found for user:");
        verify(profileRepository, never()).deleteByUserId(any());
        verify(privacySettingsRepository, never()).deleteByUserId(any());
    }

    // getPrivacySettings
    @Test
    void getPrivacySettings_Success() {
        when(privacySettingsRepository.findByUserId(userId1)).thenReturn(Optional.of(privacySettings1));

        PrivacySettingsDto dto = profileService.getPrivacySettings(userId1);

        assertThat(dto).isNotNull();
        assertThat(dto.isShowPhoneNumber()).isTrue();
        assertThat(dto.isShowEmail()).isTrue();
        assertThat(dto.isShowCity()).isTrue();
        assertThat(dto.isShowPlaceOfWork()).isTrue();
        assertThat(dto.isShowJobTitle()).isTrue();
        assertThat(dto.isShowBirthday()).isFalse();
        assertThat(dto.isShowPersonalInterests()).isTrue();
    }

    @Test
    void getPrivacySettings_NotFound() {
        when(privacySettingsRepository.findByUserId(userId1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> profileService.getPrivacySettings(userId1));

        assertThat(exception.getMessage()).contains("Privacy settings not found");
        verify(privacySettingsRepository).findByUserId(userId1);
    }

    // updatePrivacySettings
    @Test
    void updatePrivacySettings_Success() {
        when(privacySettingsRepository.findByUserId(userId1)).thenReturn(Optional.of(privacySettings1));
        when(privacySettingsRepository.save(any(PrivacySettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PrivacySettingsDto dto = new PrivacySettingsDto();
        dto.setShowPhoneNumber(false);
        dto.setShowEmail(false);
        dto.setShowCity(true);
        dto.setShowPlaceOfWork(false);
        dto.setShowJobTitle(true);
        dto.setShowBirthday(true);
        dto.setShowPersonalInterests(false);

        UpdateResponse result = profileService.updatePrivacySettings(userId1, dto);

        assertThat(result.isUpdated()).isTrue();
        assertThat(result.getProfile()).isNull();
        verify(privacySettingsRepository).save(privacySettings1);
        assertThat(privacySettings1.isShowPhoneNumber()).isFalse();
        assertThat(privacySettings1.isShowEmail()).isFalse();
        assertThat(privacySettings1.isShowBirthday()).isTrue();
        assertThat(privacySettings1.getUpdatedAt()).isNotNull();
    }

    @Test
    void updatePrivacySettings_NotFound() {
        when(privacySettingsRepository.findByUserId(userId1)).thenReturn(Optional.empty());

        PrivacySettingsDto dto = new PrivacySettingsDto();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> profileService.updatePrivacySettings(userId1, dto));

        assertThat(exception.getMessage()).contains("Privacy settings not found");
        verify(privacySettingsRepository, never()).save(any());
    }

    // getProfileWithPrivacy - owner
    @Test
    void getProfileWithPrivacy_returnsFullProfile_whenViewerIsOwner() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));

        ProfileResponse response = profileService.getProfileWithPrivacy(userId1, userId1);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("John");
        assertThat(response.getSurname()).isEqualTo("Doe");
        assertThat(response.getPhoneNumber()).isEqualTo("89991234567");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        verify(privacySettingsRepository, never()).findByUserId(any());
    }

    // getProfileWithPrivacy - other viewer
    @Test
    void getProfileWithPrivacy_appliesPrivacyFlags_forOtherViewer() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(privacySettingsRepository.findByUserId(userId1)).thenReturn(Optional.of(privacySettings1));

        ProfileResponse response = profileService.getProfileWithPrivacy(userId2, userId1);

        assertThat(response).isNotNull();
        assertThat(response.getPhoneNumber()).isEqualTo("89991234567"); // показан
        assertThat(response.getEmail()).isEqualTo("john@example.com"); // показан
        assertThat(response.getBirthday()).isNull(); // скрыт
        assertThat(response.getBio()).isNotNull();
        assertThat(response.getBio().getCity()).isEqualTo("Omsk"); // показан
        assertThat(response.getBio().getPlaceOfWork()).isEqualTo("NanoTech Corp"); // показан
    }

    // getProfileWithPrivacy - private settings
    @Test
    void getProfileWithPrivacy_hidesPrivateFields_forOtherViewer() {
        when(profileRepository.findByUserId(userId2)).thenReturn(Optional.of(testProfile2));
        when(privacySettingsRepository.findByUserId(userId2)).thenReturn(Optional.of(privacySettings2));

        ProfileResponse response = profileService.getProfileWithPrivacy(userId1, userId2);

        assertThat(response).isNotNull();
        assertThat(response.getPhoneNumber()).isNull(); // скрыт
        assertThat(response.getEmail()).isNull(); // скрыт
        assertThat(response.getBirthday()).isNull(); // скрыт
        assertThat(response.getBio()).isNotNull();
        assertThat(response.getBio().getCity()).isEqualTo("Moscow"); // показан
        assertThat(response.getBio().getPlaceOfWork()).isNull(); // скрыт
        assertThat(response.getBio().getJobTitle()).isNull(); // скрыт
        assertThat(response.getBio().getPersonalInterests()).isNull(); // скрыт
    }

    @Test
    void getProfileWithPrivacy_ProfileNotFound() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.empty());

        ProfileNotFoundException exception = assertThrows(ProfileNotFoundException.class,
                () -> profileService.getProfileWithPrivacy(userId2, userId1));

        assertThat(exception.getMessage()).contains("Profile not found for user:");
        verify(privacySettingsRepository, never()).findByUserId(any());
    }

    // getChangedFields
    @Test
    void getChangedFields_ReturnsCorrectFields() {
        ProfileRequest request = new ProfileRequest();
        request.setName("New Name");
        request.setSurname("New Surname");
        request.setCity("New City");
        // Остальные поля null

        List<String> changedFields = profileService.getChangedFields(request);

        assertThat(changedFields).hasSize(3);
        assertThat(changedFields).contains("name", "surname", "city");
        assertThat(changedFields).doesNotContain("phoneNumber", "email", "status");
    }

    @Test
    void getChangedFields_ReturnsEmptyList_WhenAllFieldsNull() {
        ProfileRequest request = new ProfileRequest();
        // Все поля null

        List<String> changedFields = profileService.getChangedFields(request);

        assertThat(changedFields).isEmpty();
    }

    // DataPersistenceException tests
    @Test
    void updateProfile_ThrowsDataPersistenceException_OnSaveError() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(profileRepository.save(any(Profile.class))).thenThrow(new RuntimeException("Database error"));

        ProfileRequest request = new ProfileRequest();
        request.setName("John");

        DataPersistenceException exception = assertThrows(DataPersistenceException.class,
                () -> profileService.updateProfile(userId1, request));

        assertThat(exception.getMessage()).contains("Failed to update profile");
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void deleteProfile_ThrowsDataPersistenceException_OnDeleteError() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        doThrow(new RuntimeException("Database error")).when(profileRepository).deleteByUserId(userId1);

        DataPersistenceException exception = assertThrows(DataPersistenceException.class,
                () -> profileService.deleteProfile(userId1));

        assertThat(exception.getMessage()).contains("Failed to delete profile");
        verify(profileRepository).deleteByUserId(userId1);
    }
}