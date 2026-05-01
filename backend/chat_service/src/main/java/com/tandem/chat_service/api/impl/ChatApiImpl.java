package com.tandem.chat_service.api.impl;

import com.tandem.chat_service.api.ChatApi;
import com.tandem.chat_service.api.mapper.ChatApiMapper;
import com.tandem.chat_service.api.model.request.CreateGroupChatRequestJson;
import com.tandem.chat_service.api.model.request.CreatePersonalChatRequestJson;
import com.tandem.chat_service.api.model.request.UpdateGroupRequestJson;
import com.tandem.chat_service.api.model.response.ChatResponseJson;
import com.tandem.chat_service.api.model.response.GroupDtoJson;
import com.tandem.chat_service.service.ChatService;
import com.tandem.chat_service.service.model.request.CreateGroupChatRequest;
import com.tandem.chat_service.service.model.request.UpdateGroupRequest;
import com.tandem.chat_service.service.model.response.ChatResponse;
import com.tandem.chat_service.service.model.response.GroupDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ChatApiImpl implements ChatApi {

    private final ChatService chatService;

    @Override
    public ResponseEntity<List<ChatResponseJson>> getMyChats() {
        UUID currentUserId = getCurrentUserId();

        List<ChatResponse> chats = chatService.getUserChats(currentUserId);
        List<ChatResponseJson> response = chats.stream()
                .map(ChatApiMapper::toJson)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ChatResponseJson> getChatById(UUID id) {
        ChatResponse chat = chatService.getChatById(id);
        return ResponseEntity.ok(ChatApiMapper.toJson(chat));
    }

    @Override
    public ResponseEntity<ChatResponseJson> getGroupChatByName(String name) {
        ChatResponse chat = chatService.getGroupChatByName(name);
        return ResponseEntity.ok(ChatApiMapper.toJson(chat));
    }

    @Override
    public ResponseEntity<ChatResponseJson> createPersonalChat(CreatePersonalChatRequestJson request) {
        UUID currentUserId = getCurrentUserId();

        ChatResponse createdChat = chatService.createPersonalChat(currentUserId, request.getTargetUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ChatApiMapper.toJson(createdChat));
    }

    @Override
    public ResponseEntity<ChatResponseJson> createGroupChat(CreateGroupChatRequestJson request) {
        UUID currentUserId = getCurrentUserId();

        CreateGroupChatRequest serviceRequest = ChatApiMapper.toServiceModel(currentUserId, request);
        ChatResponse createdChat = chatService.createGroupChat(serviceRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(ChatApiMapper.toJson(createdChat));
    }

    @Override
    public ResponseEntity<String> leaveGroupChat(UUID id) {
        UUID currentUserId = getCurrentUserId();

        chatService.leaveGroupChat(id, currentUserId);

        String message = String.format("Пользователь %s вышел из чата %s",currentUserId, id);
        return ResponseEntity.ok(message);
    }

    @Override
    public ResponseEntity<String> deletePersonalChat(UUID id) {
        chatService.deletePersonalChat(id);

        String message = String.format("Личный чат %s удален", id);
        return ResponseEntity.ok(message);
    }

    @Override
    public ResponseEntity<String> muteChat(UUID id) {
        UUID currentUserId = getCurrentUserId();
        chatService.muteChat(id, currentUserId);
        String message = String.format("Чат %s переведен в беззвучный режим", id);
        return ResponseEntity.ok(message);
    }

    @Override
    public ResponseEntity<String> kickParticipant(UUID chatId, UUID userId) {
        UUID currentUserId = getCurrentUserId();
        chatService.kickUserFromGroupChat(chatId, userId, currentUserId);
        String message = String.format("Пользователь %s исключен из чата %s",userId, chatId);
        return ResponseEntity.ok(message);
    }

    @Override
    public ResponseEntity<GroupDtoJson> updateGroupSettings(UUID chatId, UpdateGroupRequestJson request) {
        UUID currentUserId = getCurrentUserId();

        UpdateGroupRequest serviceRequest = ChatApiMapper.toUpdateServiceModel(request);
        GroupDto updatedGroup = chatService.updateGroupSettings(chatId, currentUserId, serviceRequest);

        return ResponseEntity.ok(ChatApiMapper.mapGroupToJson(updatedGroup));
    }

    @Override
    public ResponseEntity<List<ChatResponseJson>> searchGroupChats(String prefix, int limit) {
        List<ChatResponse> chats = chatService.searchGroupChatsByPrefix(prefix, limit);
        List<ChatResponseJson> response = chats.stream()
                .map(ChatApiMapper::toJson)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> joinPublicChat(UUID id) {
        UUID currentUserId = getCurrentUserId();
        chatService.joinPublicGroupChat(id, currentUserId);
        String message = String.format("Пользователь %s вступил в чат %s", currentUserId, id);
        return ResponseEntity.ok(message);
    }

    private UUID getCurrentUserId() {
        // Для тестирования (будет заменено на извлечение токена через Security)
        return UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    }
}