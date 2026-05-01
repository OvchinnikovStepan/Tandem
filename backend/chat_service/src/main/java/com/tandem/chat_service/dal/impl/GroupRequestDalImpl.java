package com.tandem.chat_service.dal.impl;

import com.tandem.chat_service.dal.GroupRequestDal;
import com.tandem.chat_service.dal.mapper.GroupRequestEntityMapper;
import com.tandem.chat_service.dao.ChatDao;
import com.tandem.chat_service.dao.ChatParticipantDao;
import com.tandem.chat_service.dao.GroupDao;
import com.tandem.chat_service.dao.GroupRequestDao;
import com.tandem.chat_service.dao.enums.GroupRequestStatus;
import com.tandem.chat_service.dao.enums.GroupVisibility;
import com.tandem.chat_service.dao.model.ChatEntity;
import com.tandem.chat_service.dao.model.ChatParticipantEntity;
import com.tandem.chat_service.dao.model.GroupEntity;
import com.tandem.chat_service.dao.model.GroupRequestEntity;
import com.tandem.chat_service.service.model.response.GroupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GroupRequestDalImpl implements GroupRequestDal {

    private final GroupRequestDao requestDao;
    private final GroupDao groupDao;
    private final ChatDao chatDao;
    private final ChatParticipantDao participantDao;

    @Override
    @Transactional
    public GroupRequestDto createRequest(UUID groupId, UUID userId, String message) {
        GroupEntity group = groupDao.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (group.getVisibility() == GroupVisibility.PUBLIC) {
            throw new RuntimeException("Group is public. Join directly instead of requesting.");
        }

        ChatEntity chat = chatDao.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Chat not found for this group"));

        if (participantDao.isParticipant(chat.getId(), userId)) {
            throw new RuntimeException("User is already a participant");
        }

        GroupRequestEntity entity = GroupRequestEntityMapper.toEntity(groupId, userId, group.getCreatorId(), message);

        requestDao.insert(entity);
        return GroupRequestEntityMapper.toDto(entity);
    }

    @Override
    @Transactional
    public void approveRequest(UUID requestId, UUID reviewerId) {
        GroupRequestEntity request = requestDao.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        ChatEntity chat = chatDao.findByGroupId(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        if (participantDao.isParticipant(chat.getId(), request.getUserId())) {
            throw new RuntimeException("User is already a participant");
        }


        verifyReviewerIsGroupCreator(request.getGroupId(), reviewerId);

        if (request.getStatus() != GroupRequestStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be approved");
        }

        if (request.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Request has expired");
        }

        requestDao.updateStatus(requestId, GroupRequestStatus.APPROVED, LocalDateTime.now(), reviewerId);

        ChatParticipantEntity participant = GroupRequestEntityMapper.toParticipantEntity(
                chat.getId(), request.getUserId());
        participantDao.insert(participant);
    }

    @Override
    public void rejectRequest(UUID requestId, UUID reviewerId) {
        GroupRequestEntity request = requestDao.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        verifyReviewerIsGroupCreator(request.getGroupId(), reviewerId);
        requestDao.updateStatus(requestId, GroupRequestStatus.REJECTED, LocalDateTime.now(), reviewerId);
    }

    @Override
    public void cancelRequest(UUID requestId, UUID userId) {
        GroupRequestEntity request = requestDao.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getUserId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own requests");
        }

        requestDao.updateStatus(requestId, GroupRequestStatus.CANCELLED, LocalDateTime.now(), userId);
    }

    @Override
    public List<GroupRequestDto> getPendingRequestsForGroup(UUID groupId, UUID requesterId) {
        verifyReviewerIsGroupCreator(groupId, requesterId);
        return requestDao.findPendingByGroupId(groupId).stream()
                .map(GroupRequestEntityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupRequestDto> getMyRequests(UUID userId) {
        return requestDao.findByUserId(userId).stream()
                .map(GroupRequestEntityMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Проверяет, что пользователь создатель
     */
    private void verifyReviewerIsGroupCreator(UUID groupId, UUID reviewerId) {
        GroupEntity group = groupDao.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!group.getCreatorId().equals(reviewerId)) {
            throw new RuntimeException("Only group creator can perform this action");
        }
    }
}