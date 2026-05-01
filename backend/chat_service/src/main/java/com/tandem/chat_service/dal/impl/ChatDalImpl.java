package com.tandem.chat_service.dal.impl;

import com.tandem.chat_service.dal.ChatDal;
import com.tandem.chat_service.dal.mapper.ChatEntityMapper;
import com.tandem.chat_service.dao.ChatDao;
import com.tandem.chat_service.dao.ChatParticipantDao;
import com.tandem.chat_service.dao.GroupDao;
import com.tandem.chat_service.dao.enums.GroupVisibility;
import com.tandem.chat_service.dao.model.ChatEntity;
import com.tandem.chat_service.dao.model.ChatParticipantEntity;
import com.tandem.chat_service.dao.model.GroupEntity;
import com.tandem.chat_service.integration.InterestEventPublisher;
import com.tandem.chat_service.service.model.request.CreateGroupChatRequest;
import com.tandem.chat_service.service.model.request.UpdateGroupRequest;
import com.tandem.chat_service.service.model.response.ChatResponse;
import com.tandem.chat_service.service.model.response.GroupDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatDalImpl implements ChatDal {

    private final ChatDao chatDao;
    private final GroupDao groupDao;
    private final ChatParticipantDao participantDao;
    private final InterestEventPublisher publisher;

    @Override
    public List<ChatResponse> getUserChats(UUID userId) {
        List<ChatResponse> responses = new ArrayList<>();

        // Личные чаты
        List<ChatEntity> personalChats = chatDao.findPersonalChatsByUser(userId);
        for (ChatEntity chat : personalChats) {
            responses.add(buildChatResponse(chat));
        }

        // Групповые чаты
        List<ChatEntity> groupChats = chatDao.findGroupChatsByUser(userId);
        for (ChatEntity chat : groupChats) {
            responses.add(buildChatResponse(chat));
        }

        return responses;
    }

    @Override
    public ChatResponse getChatById(UUID chatId) {
        ChatEntity chat = chatDao.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found with id: " + chatId));
        return buildChatResponse(chat);
    }

    @Override
    public ChatResponse getGroupChatByName(String name) {
        GroupEntity group = groupDao.findByName(name)
                .orElseThrow(() -> new RuntimeException("Group not found with name: " + name));

        ChatEntity chat = chatDao.findByGroupId(group.getId())
                .orElseThrow(() -> new RuntimeException("Chat not found for group: " + name));

        return buildChatResponse(chat);
    }

    @Override
    @Transactional
    public ChatResponse createPersonalChat(UUID initiatorId, UUID targetUserId) {
        ChatEntity chat = ChatEntityMapper.mapToChatEntity(null);
        chatDao.insert(chat);

        ChatParticipantEntity participant1 = ChatEntityMapper.mapToAdminParticipantEntity(chat.getId(), initiatorId);
        participantDao.insert(participant1);

        ChatParticipantEntity participant2 = ChatEntityMapper.mapToAdminParticipantEntity(chat.getId(), targetUserId);
        participantDao.insert(participant2);

        log.debug("Created personal chat with id {} between {} and {}", chat.getId(), initiatorId, targetUserId);
        return getChatById(chat.getId());
    }

    @Override
    @Transactional
    public ChatResponse createGroupChat(CreateGroupChatRequest request) {
        GroupEntity group = ChatEntityMapper.mapToGroupEntity(request);
        groupDao.insert(group);

        ChatEntity chat = ChatEntityMapper.mapToChatEntity(group.getId());
        chatDao.insert(chat);

        ChatParticipantEntity participant = ChatEntityMapper.mapToAdminParticipantEntity(chat.getId(), request.getCreatorId());
        participantDao.insert(participant);

        publisher.publishGroupCreated(group, request.getGroupInterests());

        log.debug("Created group chat with id {} for group {}", chat.getId(), group.getId());
        return getChatById(chat.getId());
    }

    @Override
    @Transactional
    public void leaveGroupChat(UUID chatId, UUID userId) {
        ChatEntity chat = chatDao.findById(chatId)
                .orElseThrow(() -> new  RuntimeException("Chat not found with id: " + chatId));

        if (chat.getGroupId() == null) {
            throw new RuntimeException("Cannot leave a personal chat. Use delete instead.");
        }

        if (!participantDao.isParticipant(chatId, userId)) {
            throw new RuntimeException("User is not a participant of this chat");
        }

        participantDao.deleteByChatIdAndUserId(chatId, userId);
        log.debug("User {} left group chat {}", userId, chatId);
    }
    @Override
    @Transactional
    public void deletePersonalChat(UUID chatId) {
        ChatEntity chat = chatDao.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found with id: " + chatId));

        if (chat.getGroupId() != null) {
            throw new RuntimeException("Cannot delete group chat using this method.");
        }

        chatDao.delete(chatId);
        log.debug("Deleted personal chat {}", chatId);
    }

    @Override
    public void muteChat(UUID chatId, UUID userId) {
        if (!participantDao.isParticipant(chatId, userId)) {
            throw new RuntimeException("User is not a participant of this chat");
        }
        participantDao.updateMutedStatus(chatId, userId, true);
        log.debug("User {} muted chat {}", userId, chatId);
    }

    @Override
    @Transactional
    public void kickUserFromGroupChat(UUID chatId, UUID targetUserId, UUID requesterId) {
        ChatEntity chat = chatDao.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        GroupEntity group = groupDao.findById(chat.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getCreatorId().equals(requesterId)) {
            throw new RuntimeException("Only the group creator can kick users");
        }

        participantDao.deleteByChatIdAndUserId(chatId, targetUserId);
        log.debug("Requester {} kicked user {} from chat {}", requesterId, targetUserId, chatId);
    }

    @Override
    @Transactional
    public GroupDto updateGroupSettings(UUID chatId, UUID requesterId, UpdateGroupRequest request) {
        ChatEntity chat = chatDao.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        GroupEntity group = groupDao.findById(chat.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getCreatorId().equals(requesterId)) {
            throw new RuntimeException("Only the group creator can update settings");
        }

        GroupEntity updatedGroup = ChatEntityMapper.mapToUpdatedGroupEntity(group, request);

        groupDao.update(updatedGroup);
        return ChatEntityMapper.mapToGroupDto(updatedGroup);
    }


    @Override
    public List<ChatResponse> searchGroupChatsByPrefix(String prefix, int limit) {
        List<GroupEntity> groups = groupDao.searchByNamePrefix(prefix, limit);
        List<ChatResponse> responses = new ArrayList<>();

        for (GroupEntity group : groups) {
            chatDao.findByGroupId(group.getId()).ifPresent(chat -> {
                responses.add(buildChatResponse(chat));
            });
        }
        return responses;
    }

    @Override
    @Transactional
    public void joinPublicGroupChat(UUID chatId, UUID userId) {
        ChatEntity chat = chatDao.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        GroupEntity group = groupDao.findById(chat.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Только в публичные чаты можно вступить напрямую
        if (group.getVisibility() != GroupVisibility.PUBLIC) {
            throw new RuntimeException("Cannot join a private group directly");
        }

        if (participantDao.isParticipant(chatId, userId)) {
            throw new RuntimeException("User is already a participant");
        }

        ChatParticipantEntity participant = ChatEntityMapper.mapToMemberParticipantEntity(chatId, userId);

        participantDao.insert(participant);
        log.debug("User {} joined public chat {}", userId, chatId);
    }

    /**
     * Собираем полный response для чата
     */
    private ChatResponse buildChatResponse(ChatEntity chat) {
        GroupEntity group = null;
        if (chat.getGroupId() != null) {
            group = groupDao.findById(chat.getGroupId()).orElse(null);
        }

        List<ChatParticipantEntity> participants = participantDao.findByChatId(chat.getId());

        return ChatEntityMapper.mapToChatResponse(chat, group, participants);
    }
}