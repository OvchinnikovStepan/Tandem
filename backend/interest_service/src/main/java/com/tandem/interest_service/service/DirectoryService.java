package com.tandem.interest_service.service;

import com.tandem.interest_service.integration.model.GroupCreatedEvent;
import com.tandem.interest_service.integration.model.OnboardingCompletedEvent;
import com.tandem.interest_service.service.model.response.GroupSearchResponse;
import com.tandem.interest_service.service.model.response.UserSearchResponse;

import java.util.List;

public interface DirectoryService {

    void syncUserFromOnboarding(OnboardingCompletedEvent event);

    void syncGroupFromEvent(GroupCreatedEvent event);

    List<UserSearchResponse> searchUsersByNameFragment(String fragment, int limit);

    List<GroupSearchResponse> searchGroupsByNameFragment(String fragment, int limit);
}
