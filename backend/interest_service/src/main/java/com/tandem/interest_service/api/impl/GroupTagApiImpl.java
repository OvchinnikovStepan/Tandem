package com.tandem.interest_service.api.impl;

import com.tandem.interest_service.api.GroupTagApi;
import com.tandem.interest_service.api.mapper.GroupTagApiMapper;
import com.tandem.interest_service.api.model.request.GroupInterestCreateRequestJson;
import com.tandem.interest_service.api.model.response.GroupInterestResponseJson;
import com.tandem.interest_service.service.GroupInterestService;
import com.tandem.interest_service.service.model.request.GroupInterestRequest;
import com.tandem.interest_service.service.model.response.GroupInterestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class GroupTagApiImpl implements GroupTagApi {

    private final GroupInterestService groupInterestService;

    @Override
    public ResponseEntity<List<GroupInterestResponseJson>> getGroupTags(UUID groupId) {
        List<GroupInterestResponse> responses = groupInterestService.getGroupInterests(groupId);

        List<GroupInterestResponseJson> jsonResponses = responses.stream()
                .map(GroupTagApiMapper::toJson)
                .collect(Collectors.toList());

        return ResponseEntity.ok(jsonResponses);
    }

    @Override
    public ResponseEntity<List<GroupInterestResponseJson>> addGroupTags(UUID groupId, GroupInterestCreateRequestJson request) {
        List<GroupInterestRequest> serviceRequests = GroupTagApiMapper.toServiceModel(groupId, request);

        List<GroupInterestResponse> responses = groupInterestService.addGroupInterest(serviceRequests);

        List<GroupInterestResponseJson> jsonResponses = responses.stream()
                .map(GroupTagApiMapper::toJson)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(jsonResponses);
    }

    @Override
    public ResponseEntity<Void> deleteGroupTag(UUID groupId, UUID tagId) {
        GroupInterestRequest serviceRequest = GroupInterestRequest.builder()
                .groupId(groupId)
                .tagId(tagId)
                .build();

        groupInterestService.removeGroupInterest(serviceRequest);

        return ResponseEntity.noContent().build();
    }
}