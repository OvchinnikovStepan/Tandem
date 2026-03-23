package com.tandem.interest_service.api.impl;

import com.tandem.interest_service.api.UserInterestApi;
import com.tandem.interest_service.api.mapper.UserInterestApiMapper;
import com.tandem.interest_service.api.model.request.UserInterestCreateRequestJson;
import com.tandem.interest_service.api.model.request.UserInterestDeleteRequestJson;
import com.tandem.interest_service.api.model.response.UserInterestResponseJson;
import com.tandem.interest_service.api.model.response.UserMatchingResponseJson;
import com.tandem.interest_service.security.SecurityUtils;
import com.tandem.interest_service.service.UserInterestService;
import com.tandem.interest_service.service.impl.MatchingServiceImpl;
import com.tandem.interest_service.service.model.request.UserInterestRequest;
import com.tandem.interest_service.service.model.response.UserInterestResponse;
import com.tandem.interest_service.service.model.response.UserMatchingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserInterestApiImpl implements UserInterestApi {

    private final UserInterestService userInterestService;
    private final MatchingServiceImpl matchingServiceImpl;

    @Override
    public ResponseEntity<List<UserInterestResponseJson>> getMyTags() {
        UUID userId = SecurityUtils.getCurrentUserIdOrThrow();

        List<UserInterestResponse> interests = userInterestService.getUserInterests(userId);

        List<UserInterestResponseJson> response = interests.stream()
                .map(UserInterestApiMapper::toJson)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<UserInterestResponseJson>> addMyTag(UserInterestCreateRequestJson request) {

        UUID userId = SecurityUtils.getCurrentUserIdOrThrow();

        List<UserInterestRequest> serviceRequests = UserInterestApiMapper.toServiceModel(userId, request);
        List<UserInterestResponse> created = userInterestService.addUserInterest(serviceRequests);

        List<UserInterestResponseJson> response = created.stream()
                .map(UserInterestApiMapper::toJson)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<String> deleteMyTag(UserInterestDeleteRequestJson request) {
        UUID userId = SecurityUtils.getCurrentUserIdOrThrow();

        UserInterestRequest serviceRequest = UserInterestApiMapper.toDeleteServiceModel(userId, request);
        userInterestService.removeUserInterest(serviceRequest);

        String message = String.format("Tag %s successfully removed from user %s", request.getTagId(), userId);
        return ResponseEntity.ok(message);
    }

    @Override
    public ResponseEntity<List<UserMatchingResponseJson>> getMatchUsers(
            Integer limit, Integer minMatchCount) {

        UUID userId = SecurityUtils.getCurrentUserIdOrThrow();

        List<UserMatchingResponse> matches = matchingServiceImpl.getMatchingUsers(
                userId, limit, minMatchCount);

        List<UserMatchingResponseJson> response = matches.stream()
                .map(UserInterestApiMapper::toUserMatchingResponseJson)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}