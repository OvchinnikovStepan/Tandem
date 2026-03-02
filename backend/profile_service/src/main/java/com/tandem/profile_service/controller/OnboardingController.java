package com.tandem.profile_service.controller;

import com.tandem.profile_service.security.SecurityUtils;
import com.tandem.profile_service.dto.OnboardingCompleteRequest;
import com.tandem.profile_service.dto.OnboardingCompleteResponse;
import com.tandem.profile_service.dto.OnboardingQuestionsResponse;
import com.tandem.profile_service.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    /**
     * GET /api/profile/onboarding/questions
     */
    @GetMapping("/questions")
    public ResponseEntity<OnboardingQuestionsResponse> getOnboardingQuestions() {
        UUID currentUserId = SecurityUtils.getCurrentUserIdOrThrow();

        OnboardingQuestionsResponse response = onboardingService.getOnboardingQuestions(currentUserId);
        return ResponseEntity.ok(response);
    }


    /**
     * POST /api/profile/onboarding/complete
     */
    @PostMapping("/complete")
    public ResponseEntity<OnboardingCompleteResponse> completeOnboarding(
            @RequestBody OnboardingCompleteRequest requestBody) {

        UUID currentUserId = SecurityUtils.getCurrentUserIdOrThrow();
        OnboardingCompleteResponse response = onboardingService.completeOnboarding(currentUserId, requestBody);
        return ResponseEntity.ok(response);
    }
}
