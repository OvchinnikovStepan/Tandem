package com.tandem.interest_service.service;

import com.tandem.interest_service.service.model.request.GroupInterestRequest;
import com.tandem.interest_service.service.model.response.GroupInterestResponse;

import java.util.List;
import java.util.UUID;

public interface GroupInterestService {
    List<GroupInterestResponse> addGroupInterest(List<GroupInterestRequest> requests);
    void removeGroupInterest(GroupInterestRequest request);
    List<GroupInterestResponse> getGroupInterests(UUID groupId);
    List<GroupInterestRequest> parseToGroupInterestRequest(String message);
}