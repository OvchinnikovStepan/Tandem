package com.tandem.interest_service.dal;

import com.tandem.interest_service.service.model.request.GroupInterestRequest;
import com.tandem.interest_service.service.model.response.GroupInterestResponse;
import com.tandem.interest_service.service.model.response.TagResponse;

import java.util.List;
import java.util.UUID;

public interface GroupTagDal {
    List<GroupInterestResponse> insert(List<GroupInterestRequest> requests);
    void delete(UUID id);
    List<GroupInterestResponse> getGroupInterests(UUID groupId);
    GroupInterestResponse getGroupInterest(UUID groupId, UUID tagId);
    TagResponse findTagByName(String name);
}