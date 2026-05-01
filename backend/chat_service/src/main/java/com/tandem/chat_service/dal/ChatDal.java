package com.tandem.chat_service.dal;

import com.tandem.chat_service.service.model.request.CreateGroupChatRequest;
import com.tandem.chat_service.service.model.request.UpdateGroupRequest;
import com.tandem.chat_service.service.model.response.ChatResponse;
import com.tandem.chat_service.service.model.response.GroupDto;

import java.util.List;
import java.util.UUID;

public interface ChatDal {
    List<ChatResponse> getUserChats(UUID userId);
    ChatResponse getChatById(UUID chatId);
    ChatResponse getGroupChatByName(String name);
    ChatResponse createPersonalChat(UUID initiatorId, UUID targetUserId);
    ChatResponse createGroupChat(CreateGroupChatRequest request);
    void leaveGroupChat(UUID chatId, UUID userId);
    void deletePersonalChat(UUID chatId);
    void muteChat(UUID chatId, UUID userId);
    void kickUserFromGroupChat(UUID chatId, UUID targetUserId, UUID requesterId);
    GroupDto updateGroupSettings(UUID chatId, UUID requesterId, UpdateGroupRequest request);
    List<ChatResponse> searchGroupChatsByPrefix(String prefix, int limit);
    void joinPublicGroupChat(UUID chatId, UUID userId);
}