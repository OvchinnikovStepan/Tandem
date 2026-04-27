package com.tandem.chat_service.api;

import com.tandem.chat_service.api.model.request.CreateGroupChatRequestJson;
import com.tandem.chat_service.api.model.request.CreatePersonalChatRequestJson;
import com.tandem.chat_service.api.model.request.UpdateGroupRequestJson;
import com.tandem.chat_service.api.model.response.ChatResponseJson;
import com.tandem.chat_service.api.model.response.GroupDtoJson;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;
import java.util.UUID;

@RequestMapping("/api/chats")
public interface ChatApi {

    @Operation(summary = "Получить все чаты пользователя")
    @GetMapping
    ResponseEntity<List<ChatResponseJson>> getMyChats();

    @Operation(summary = "Получить чат по ID")
    @GetMapping("/{id}")
    ResponseEntity<ChatResponseJson> getChatById(@PathVariable UUID id);

    @Operation(summary = "Получить групповой чат по названию")
    @GetMapping("/group/search")
    ResponseEntity<ChatResponseJson> getGroupChatByName(@RequestParam String name);

    @Operation(summary = "Создать личный чат")
    @PostMapping("/personal")
    ResponseEntity<ChatResponseJson> createPersonalChat(@Valid @RequestBody CreatePersonalChatRequestJson request);

    @Operation(summary = "Создать групповой чат")
    @PostMapping("/group")
    ResponseEntity<ChatResponseJson> createGroupChat(@Valid @RequestBody CreateGroupChatRequestJson request);

    @Operation(summary = "Выйти из группового чата")
    @PostMapping("/group/{id}/leave")
    ResponseEntity<String> leaveGroupChat(@PathVariable UUID id);

    @Operation(summary = "Удалить личный чат")
    @DeleteMapping("/personal/{id}")
    ResponseEntity<String> deletePersonalChat(@PathVariable UUID id);

    @Operation(summary = "Замутить чат")
    @PutMapping("/{id}/mute")
    ResponseEntity<String> muteChat(@PathVariable UUID id);

    @Operation(summary = "Исключить пользователя из группового чата (для владельцев)")
    @DeleteMapping("/{chatId}/participants/{userId}")
    ResponseEntity<String> kickParticipant(@PathVariable UUID chatId, @PathVariable UUID userId);

    @Operation(summary = "Обновить настройки группы (для владельцев)")
    @PutMapping("/{chatId}/settings")
    ResponseEntity<GroupDtoJson> updateGroupSettings(
            @PathVariable UUID chatId,
            @Valid @RequestBody UpdateGroupRequestJson request
    );

    @Operation(summary = "Поиск групповых чатов по началу названия")
    @GetMapping("/group/search-prefix")
    ResponseEntity<List<ChatResponseJson>> searchGroupChats(
            @RequestParam String prefix,
            @RequestParam(defaultValue = "10") int limit
    );

    @Operation(summary = "Вступить в публичный чат")
    @PostMapping("/{id}/join")
    ResponseEntity<String> joinPublicChat(@PathVariable UUID id);
}
