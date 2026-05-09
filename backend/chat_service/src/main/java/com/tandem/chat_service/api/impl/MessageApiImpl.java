package com.tandem.chat_service.api.impl;

import com.tandem.chat_service.api.MessageApi;
import com.tandem.chat_service.api.mapper.MessageApiMapper;
import com.tandem.chat_service.api.model.request.SendMessageRequestJson;
import com.tandem.chat_service.api.model.request.UpdateMessageRequestJson;
import com.tandem.chat_service.api.model.response.MessageResponseJson;
import com.tandem.chat_service.api.model.response.PaginatedMessagesResponseJson;
import com.tandem.chat_service.security.SecurityUtils;
import com.tandem.chat_service.service.MessageService;
import com.tandem.chat_service.service.model.response.MessageResponse;
import com.tandem.chat_service.service.model.response.PaginatedMessagesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageApiImpl implements MessageApi {

    private final MessageService messageService;
    private final MessageApiMapper apiMapper;

    @Override
    public ResponseEntity<PaginatedMessagesResponseJson> getMessages(UUID chatId, int limit, LocalDate before, LocalDate after) {
        UUID currentUserId = SecurityUtils.getCurrentUserIdOrThrow();

        LocalDateTime beforeDateTime = (before != null) ? before.atStartOfDay() : null;
        LocalDateTime afterDateTime = (after != null) ? after.atStartOfDay() : null;

        PaginatedMessagesResponse result = messageService.getMessages(
                chatId,
                currentUserId,
                beforeDateTime,
                afterDateTime,
                limit
        );

        return ResponseEntity.ok(apiMapper.toPaginatedJson(result));
    }

    @Override
    public ResponseEntity<MessageResponseJson> sendMessage(UUID chatId, SendMessageRequestJson request) {
        UUID currentUserId = SecurityUtils.getCurrentUserIdOrThrow();
        MessageResponse response = messageService.sendMessage(chatId, currentUserId, apiMapper.toServiceModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(apiMapper.toJson(response));
    }

    @Override
    public ResponseEntity<MessageResponseJson> updateMessage(UUID messageId, UpdateMessageRequestJson request) {
        UUID currentUserId = SecurityUtils.getCurrentUserIdOrThrow();
        MessageResponse response = messageService.updateMessage(messageId, currentUserId, apiMapper.toUpdateServiceModel(request));
        return ResponseEntity.ok(apiMapper.toJson(response));
    }

    @Override
    public ResponseEntity<Void> deleteMessage(UUID messageId) {
        UUID currentUserId = SecurityUtils.getCurrentUserIdOrThrow();
        messageService.deleteMessage(messageId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}