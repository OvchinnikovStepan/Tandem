package com.tandem.profile_service.service;

import com.tandem.profile_service.dto.OnboardingCompleteRequest;
import com.tandem.profile_service.dto.OnboardingCompleteResponse;
import com.tandem.profile_service.dto.OnboardingEventData;
import com.tandem.profile_service.dto.OnboardingQuestionsResponse;
import com.tandem.profile_service.exception.DataPersistenceException;
import com.tandem.profile_service.exception.OnboardingException;
import com.tandem.profile_service.exception.ProfileNotFoundException;
import com.tandem.profile_service.exception.ValidationException;
import com.tandem.profile_service.kafka.ProfileEventPublisher;
import com.tandem.profile_service.model.OnboardingResponse;
import com.tandem.profile_service.model.Poll;
import com.tandem.profile_service.model.Profile;
import com.tandem.profile_service.model.Question;
import com.tandem.profile_service.repository.OnboardingResponseRepository;
import com.tandem.profile_service.repository.PollRepository;
import com.tandem.profile_service.repository.ProfileRepository;
import com.tandem.profile_service.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingService {

    private final QuestionRepository questionRepository;
    private final PollRepository pollRepository;
    private final OnboardingResponseRepository onboardingResponseRepository;
    private final ProfileRepository profileRepository;
    private final ProfileEventPublisher profileEventPublisher;

    /**
     * Возвращает активный онбординг-опрос и список его вопросов.
     * Бросает RuntimeException, если профиль не найден/онбординг уже завершен/нет активного опроса.
     */
    @Transactional
    public OnboardingQuestionsResponse getOnboardingQuestions(UUID userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        if (profile.isOnboardingCompleted()) {
            log.warn("Onboarding already completed for userId={}", userId);
            throw new OnboardingException("Onboarding already completed for user: " + userId);
        }

        Poll poll = pollRepository.findLatestActive()
                .orElseThrow(() -> new OnboardingException("No active onboarding poll found"));

        UUID pollId = poll.getId();

        List<Question> questions = questionRepository.findByPollId(pollId);
        log.info("Retrieved {} questions for pollId={}", questions.size(), pollId);

        List<OnboardingQuestionsResponse.QuestionDto> questionDtos = questions.stream()
                .map(q -> OnboardingQuestionsResponse.QuestionDto.builder()
                        .id(q.getId())
                        .type(q.getQuestionType().getValue())
                        .label(q.getLabel())
                        .required(q.isRequired())
                        .options(q.getOptions())
                        .validation(q.getValidationRules())
                        .build())
                .toList();

        return OnboardingQuestionsResponse.builder()
                .pollId(pollId)
                .questions(questionDtos)
                .build();
    }

    /**
     * Принимает ответы пользователя на онбординг-опрос, сохраняет ответы в onboarding_responses
     */
    @Transactional
    public OnboardingCompleteResponse completeOnboarding(UUID userId, OnboardingCompleteRequest request) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        if (profile.isOnboardingCompleted()) {
            log.warn("Attempt to complete already completed onboarding for userId={}", userId);
            throw new OnboardingException("Onboarding already completed for user: " + userId);
        }

        Poll poll = pollRepository.findLatestActive()
                .orElseThrow(() -> new OnboardingException("No active onboarding poll found"));

        UUID pollId = poll.getId();

        List<Question> questions = questionRepository.findByPollId(pollId);
        if (questions.isEmpty()) {
            log.error("Onboarding poll {} has no questions", pollId);
            throw new OnboardingException("Onboarding poll has no questions");
        }

        Map<UUID, Question> questionById = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        validateRequiredQuestions(questions, request);

        saveResponsesAndUpdateProfile(userId, pollId, profile, questionById, request);

        markOnboardingCompleted(profile);

        OnboardingCompleteResponse response = buildCompleteResponse(userId, profile);

        publishOnboardingCompletedEvent(userId, response);

        log.info("Onboarding completed successfully for userId={}", userId);
        return response;
    }

    /**
     * Проверяет, что все обязательные вопросы опроса присутствуют в списке ответов.
     * Бросает RuntimeException, если найден обязательный вопрос без ответа.
     */
    private void validateRequiredQuestions(List<Question> questions, OnboardingCompleteRequest request) {
        Set<UUID> answeredIds = request.getResponses().stream()
                .map(OnboardingCompleteRequest.ResponseItem::getQuestionId)
                .collect(Collectors.toSet());

        List<Question> requiredQuestions = questions.stream()
                .filter(Question::isRequired)
                .toList();

        for (Question rq : requiredQuestions) {
            // Если ID обязательного вопроса отсутствует в answeredIds - бросает исключение
            if (!answeredIds.contains(rq.getId())) {
                throw new ValidationException("Required question not answered: " + rq.getId());
            }
        }
        log.debug("All required questions are answered");
    }

    /**
     * Сохраняет ответы пользователя в таблицу onboarding_responses и обновляет профиль.
     */
    @Transactional
    private void saveResponsesAndUpdateProfile(
            UUID userId,
            UUID pollId,
            Profile profile,
            Map<UUID, Question> questionById,
            OnboardingCompleteRequest request
    ) {
        try {
            for (OnboardingCompleteRequest.ResponseItem item : request.getResponses()) {
                UUID questionId = item.getQuestionId();
                Object answer = item.getAnswer();

                Question question = questionById.get(questionId);
                if (question == null) {
                    throw new ValidationException("Unknown question: " + questionId);
                }

                OnboardingResponse response = onboardingResponseRepository.createResponse(
                        userId,
                        pollId,
                        questionId,
                        answer
                );

                applyAnswerToProfile(profile, question, answer);
            }

            profileRepository.save(profile);

            log.info("All responses saved and profile updated for userId={}", userId);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to save onboarding responses for userId={}", userId, e);
            throw new DataPersistenceException("Failed to save onboarding responses", e);
        }
    }

    /**
     * Помечает онбординг завершенным.
     */
    @Transactional
    private void markOnboardingCompleted(Profile profile) {
        if (!profile.isOnboardingCompleted()) {
            profile.setOnboardingCompleted(true);
            profile.setOnboardingCompletedAt(LocalDateTime.now());
        }
        profile.setUpdatedAt(LocalDateTime.now());
        log.info("Profile saved with onboarding completed status");
    }

    /**
     * Применяет ответ на конкретный вопрос к полям профиля.
     */
    private void applyAnswerToProfile(Profile profile, Question question, Object answer) {
        if (answer == null) {
            log.debug("Answer is null, skipping profile update");
            return;
        }

        String profileField = question.getProfileField();
        if (profileField == null || profileField.isBlank()) {
            log.debug("ProfileField not set for question {}, skipping profile update", question.getId());
            return;
        }

        String value;
        // Проверка список ли ответ (для интересов), преобразовываем в строку
        if (answer instanceof List<?> listAnswer) {
            value = listAnswer.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
        } else {
            value = answer.toString();
        }

        setProfileField(profile, profileField, value);
    }

    /**
     * Динамически устанавливает значение поля профиля по имени profileField.
     */
    private void setProfileField(Profile profile, String profileField, String value) {
        try {
            Field field = Profile.class.getDeclaredField(profileField);
            field.setAccessible(true);

            Class<?> fieldType = field.getType();
            Object converted = convertValue(fieldType, value);

            if (converted != null) {
                field.set(profile, converted);
                log.debug("Set field {} = {}", profileField, value);
            }
        } catch (NoSuchFieldException e) {
            log.warn("Field {} not found in Profile class", profileField);
        } catch (IllegalAccessException e) {
            log.error("Cannot access field {} in Profile class", profileField, e);
        }
    }

    /**
     * Преобразование строкового значения к типу поля профиля.
     */
    private Object convertValue(Class<?> fieldType, String value) {
        if (fieldType.equals(String.class)) {
            return value;
        }
        return null;
    }

    /**
     * Создает OnboardingEventData для публикации события в Kafka
     */
    public OnboardingEventData buildOnboardingEventData(UUID userId, OnboardingCompleteResponse response) {
        Profile profile = response.getProfile();

        List<String> interests = extractInterestsFromProfile(profile);

        log.info("Extracted {} interests for userId={}: {}", interests.size(), userId, interests);
        return OnboardingEventData.from(userId, response, interests);
    }

    /**
     * Извлекает интересы из поля personalInterests профиля
     */
    private List<String> extractInterestsFromProfile(Profile profile) {
        List<String> interests = new ArrayList<>();

        if (profile.getPersonalInterests() == null || profile.getPersonalInterests().isBlank()) {
            return interests;
        }

        String[] parts = profile.getPersonalInterests().split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isBlank()) {
                interests.add(trimmed);
            }
        }
        return interests;
    }

    /**
     * Создание ответа для контроллера
     */
    private OnboardingCompleteResponse buildCompleteResponse(UUID userId, Profile profile) {
        return OnboardingCompleteResponse.builder()
                .profile(profile)
                .onboardingCompleted(profile.isOnboardingCompleted())
                .userId(userId)
                .build();
    }

    /**
     * Публикация события в kafka о завершении онбординга
     */
    @Transactional
    public void publishOnboardingCompletedEvent(UUID userId, OnboardingCompleteResponse response) {
        try {
            OnboardingEventData eventData = buildOnboardingEventData(userId, response);

            profileEventPublisher.publishOnboardingCompleted(
                    eventData.getUserId(),
                    eventData.getProfileId(),
                    eventData.getName(),
                    eventData.getSurname(),
                    eventData.getInterests()
            );

            log.info("Successfully published onboarding completed event for userId={}", userId);
        } catch (Exception e) {
            log.error("Failed to publish onboarding completed event for userId={}", userId, e);
        }
    }
}
