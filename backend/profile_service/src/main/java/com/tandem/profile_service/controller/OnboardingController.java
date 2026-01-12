package com.tandem.profile_service.controller;

import com.tandem.profile_service.dto.OnboardingCompleteRequest;
import com.tandem.profile_service.dto.OnboardingCompleteResponse;
import com.tandem.profile_service.dto.OnboardingEventData;
import com.tandem.profile_service.dto.OnboardingQuestionsResponse;
import com.tandem.profile_service.kafka.ProfileEventPublisher;
import com.tandem.profile_service.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;
    private final ProfileEventPublisher profileEventPublisher;

    /**
     * Метод для извлечения userId из JWT токена
     */
    private UUID getCurrentUserId() {
        // TODO: Реализовать
        // Пока заглушка, возвращает тестовый userId
        return UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
    }


    /**
     * GET /api/profile/onboarding/questions
     */
    @GetMapping("/questions")
    public ResponseEntity<OnboardingQuestionsResponse> getOnboardingQuestions() {

        UUID currentUserId = getCurrentUserId();

        try {
            OnboardingQuestionsResponse response =
                    onboardingService.getOnboardingQuestions(currentUserId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/profile/onboarding/complete
     */
    @PostMapping("/complete")
    public ResponseEntity<OnboardingCompleteResponse> completeOnboarding(
            @RequestBody OnboardingCompleteRequest request) {

        UUID currentUserId = getCurrentUserId();

        try {
            OnboardingCompleteResponse response =
                    onboardingService.completeOnboarding(currentUserId, request);

            OnboardingEventData eventData =
                    onboardingService.buildOnboardingEventData(currentUserId, response);

            profileEventPublisher.publishOnboardingCompleted(
                    eventData.getUserId(),
                    eventData.getProfileId(),
                    eventData.getName(),
                    eventData.getSurname(),
                    eventData.getInterests()
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
