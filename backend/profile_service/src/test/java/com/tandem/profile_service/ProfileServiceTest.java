package com.tandem.profile_service;

import com.tandem.profile_service.dto.PrivacySettingsDto;
import com.tandem.profile_service.dto.ProfileRequest;
import com.tandem.profile_service.dto.ProfileResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private PrivacySettingsRepository privacySettingsRepository;

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
        profileService = new ProfileService(profileRepository, privacySettingsRepository);
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

        privacySettings1 = new PrivacySettings();
        privacySettings1.setUserId(userId1);
        privacySettings1.setShowPhoneNumber(true);
        privacySettings1.setShowEmail(true);
        privacySettings1.setShowCity(true);
        privacySettings1.setShowPlaceOfWork(true);
        privacySettings1.setShowJobTitle(true);
        privacySettings1.setShowBirthday(false);
        privacySettings1.setShowPersonalInterests(true);
        privacySettings1.setCreatedAt(LocalDateTime.now().minusDays(30));
        privacySettings1.setUpdatedAt(LocalDateTime.now().minusDays(5));

        privacySettings2 = new PrivacySettings();
        privacySettings2.setUserId(userId2);
        privacySettings2.setShowPhoneNumber(false);
        privacySettings2.setShowEmail(false);
        privacySettings2.setShowCity(true);
        privacySettings2.setShowPlaceOfWork(false);
        privacySettings2.setShowJobTitle(false);
        privacySettings2.setShowBirthday(false);
        privacySettings2.setShowPersonalInterests(false);
        privacySettings2.setCreatedAt(LocalDateTime.now().minusDays(7));
        privacySettings2.setUpdatedAt(LocalDateTime.now().minusDays(1));
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

        Profile result = profileService.updateProfile(userId1, request);

        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getSurname()).isEqualTo("Doe");
        assertThat(result.getPhoneNumber()).isEqualTo("123456789");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getStatus()).isEqualTo("online");
        assertThat(result.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(result.getCity()).isEqualTo("Omsk");
        assertThat(result.getPlaceOfWork()).isEqualTo("TechCorp");
        assertThat(result.getJobTitle()).isEqualTo("Engineer");
        assertThat(result.getPersonalInterests()).isEqualTo("Nanotechnology");
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(profileRepository).save(testProfile1);
    }

    // patchProfile
    @Test
    void patchProfile_Success() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileRequest request = new ProfileRequest();
        request.setName("NewName");
        request.setSurname("NewSurname");
        // city и email null

        Profile result = profileService.patchProfile(userId1, request);

        assertThat(result.getName()).isEqualTo("NewName");
        assertThat(result.getSurname()).isEqualTo("NewSurname");
        assertThat(result.getCity()).isEqualTo("Omsk"); // не изменился
        assertThat(result.getEmail()).isEqualTo("john@example.com"); // не изменился
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(profileRepository).save(testProfile1);
    }


    // deleteProfile
    @Test
    void deleteProfile_Success() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));

        profileService.deleteProfile(userId1);

        verify(profileRepository).deleteByUserId(userId1);
        verify(privacySettingsRepository).deleteByUserId(userId1);
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

    // updatePrivacySettings
    @Test
    void updatePrivacySettings_Success() {
        when(privacySettingsRepository.findByUserId(userId1)).thenReturn(Optional.of(privacySettings1));

        PrivacySettingsDto dto = new PrivacySettingsDto();
        dto.setShowPhoneNumber(false);
        dto.setShowEmail(false);
        dto.setShowCity(true);
        dto.setShowPlaceOfWork(false);
        dto.setShowJobTitle(true);
        dto.setShowBirthday(true);
        dto.setShowPersonalInterests(false);

        profileService.updatePrivacySettings(userId1, dto);

        assertThat(privacySettings1.isShowPhoneNumber()).isFalse();
        assertThat(privacySettings1.isShowEmail()).isFalse();
        assertThat(privacySettings1.isShowCity()).isTrue();
        assertThat(privacySettings1.isShowBirthday()).isTrue();
        assertThat(privacySettings1.getUpdatedAt()).isNotNull();
        verify(privacySettingsRepository).save(privacySettings1);
        verify(privacySettingsRepository, never()).saveDefaultSettings(any());
    }

    // getProfileWithPrivacy - owner
    @Test
    void getProfileWithPrivacy_returnsFullProfile_whenViewerIsOwner() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));

        ProfileResponse response = profileService.getProfileWithPrivacy(userId1, userId1);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("John");
        verify(privacySettingsRepository, never()).findByUserId(any());
    }

    // getProfileWithPrivacy - other viewer
    @Test
    void getProfileWithPrivacy_appliesPrivacyFlags_forOtherViewer() {
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(privacySettingsRepository.findByUserId(userId1)).thenReturn(Optional.of(privacySettings1));

        ProfileResponse response = profileService.getProfileWithPrivacy(userId2, userId1);

        assertThat(response).isNotNull();
        assertThat(response.getPhoneNumber()).isEqualTo("89991234567");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getBirthday()).isNull(); // скрыт
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

    }
}
