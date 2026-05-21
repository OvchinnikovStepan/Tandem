package com.tandem.interest_service.service.impl;

import com.tandem.interest_service.dal.DirectoryDal;
import com.tandem.interest_service.integration.model.GroupCreatedEvent;
import com.tandem.interest_service.integration.model.OnboardingCompletedEvent;
import com.tandem.interest_service.service.DirectoryService;
import com.tandem.interest_service.service.model.response.GroupSearchResponse;
import com.tandem.interest_service.service.model.response.UserSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 50;
    private static final int MAX_NAME_LENGTH = 255;

    private final DirectoryDal directoryDal;

    @Override
    public void syncUserFromOnboarding(OnboardingCompletedEvent event) {
        try {
            UUID userId = event.getUserIdAsUUID();
            event.resolveUsername()
                    .map(this::truncateName)
                    .filter(s -> !s.isBlank())
                    .ifPresent(name -> directoryDal.upsertUser(userId, name));
        } catch (Exception e) {
            log.warn("Skipping user directory sync: {}", e.getMessage());
        }
    }

    @Override
    public void syncGroupFromEvent(GroupCreatedEvent event) {
        try {
            UUID groupId = event.getGroupIdAsUUID();
            if (event.getGroupName() == null || event.getGroupName().isBlank()) {
                return;
            }
            String name = truncateName(event.getGroupName().trim());
            directoryDal.upsertGroup(groupId, name);
        } catch (Exception e) {
            log.warn("Skipping group directory sync: {}", e.getMessage());
        }
    }

    @Override
    public List<UserSearchResponse> searchUsersByNameFragment(String fragment, int limit) {
        String trimmed = fragment == null ? "" : fragment.trim();
        if (trimmed.isEmpty()) {
            return List.of();
        }

        int effectiveLimit = normalizeLimit(limit);
        return directoryDal.searchUsersByNameFragment(trimmed, effectiveLimit);
    }

    @Override
    public List<GroupSearchResponse> searchGroupsByNameFragment(String fragment, int limit) {
        String trimmed = fragment == null ? "" : fragment.trim();
        if (trimmed.isEmpty()) {
            return List.of();
        }

        int effectiveLimit = normalizeLimit(limit);
        return directoryDal.searchGroupsByNameFragment(trimmed, effectiveLimit);
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private String truncateName(String name) {
        if (name.length() <= MAX_NAME_LENGTH) {
            return name;
        }
        return name.substring(0, MAX_NAME_LENGTH);
    }
}
