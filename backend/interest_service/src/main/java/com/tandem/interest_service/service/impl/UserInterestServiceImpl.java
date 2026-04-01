package com.tandem.interest_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.interest_service.dal.UserInterestDal;
import com.tandem.interest_service.integration.model.OnboardingCompletedEvent;
import com.tandem.interest_service.service.UserInterestService;
import com.tandem.interest_service.service.exception.UserInterestNotFoundException;
import com.tandem.interest_service.service.model.request.UserInterestRequest;
import com.tandem.interest_service.service.model.response.TagResponse;
import com.tandem.interest_service.service.model.response.UserInterestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInterestServiceImpl implements UserInterestService {

    private final UserInterestDal userInterestDal;
    private final ObjectMapper objectMapper;

    @Override
    public List<UserInterestResponse> addUserInterest(List<UserInterestRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            log.info("No interests to add - empty request list");
            return List.of();
        }

        log.info("Adding {} interests for user: {}",
                requests.size(), requests.get(0).getUserId());

        for (UserInterestRequest request : requests) {
            if (request.getUserId() == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            if (request.getTagId() == null) {
                throw new IllegalArgumentException("Tag ID cannot be null");
            }
        }

        List<UserInterestResponse> responses = userInterestDal.insert(requests);

        log.info("Successfully added {} interests for user: {}",
                responses.size(), requests.get(0).getUserId());

        return responses;
    }

    @Override
    public void removeUserInterest(UserInterestRequest request) {
        log.info("Removing user interest with tag_id: {} for user : {}",
                request.getTagId(), request.getUserId());

        if (request.getUserId() == null || request.getTagId() == null) {
            throw new IllegalArgumentException("User ID and Tag ID cannot be null");
        }

        try {
            UserInterestResponse interest = userInterestDal.getUserInterest(
                    request.getUserId(), request.getTagId()
            );
            userInterestDal.delete(interest.getId());

            log.info("Successfully removed interest for user: {} with tag: {}",
                    request.getUserId(), request.getTagId());

        } catch (Exception e) {
            if (e.getMessage() != null) {
                throw new UserInterestNotFoundException(request.getUserId(), request.getTagId());
            }
            log.error("Failed to remove user interest", e);
            throw new RuntimeException("Failed to remove user interest", e);
        }
    }

    @Override
    public List<UserInterestResponse> getUserInterests(UUID userId) {
        log.info("Fetching all interests for user: {}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        List<UserInterestResponse> interests = userInterestDal.getUserInterests(userId);

        log.info("Found {} interests for user: {}", interests.size(), userId);
        return interests;
    }

    @Override
    public List<UserInterestRequest> parseToUserInterestRequests(String message) {
        try {
            // Парсим JSON в DTO
            OnboardingCompletedEvent event = objectMapper.readValue(message, OnboardingCompletedEvent.class);

            UUID userId;
            try {
                userId = event.getUserIdAsUUID();
            } catch (Exception e) {
                log.error("Invalid UUID format in message: {}", event.getUserId());
                return List.of();
            }

            List<String> interestNames = event.getInterests();

            if (interestNames.isEmpty()) {
                return List.of();
            }

            List<TagResponse> tags = new ArrayList<>();
            for (String interest : interestNames) {
                try {
                    TagResponse tag = userInterestDal.findTagByName(interest);
                    if (tag != null) {
                        tags.add(tag);
                    } else {
                        // Просто логируем, что такого тега нет, но не прерываем цикл
                        log.warn("Tag not found in database: '{}'", interest);
                    }
                } catch (Exception e) {
                    log.error("Error while searching for tag '{}': {}", interest, e.getMessage());
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
