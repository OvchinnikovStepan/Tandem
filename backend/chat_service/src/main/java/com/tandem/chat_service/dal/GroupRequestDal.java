package com.tandem.chat_service.dal;

import com.tandem.chat_service.service.model.response.GroupRequestDto;

import java.util.List;
import java.util.UUID;

public interface GroupRequestDal {
    GroupRequestDto createRequest(UUID groupId, UUID userId, String message);
    void approveRequest(UUID requestId, UUID reviewerId);
    void rejectRequest(UUID requestId, UUID reviewerId);
    void cancelRequest(UUID requestId, UUID userId);
    List<GroupRequestDto> getPendingRequestsForGroup(UUID groupId, UUID requesterId);
    List<GroupRequestDto> getMyRequests(UUID userId);
}
