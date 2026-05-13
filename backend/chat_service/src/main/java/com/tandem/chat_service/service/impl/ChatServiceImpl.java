package com.tandem.chat_service.service.impl;

import com.tandem.chat_service.dal.ChatDal;
import com.tandem.chat_service.service.ChatService;
import com.tandem.chat_service.service.exception.ChatAccessDeniedException;
import com.tandem.chat_service.service.exception.ChatNotFoundException;
import com.tandem.chat_service.service.exception.GroupNotFoundException;
import com.tandem.chat_service.service.exception.InvalidChatOperationException;
import com.tandem.chat_service.service.model.request.CreateGroupChatRequest;
import com.tandem.chat_service.service.model.request.UpdateGroupRequest;
import com.tandem.chat_service.service.model.response.ChatResponse;
import com.tandem.chat_service.service.model.response.GroupDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatDal chatDal;

    @Override
    public List<ChatResponse> getUserChats(UUID userId) {
        log.info("Fetching all chats for user: {}", userId);
        return chatDal.getUserChats(userId);
    }

    @Override
    public ChatResponse getChatById(UUID chatId) {
        log.info("Fetching chat by id: {}", chatId);
        try {
            return chatDal.getChatById(chatId);
        } catch (RuntimeException e) {
            throw new ChatNotFoundException(chatId);
        }
    }

    @Override
    public ChatResponse getGroupChatByName(String name) {
        log.info("Fetching group chat by name: {}", name);
        try {
            return chatDal.getGroupChatByName(name);
        } catch (RuntimeException e) {
            throw new GroupNotFoundException(name);
        }
    }

    @Override
    public ChatResponse createPersonalChat(UUID initiatorId, UUID targetUserId) {
        log.info("Creating personal chat between initiator {} and target {}", initiatorId, targetUserId);

        if (initiatorId.equals(targetUserId)) {
            throw new InvalidChatOperationException("Cannot create a personal chat with yourself");
        }

        ChatResponse response = chatDal.createPersonalChat(initiatorId, targetUserId);
        chatDal.publishPersonalChatCreatedEvent(response.getId(), List.of(initiatorId, targetUserId));

        return response;
    }

    @Override
    public ChatResponse createGroupChat(CreateGroupChatRequest request) {
        log.info("Creating group chat with name: {} by user: {}", request.getName(), request.getCreatorId());

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new InvalidChatOperationException("Group chat name cannot be empty");
        }

        ChatResponse response = chatDal.createGroupChat(request);

        chatDal.publishGroupCreatedEvent(response.getGroup().getId(), request.getGroupInterests());

        return response;
    }

    @Override
    public void leaveGroupChat(UUID chatId, UUID userId) {
        log.info("User {} is leaving group chat {}", userId, chatId);
        try {
            chatDal.leaveGroupChat(chatId, userId);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Cannot leave a personal chat")) {
                throw new InvalidChatOperationException(e.getMessage());
            }
            throw new ChatNotFoundException(chatId);
        }
    }

    @Override
    public void deletePersonalChat(UUID chatId) {
        log.info("Deleting personal chat {}", chatId);
        try {
            chatDal.deletePersonalChat(chatId);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Cannot delete group chat")) {
                throw new InvalidChatOperationException(e.getMessage());
            }
            throw new ChatNotFoundException(chatId);
        }
    }

    @Override
    public void muteChat(UUID chatId, UUID userId) {
        log.info("Muting chat {} for user {}", chatId, userId);
        try {
            chatDal.muteChat(chatId, userId);
        } catch (RuntimeException e) {
            throw new InvalidChatOperationException(e.getMessage());
        }
    }

    @Override
    public void kickUserFromGroupChat(UUID chatId, UUID targetUserId, UUID requesterId) {
        log.info("Attempting to kick user {} from chat {} by requester {}", targetUserId, chatId, requesterId);
        if (targetUserId.equals(requesterId)) {
            throw new InvalidChatOperationException("You cannot kick yourself.");
        }

        try {
            chatDal.kickUserFromGroupChat(chatId, targetUserId, requesterId);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("creator")) {
                throw new ChatAccessDeniedException(e.getMessage());
            } else if (e.getMessage().contains("Group")) {
                throw new InvalidChatOperationException(e.getMessage());
            }
            throw new ChatNotFoundException(chatId);
        }
    }

    @Override
    public GroupDto updateGroupSettings(UUID chatId, UUID requesterId, UpdateGroupRequest request) {
        log.info("Updating settings for group chat {} by requester {}", chatId, requesterId);
        try {
            return chatDal.updateGroupSettings(chatId, requesterId, request);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("creator")) {
                throw new ChatAccessDeniedException(e.getMessage());
            } else if (e.getMessage().contains("Group")) {
                throw new InvalidChatOperationException(e.getMessage());
            }
            throw new ChatNotFoundException(chatId);
        }
    }

    @Override
    public List<ChatResponse> searchGroupChatsByPrefix(String prefix, int limit) {
        log.info("Searching group chats by prefix: {}", prefix);
        return chatDal.searchGroupChatsByPrefix(prefix, limit);
    }

    @Override
    public void joinPublicGroupChat(UUID chatId, UUID userId) {
        log.info("User {} is joining public chat {}", userId, chatId);
        try {
            chatDal.joinPublicGroupChat(chatId, userId);
            chatDal.publishUserJoinedGroupEvent(chatId, userId);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Chat not found")) {
                throw new ChatNotFoundException(chatId);
            }
            throw new InvalidChatOperationException(e.getMessage());
        }
    }
}
