package com.tandem.interest_service.api.impl;

import com.tandem.interest_service.api.MatchingApi;
import com.tandem.interest_service.api.mapper.UserInterestApiMapper;
import com.tandem.interest_service.api.model.response.UserMatchingResponseJson;
import com.tandem.interest_service.security.SecurityUtils;
import com.tandem.interest_service.service.MatchingService;
import com.tandem.interest_service.service.model.response.UserMatchingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MatchingApiImpl implements MatchingApi {
    private final MatchingService matchingService;

    @Override
    public ResponseEntity<List<UserMatchingResponseJson>> getMatchUsers(
            Integer limit, Integer minMatchCount) {

        UUID userId = SecurityUtils.getCurrentUserIdOrThrow();

        List<UserMatchingResponse> matches = matchingService.getMatchingUsers(
                userId, limit, minMatchCount);

        List<UserMatchingResponseJson> response = matches.stream()
                .map(UserInterestApiMapper::toUserMatchingResponseJson)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
