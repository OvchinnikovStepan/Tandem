package com.tandem.interest_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.interest_service.dal.GroupTagDal;
import com.tandem.interest_service.integration.model.GroupCreatedEvent;
import com.tandem.interest_service.service.GroupInterestService;
import com.tandem.interest_service.service.exception.GroupInterestNotFoundException;
import com.tandem.interest_service.service.model.request.GroupInterestRequest;
import com.tandem.interest_service.service.model.response.GroupInterestResponse;
import com.tandem.interest_service.service.model.response.TagResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupInterestServiceImpl implements GroupInterestService {

    private final GroupTagDal groupTagDal;
    private final ObjectMapper objectMapper;

    @Override
    public List<GroupInterestResponse> addGroupInterest(List<GroupInterestRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            log.info("No interests to add - empty request list");
            return List.of();
        }

        for (GroupInterestRequest request : requests) {
            if (request.getGroupId() == null || request.getTagId() == null) {
                throw new IllegalArgumentException("Group ID and Tag ID cannot be null");
            }
        }

        List<GroupInterestResponse> responses = groupTagDal.insert(requests);

        log.info("Successfully added {} interests for group: {}", responses.size(), requests.get(0).getGroupId());

        return responses;
    }

    @Override
    public void removeGroupInterest(GroupInterestRequest request) {
        log.info("Removing group interest with tag_id: {} for group: {}",
                request.getTagId(), request.getGroupId());

        if (request.getGroupId() == null || request.getTagId() == null) {
            throw new IllegalArgumentException("Group ID and Tag ID cannot be null");
        }

        try {
            GroupInterestResponse interest = groupTagDal.getGroupInterest(
                    request.getGroupId(), request.getTagId()
            );
            groupTagDal.delete(interest.getId());

            log.info("Successfully removed interest for group: {} with tag: {}",
                    request.getGroupId(), request.getTagId());

        } catch (Exception e) {
            throw new GroupInterestNotFoundException(request.getGroupId(), request.getTagId());
        }
    }

    @Override
    public List<GroupInterestResponse> getGroupInterests(UUID groupId) {

        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }

        List<GroupInterestResponse> interests = groupTagDal.getGroupInterests(groupId);

        log.info("Found {} interests for group: {}", interests.size(), groupId);
        return interests;
    }

    @Override
    public List<GroupInterestRequest> parseToGroupInterestRequest(String message) {
        try {
            GroupCreatedEvent event = objectMapper.readValue(message, GroupCreatedEvent.class);

            UUID groupId;
            try {
                groupId = event.getGroupIdAsUUID();
            } catch (Exception e) {
                log.error("Invalid UUID format in message: {}", event.getGroupIdAsUUID());
                return List.of();
            }

            List<String> interests = event.getInterests();

            if (interests.isEmpty()) {
                return List.of();
            }

            List<TagResponse> tags = new ArrayList<>();
            for (String interest : interests) {
                try {
                    TagResponse tag = groupTagDal.findTagByName(interest);
                    if (tag != null) {
                        tags.add(tag);
                    } else {
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
                    .map(tag -> GroupInterestRequest.builder()
                            .groupId(groupId)
                            .tagId(tag.getId())
                            .build())
                    .toList();

        } catch (Exception e) {
            return List.of();
        }
    }
}
