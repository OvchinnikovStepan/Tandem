package com.tandem.interest_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.interest_service.service.model.request.UserInterestRequest;
import com.tandem.interest_service.service.TagService;
import com.tandem.interest_service.service.model.response.TagResponse;
import com.tandem.interest_service.integration.model.OnboardingCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OnboardingEventParser {

    private final ObjectMapper objectMapper;
    private final TagService tagService;

    public List<UserInterestRequest> parseToUserInterestRequests(String message) {
        try {
            // Парсим JSON в DTO
            OnboardingCompletedEvent event = objectMapper.readValue(message, OnboardingCompletedEvent.class);

            UUID userId = event.getUserIdAsUUID();

            List<String> interestNames = event.getInterests();

            if (interestNames.isEmpty()) {
                return List.of();
            }

            List<TagResponse> tags = new ArrayList<>();
            for (String interest : interestNames) {
                TagResponse tag = tagService.findByName(interest);
                if (tag != null) {
                    tags.add(tag);
                }
            }

            if (tags.isEmpty()) {
                return List.of();
            }

            return tags.stream()
                    .map(tag -> UserInterestRequest.builder()
                            .userId(userId)
                            .tagId(tag.getId())
                            .build())
                    .toList();

        } catch (Exception e) {
            return List.of();
        }
    }
}
