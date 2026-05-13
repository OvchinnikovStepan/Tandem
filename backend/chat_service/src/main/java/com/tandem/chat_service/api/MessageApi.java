package com.tandem.chat_service.api;

import com.tandem.chat_service.api.model.request.SendMessageRequestJson;
import com.tandem.chat_service.api.model.request.UpdateMessageRequestJson;
import com.tandem.chat_service.api.model.response.MessageResponseJson;
import com.tandem.chat_service.api.model.response.PaginatedMessagesResponseJson;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RequestMapping("/api/chat")
public interface MessageApi {

    @Operation(summary = "Получить сообщения из чата")
    @GetMapping("/chats/{chatId}/messages")
    ResponseEntity<PaginatedMessagesResponseJson> getMessages(
            @PathVariable UUID chatId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after
    );

    @Operation(summary = "Отправить сообщение в чат")
    @PostMapping("/chats/{chatId}/messages")
    ResponseEntity<MessageResponseJson> sendMessage(
            @PathVariable UUID chatId,
            @Valid @RequestBody SendMessageRequestJson request
    );

    @Operation(summary = "Редактировать сообщение")
    @PutMapping("/messages/{messageId}")
    ResponseEntity<MessageResponseJson> updateMessage(
            @PathVariable UUID messageId,
            @Valid @RequestBody UpdateMessageRequestJson request
    );

    @Operation(summary = "Удалить сообщение")
    @DeleteMapping("/messages/{messageId}")
    ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId);
}