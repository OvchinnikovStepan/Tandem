package com.tandem.interest_service.dal;

import com.tandem.interest_service.service.model.request.UserInterestRequest;
import com.tandem.interest_service.service.model.response.TagResponse;
import com.tandem.interest_service.service.model.response.UserInterestResponse;

import java.util.List;
import java.util.UUID;

public interface UserInterestDal {
    List<UserInterestResponse> insert(List<UserInterestRequest> requests);
    void delete(UUID id);
    List<UserInterestResponse> getUserInterests(UUID userId);
    UserInterestResponse getUserInterest(UUID userId, UUID tagId);
    TagResponse findTagByName(String name);
}
