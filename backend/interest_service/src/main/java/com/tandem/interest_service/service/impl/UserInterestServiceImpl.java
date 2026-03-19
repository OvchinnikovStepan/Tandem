package com.tandem.interest_service.service.impl;

import com.tandem.interest_service.dal.UserInterestDal;
import com.tandem.interest_service.integration.InterestEventPublisher;
import com.tandem.interest_service.service.UserInterestService;
import com.tandem.interest_service.service.exception.UserInterestNotFoundException;
import com.tandem.interest_service.service.model.request.UserInterestRequest;
import com.tandem.interest_service.service.model.response.UserInterestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInterestServiceImpl implements UserInterestService {

    private final UserInterestDal userInterestDal;
    private final InterestEventPublisher eventPublisher;

    @Override
    @Transactional
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
        eventPublisher.publishInterestsUpdated(responses);

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
}