package com.tandem.profile_service;

import com.tandem.profile_service.dto.OnboardingCompleteRequest;
import com.tandem.profile_service.dto.OnboardingCompleteResponse;
import com.tandem.profile_service.dto.OnboardingQuestionsResponse;
import com.tandem.profile_service.exception.ProfileNotFoundException;
import com.tandem.profile_service.exception.OnboardingException;
import com.tandem.profile_service.exception.ValidationException;
import com.tandem.profile_service.exception.DataPersistenceException;
import com.tandem.profile_service.kafka.ProfileEventPublisher;
import com.tandem.profile_service.model.Question;
import com.tandem.profile_service.model.Poll;
import com.tandem.profile_service.model.Profile;
import com.tandem.profile_service.repository.OnboardingResponseRepository;
import com.tandem.profile_service.repository.PollRepository;
import com.tandem.profile_service.repository.ProfileRepository;
import com.tandem.profile_service.repository.QuestionRepository;
import com.tandem.profile_service.service.OnboardingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private PollRepository pollRepository;

    @Mock
    private OnboardingResponseRepository onboardingResponseRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ProfileEventPublisher profileEventPublisher;

    private OnboardingService onboardingService;

    private UUID userId1;
    private UUID userId2;
    private UUID profileId1;
    private UUID profileId2;
    private UUID pollId1;
    private UUID questionId1;
    private UUID questionId2;
    private UUID questionId3;
    private UUID questionId4;
    private UUID questionId5;
    private UUID questionId6;
    private UUID questionId7;

    private Profile testProfile1;
    private Profile testProfile2;
    private Poll testPoll1;
    private Question testQuestion1;
    private Question testQuestion2;
    private Question testQuestion3;
    private Question testQuestion4;
    private Question testQuestion5;
    private Question testQuestion6;
    private Question testQuestion7;

    @BeforeEach
    void setUp() {
        onboardingService = new OnboardingService(
                questionRepository,
                pollRepository,
                onboardingResponseRepository,
                profileRepository,
                profileEventPublisher
        );
        initializeTestData();
    }

    private void initializeTestData() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        profileId1 = UUID.randomUUID();
        profileId2 = UUID.randomUUID();
        pollId1 = UUID.randomUUID();

        questionId1 = UUID.randomUUID();
        questionId2 = UUID.randomUUID();
        questionId3 = UUID.randomUUID();
        questionId4 = UUID.randomUUID();
        questionId5 = UUID.randomUUID();
        questionId6 = UUID.randomUUID();
        questionId7 = UUID.randomUUID();

        // Первый профиль - онбординг не завершен
        testProfile1 = Profile.builder()
                .id(profileId1)
                .userId(userId1)
                .email("john@example.com")
                .phoneNumber("89991234567")
                .name(null)
                .surname(null)
                .status(null)
                .birthday(null)
                .city(null)
                .placeOfWork(null)
                .jobTitle(null)
                .personalInterests(null)
                .onboardingCompleted(false)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        // Второй профиль - онбординг завершен
        testProfile2 = Profile.builder()
                .id(profileId2)
                .userId(userId2)
                .email("jane@example.com")
                .phoneNumber("89995555555")
                .name("Jane")
                .surname("Smith")
                .status("away")
                .onboardingCompleted(true)
                .onboardingCompletedAt(LocalDateTime.now().minusDays(10))
                .createdAt(LocalDateTime.now().minusDays(30))
                .updatedAt(LocalDateTime.now().minusDays(5))
                .build();

        testPoll1 = Poll.builder()
                .id(pollId1)
                .name("onboarding")
                .version(1)
                .isActive(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();

        Map<String, Object> nameRules = new HashMap<>();
        nameRules.put("minLength", 2);
        nameRules.put("maxLength", 50);
        testQuestion1 = Question.builder()
                .id(questionId1)
                .pollId(pollId1)
                .questionOrder(1)
                .questionType(Question.QuestionType.TEXT)
                .label("name")
                .description("Your first name")
                .isRequired(true)
                .profileField("name")
                .options(new ArrayList<>())
                .validationRules(nameRules)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        Map<String, Object> surnameRules = new HashMap<>();
        surnameRules.put("minLength", 2);
        surnameRules.put("maxLength", 50);
        testQuestion2 = Question.builder()
                .id(questionId2)
                .pollId(pollId1)
                .questionOrder(2)
                .questionType(Question.QuestionType.TEXT)
                .label("surname")
                .description("Your surname")
                .isRequired(true)
                .profileField("surname")
                .options(new ArrayList<>())
                .validationRules(surnameRules)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        Map<String, Object> bioRules = new HashMap<>();
        bioRules.put("maxLength", 500);
        testQuestion3 = Question.builder()
                .id(questionId3)
                .pollId(pollId1)
                .questionOrder(3)
                .questionType(Question.QuestionType.TEXTAREA)
                .label("bio")
                .description("Tell us about yourself...")
                .isRequired(false)
                .profileField("personalInterests")
                .options(new ArrayList<>())
                .validationRules(bioRules)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        Map<String, Object> interestsRules = new HashMap<>();
        interestsRules.put("minSelected", 1);
        testQuestion4 = Question.builder()
                .id(questionId4)
                .pollId(pollId1)
                .questionOrder(4)
                .questionType(Question.QuestionType.MULTISELECT)
                .label("personal_interests")
                .description("Select your personal interests")
                .isRequired(true)
                .profileField("personalInterests")
                .options(Arrays.asList("otaku", "doing sports", "gaming", "reading", "music"))
                .validationRules(interestsRules)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        Map<String, Object> cityRules = new HashMap<>();
        cityRules.put("minLength", 2);
        cityRules.put("maxLength", 100);
        testQuestion5 = Question.builder()
                .id(questionId5)
                .pollId(pollId1)
                .questionOrder(5)
                .questionType(Question.QuestionType.TEXT)
                .label("city")
                .description("Your city")
                .isRequired(false)
                .profileField("city")
                .options(new ArrayList<>())
                .validationRules(cityRules)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        Map<String, Object> placeOfWorkRules = new HashMap<>();
        placeOfWorkRules.put("minLength", 2);
        placeOfWorkRules.put("maxLength", 200);
        testQuestion6 = Question.builder()
                .id(questionId6)
                .pollId(pollId1)
                .questionOrder(6)
                .questionType(Question.QuestionType.TEXT)
                .label("place_of_work")
                .description("Your place of work")
                .isRequired(false)
                .profileField("placeOfWork")
                .options(new ArrayList<>())
                .validationRules(placeOfWorkRules)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        Map<String, Object> jobTitleRules = new HashMap<>();
        jobTitleRules.put("minLength", 2);
        jobTitleRules.put("maxLength", 100);
        testQuestion7 = Question.builder()
                .id(questionId7)
                .pollId(pollId1)
                .questionOrder(7)
                .questionType(Question.QuestionType.TEXT)
                .label("job_title")
                .description("Your job title")
                .isRequired(false)
                .profileField("jobTitle")
                .options(new ArrayList<>())
                .validationRules(jobTitleRules)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    void getOnboardingQuestions_Success() {
        // Arrange
        List<Question> questions = Arrays.asList(
                testQuestion1, testQuestion2, testQuestion3, testQuestion4,
                testQuestion5, testQuestion6, testQuestion7
        );

        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(pollRepository.findLatestActive()).thenReturn(Optional.of(testPoll1));
        when(questionRepository.findByPollId(pollId1)).thenReturn(questions);

        // Act
        OnboardingQuestionsResponse response = onboardingService.getOnboardingQuestions(userId1);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getPollId()).isEqualTo(pollId1);
        assertThat(response.getQuestions()).hasSize(7);
        assertThat(response.getQuestions().get(0).getId()).isEqualTo(questionId1);
        assertThat(response.getQuestions().get(0).getLabel()).isEqualTo("name");
        assertThat(response.getQuestions().get(0).isRequired()).isTrue();
        assertThat(response.getQuestions().get(3).getLabel()).isEqualTo("personal_interests");
        assertThat(response.getQuestions().get(3).getType()).isEqualTo("multiselect");

        verify(profileRepository).findByUserId(userId1);
        verify(pollRepository).findLatestActive();
        verify(questionRepository).findByPollId(pollId1);
    }

    @Test
    void getOnboardingQuestions_ProfileNotFound() {
        // Arrange
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.empty());

        // Act & Assert
        ProfileNotFoundException exception = assertThrows(ProfileNotFoundException.class,
                () -> onboardingService.getOnboardingQuestions(userId1));

        assertThat(exception.getMessage()).contains("Profile not found for user");
        verify(pollRepository, never()).findLatestActive();
        verify(questionRepository, never()).findByPollId(any());
    }

    @Test
    void getOnboardingQuestions_AlreadyCompleted() {
        // Arrange
        when(profileRepository.findByUserId(userId2)).thenReturn(Optional.of(testProfile2));

        // Act & Assert
        OnboardingException exception = assertThrows(OnboardingException.class,
                () -> onboardingService.getOnboardingQuestions(userId2));

        assertThat(exception.getMessage()).contains("Onboarding already completed");
        verify(pollRepository, never()).findLatestActive();
        verify(questionRepository, never()).findByPollId(any());
    }

    @Test
    void getOnboardingQuestions_NoActivePoll() {
        // Arrange
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(pollRepository.findLatestActive()).thenReturn(Optional.empty());

        // Act & Assert
        OnboardingException exception = assertThrows(OnboardingException.class,
                () -> onboardingService.getOnboardingQuestions(userId1));

        assertThat(exception.getMessage()).contains("No active onboarding poll");
        verify(questionRepository, never()).findByPollId(any());
    }

    @Test
    void completeOnboarding_Success() {
        // Arrange
        List<Question> questions = Arrays.asList(
                testQuestion1, testQuestion2, testQuestion3, testQuestion4,
                testQuestion5, testQuestion6, testQuestion7
        );

        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(pollRepository.findLatestActive()).thenReturn(Optional.of(testPoll1));
        when(questionRepository.findByPollId(pollId1)).thenReturn(questions);
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock для createResponse - используем builder или просто возвращаем null
        when(onboardingResponseRepository.createResponse(any(UUID.class), any(UUID.class), any(UUID.class), any()))
                .thenReturn(null); // или OnboardingResponse.builder().build() если builder публичный

        OnboardingCompleteRequest request = new OnboardingCompleteRequest();
        List<OnboardingCompleteRequest.ResponseItem> responses = Arrays.asList(
                createResponseItem(questionId1, "John"),
                createResponseItem(questionId2, "Doe"),
                createResponseItem(questionId3, "Researcher in nanotechnology"),
                createResponseItem(questionId4, Arrays.asList("gaming", "reading")),
                createResponseItem(questionId5, "Omsk"),
                createResponseItem(questionId6, "NanoTech Corp"),
                createResponseItem(questionId7, "Senior Researcher")
        );
        request.setResponses(responses);

        // Act
        OnboardingCompleteResponse response = onboardingService.completeOnboarding(userId1, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getProfile()).isNotNull();
        assertThat(response.isOnboardingCompleted()).isTrue();
        assertThat(response.getUserId()).isEqualTo(userId1);

        // Проверяем, что профиль обновлен
        assertThat(testProfile1.getName()).isEqualTo("John");
        assertThat(testProfile1.getSurname()).isEqualTo("Doe");
        assertThat(testProfile1.getPersonalInterests()).isEqualTo("gaming, reading");
        assertThat(testProfile1.getCity()).isEqualTo("Omsk");
        assertThat(testProfile1.getPlaceOfWork()).isEqualTo("NanoTech Corp");
        assertThat(testProfile1.getJobTitle()).isEqualTo("Senior Researcher");
        assertThat(testProfile1.isOnboardingCompleted()).isTrue();
        assertThat(testProfile1.getOnboardingCompletedAt()).isNotNull();

        // Проверяем вызовы
        verify(profileRepository).save(testProfile1);
        verify(onboardingResponseRepository, times(7)).createResponse(any(), any(), any(), any());
        verify(profileEventPublisher).publishOnboardingCompleted(any(), any(), any(), any(), any());
    }

    @Test
    void completeOnboarding_ProfileNotFound() {
        // Arrange
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.empty());

        OnboardingCompleteRequest request = new OnboardingCompleteRequest();
        request.setResponses(Arrays.asList());

        // Act & Assert
        ProfileNotFoundException exception = assertThrows(ProfileNotFoundException.class,
                () -> onboardingService.completeOnboarding(userId1, request));

        assertThat(exception.getMessage()).contains("Profile not found for user");
        verify(pollRepository, never()).findLatestActive();
        verify(questionRepository, never()).findByPollId(any());
    }

    @Test
    void completeOnboarding_AlreadyCompleted() {
        // Arrange
        when(profileRepository.findByUserId(userId2)).thenReturn(Optional.of(testProfile2));

        OnboardingCompleteRequest request = new OnboardingCompleteRequest();
        request.setResponses(Arrays.asList());

        // Act & Assert
        OnboardingException exception = assertThrows(OnboardingException.class,
                () -> onboardingService.completeOnboarding(userId2, request));

        assertThat(exception.getMessage()).contains("Onboarding already completed");
        verify(pollRepository, never()).findLatestActive();
        verify(questionRepository, never()).findByPollId(any());
    }

    @Test
    void completeOnboarding_NoActivePoll() {
        // Arrange
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(pollRepository.findLatestActive()).thenReturn(Optional.empty());

        OnboardingCompleteRequest request = new OnboardingCompleteRequest();
        request.setResponses(Arrays.asList());

        // Act & Assert
        OnboardingException exception = assertThrows(OnboardingException.class,
                () -> onboardingService.completeOnboarding(userId1, request));

        assertThat(exception.getMessage()).contains("No active onboarding poll");
        verify(questionRepository, never()).findByPollId(any());
    }

    @Test
    void completeOnboarding_NoQuestionsInPoll() {
        // Arrange
        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(pollRepository.findLatestActive()).thenReturn(Optional.of(testPoll1));
        when(questionRepository.findByPollId(pollId1)).thenReturn(Arrays.asList());

        OnboardingCompleteRequest request = new OnboardingCompleteRequest();
        request.setResponses(Arrays.asList());

        // Act & Assert
        OnboardingException exception = assertThrows(OnboardingException.class,
                () -> onboardingService.completeOnboarding(userId1, request));

        assertThat(exception.getMessage()).contains("Onboarding poll has no questions");
        verify(profileRepository, never()).save(any());
    }

    @Test
    void completeOnboarding_RequiredQuestionNotAnswered() {
        // Arrange
        List<Question> questions = Arrays.asList(
                testQuestion1, testQuestion2, testQuestion3, testQuestion4,
                testQuestion5, testQuestion6, testQuestion7
        );

        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(pollRepository.findLatestActive()).thenReturn(Optional.of(testPoll1));
        when(questionRepository.findByPollId(pollId1)).thenReturn(questions);

        OnboardingCompleteRequest request = new OnboardingCompleteRequest();
        request.setResponses(Arrays.asList(
                createResponseItem(questionId2, "Doe"),
                createResponseItem(questionId4, Arrays.asList("gaming"))
        ));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> onboardingService.completeOnboarding(userId1, request));

        assertThat(exception.getMessage()).contains("Required question not answered");
        verify(onboardingResponseRepository, never()).createResponse(any(), any(), any(), any());
        verify(profileRepository, never()).save(any());
    }

    @Test
    void completeOnboarding_UnknownQuestion() {
        // Arrange
        List<Question> questions = Arrays.asList(
                testQuestion1, testQuestion2, testQuestion3, testQuestion4,
                testQuestion5, testQuestion6, testQuestion7
        );

        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(pollRepository.findLatestActive()).thenReturn(Optional.of(testPoll1));
        when(questionRepository.findByPollId(pollId1)).thenReturn(questions);

        UUID unknownQuestionId = UUID.randomUUID();
        OnboardingCompleteRequest request = new OnboardingCompleteRequest();
        request.setResponses(Arrays.asList(
                createResponseItem(questionId1, "John"),
                createResponseItem(questionId2, "Doe"),
                createResponseItem(questionId4, Arrays.asList("gaming")),
                createResponseItem(unknownQuestionId, "Unknown Answer")
        ));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> onboardingService.completeOnboarding(userId1, request));

        assertThat(exception.getMessage()).contains("Unknown question");
        verify(profileRepository, never()).save(any());
    }

    @Test
    void completeOnboarding_SaveResponsesError() {
        // Arrange
        List<Question> questions = Arrays.asList(
                testQuestion1, testQuestion2, testQuestion3, testQuestion4,
                testQuestion5, testQuestion6, testQuestion7
        );

        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(pollRepository.findLatestActive()).thenReturn(Optional.of(testPoll1));
        when(questionRepository.findByPollId(pollId1)).thenReturn(questions);
        when(onboardingResponseRepository.createResponse(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        OnboardingCompleteRequest request = new OnboardingCompleteRequest();
        request.setResponses(Arrays.asList(
                createResponseItem(questionId1, "John"),
                createResponseItem(questionId2, "Doe"),
                createResponseItem(questionId4, Arrays.asList("gaming"))
        ));

        // Act & Assert
        DataPersistenceException exception = assertThrows(DataPersistenceException.class,
                () -> onboardingService.completeOnboarding(userId1, request));

        assertThat(exception.getMessage()).contains("Failed to save onboarding responses");
        verify(profileRepository, never()).save(any());
    }

    @Test
    void completeOnboarding_SetsProfileFieldsCorrectly() {
        // Arrange
        List<Question> questions = Arrays.asList(testQuestion1, testQuestion2, testQuestion4, testQuestion5);

        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(pollRepository.findLatestActive()).thenReturn(Optional.of(testPoll1));
        when(questionRepository.findByPollId(pollId1)).thenReturn(questions);
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock для createResponse
        when(onboardingResponseRepository.createResponse(any(UUID.class), any(UUID.class), any(UUID.class), any()))
                .thenReturn(null);

        OnboardingCompleteRequest request = new OnboardingCompleteRequest();
        request.setResponses(Arrays.asList(
                createResponseItem(questionId1, "John"),
                createResponseItem(questionId2, "Doe"),
                createResponseItem(questionId4, Arrays.asList("gaming", "music")),
                createResponseItem(questionId5, "Moscow")
        ));

        // Act
        OnboardingCompleteResponse response = onboardingService.completeOnboarding(userId1, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(testProfile1.getName()).isEqualTo("John");
        assertThat(testProfile1.getSurname()).isEqualTo("Doe");
        assertThat(testProfile1.getPersonalInterests()).isEqualTo("gaming, music");
        assertThat(testProfile1.getCity()).isEqualTo("Moscow");
        assertThat(testProfile1.isOnboardingCompleted()).isTrue();
    }

    @Test
    void completeOnboarding_WithEmptyListAnswer() {
        // Arrange
        List<Question> questions = Arrays.asList(testQuestion1, testQuestion2, testQuestion4);

        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(pollRepository.findLatestActive()).thenReturn(Optional.of(testPoll1));
        when(questionRepository.findByPollId(pollId1)).thenReturn(questions);
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(onboardingResponseRepository.createResponse(any(UUID.class), any(UUID.class), any(UUID.class), any()))
                .thenReturn(null);

        OnboardingCompleteRequest request = new OnboardingCompleteRequest();
        request.setResponses(Arrays.asList(
                createResponseItem(questionId1, "John"),
                createResponseItem(questionId2, "Doe"),
                createResponseItem(questionId4, Arrays.asList()) // Пустой список для интересов
        ));

        // Act
        OnboardingCompleteResponse response = onboardingService.completeOnboarding(userId1, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(testProfile1.getName()).isEqualTo("John");
        assertThat(testProfile1.getSurname()).isEqualTo("Doe");
        assertThat(testProfile1.getPersonalInterests()).isEqualTo(""); // Пустая строка для пустого списка
    }

    @Test
    void completeOnboarding_WithoutOptionalFields() {
        // Arrange - отвечаем только на обязательные вопросы
        List<Question> questions = Arrays.asList(testQuestion1, testQuestion2, testQuestion4);

        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(pollRepository.findLatestActive()).thenReturn(Optional.of(testPoll1));
        when(questionRepository.findByPollId(pollId1)).thenReturn(questions);
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(onboardingResponseRepository.createResponse(any(UUID.class), any(UUID.class), any(UUID.class), any()))
                .thenReturn(null);

        OnboardingCompleteRequest request = new OnboardingCompleteRequest();
        request.setResponses(Arrays.asList(
                createResponseItem(questionId1, "John"),
                createResponseItem(questionId2, "Doe"),
                createResponseItem(questionId4, Arrays.asList("reading"))
        ));

        // Act
        OnboardingCompleteResponse response = onboardingService.completeOnboarding(userId1, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(testProfile1.getName()).isEqualTo("John");
        assertThat(testProfile1.getSurname()).isEqualTo("Doe");
        assertThat(testProfile1.getPersonalInterests()).isEqualTo("reading");
        assertThat(testProfile1.getCity()).isNull(); // Не задавали город
        assertThat(testProfile1.isOnboardingCompleted()).isTrue();
    }

    @Test
    void buildOnboardingEventData_ExtractsInterestsCorrectly() {
        // Arrange - через публичный метод completeOnboarding
        List<Question> questions = Arrays.asList(testQuestion1, testQuestion2, testQuestion4);

        when(profileRepository.findByUserId(userId1)).thenReturn(Optional.of(testProfile1));
        when(pollRepository.findLatestActive()).thenReturn(Optional.of(testPoll1));
        when(questionRepository.findByPollId(pollId1)).thenReturn(questions);
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(onboardingResponseRepository.createResponse(any(UUID.class), any(UUID.class), any(UUID.class), any()))
                .thenReturn(null);

        OnboardingCompleteRequest request = new OnboardingCompleteRequest();
        request.setResponses(Arrays.asList(
                createResponseItem(questionId1, "John"),
                createResponseItem(questionId2, "Doe"),
                createResponseItem(questionId4, Arrays.asList("gaming", "music", "reading"))
        ));

        // Act
        OnboardingCompleteResponse response = onboardingService.completeOnboarding(userId1, request);

        // Assert - проверяем, что интересы сохранились правильно
        assertThat(response).isNotNull();
        assertThat(testProfile1.getPersonalInterests()).isEqualTo("gaming, music, reading");

        // Проверяем, что событие было опубликовано
        verify(profileEventPublisher).publishOnboardingCompleted(
                eq(userId1),
                eq(profileId1),
                eq("John"),
                eq("Doe"),
                any(List.class) // интересы
        );
    }

    private OnboardingCompleteRequest.ResponseItem createResponseItem(UUID questionId, Object answer) {
        OnboardingCompleteRequest.ResponseItem item = new OnboardingCompleteRequest.ResponseItem();
        item.setQuestionId(questionId);
        item.setAnswer(answer);
        return item;
    }
}