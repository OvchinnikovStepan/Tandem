package com.tandem.chat_service.service.impl;

import com.tandem.chat_service.dal.GroupRequestDal;
import com.tandem.chat_service.service.GroupRequestService;
import com.tandem.chat_service.service.exception.ChatAccessDeniedException;
import com.tandem.chat_service.service.exception.GroupNotFoundException;
import com.tandem.chat_service.service.exception.GroupRequestNotFoundException;
import com.tandem.chat_service.service.exception.InvalidChatOperationException;
import com.tandem.chat_service.service.model.response.GroupRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupRequestServiceImpl implements GroupRequestService {

    private final GroupRequestDal requestDal;

    @Override
    public GroupRequestDto createRequest(UUID groupId, UUID userId, String message) {
        log.info("User {} requesting to join group {}", userId, groupId);
        try {
            return requestDal.createRequest(groupId, userId, message);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Group not found")) {
                throw new GroupNotFoundException(groupId);
            }
            throw new InvalidChatOperationException(e.getMessage());
        }
    }

    @Override
    public void approveRequest(UUID requestId, UUID reviewerId) {
        log.info("Reviewer {} is approving request {}", reviewerId, requestId);
        try {
            requestDal.approveRequest(requestId, reviewerId);
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (e.getMessage().contains("Request not found")) {
                throw new GroupRequestNotFoundException(requestId);
            }
            throw new InvalidChatOperationException(msg);
        }
    }

    @Override
    public void rejectRequest(UUID requestId, UUID reviewerId) {
        log.info("Reviewer {} is rejecting request {}", reviewerId, requestId);
        try {
            requestDal.rejectRequest(requestId, reviewerId);
        } catch (RuntimeException e) {
            throw new GroupRequestNotFoundException(requestId);
        }
    }

    @Override
    public void cancelRequest(UUID requestId, UUID userId) {
        log.info("User {} is cancelling their request {}", userId, requestId);
        try {
            requestDal.cancelRequest(requestId, userId);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("your own")) {
                throw new ChatAccessDeniedException(e.getMessage());
            }
            throw new GroupRequestNotFoundException(requestId);
        }
    }

    @Override
    public List<GroupRequestDto> getPendingRequestsForGroup(UUID groupId, UUID requesterId) {
        log.info("Requester {} fetching pending requests for group {}", requesterId, groupId);
        try {
            return requestDal.getPendingRequestsForGroup(groupId, requesterId);
        } catch (RuntimeException e) {
            throw new GroupNotFoundException(groupId);
        }
    }

    @Override
    public List<GroupRequestDto> getMyRequests(UUID userId) {
        log.info("Fetching join requests created by user {}", userId);
        return requestDal.getMyRequests(userId);
    }
}
