package com.tandem.chat_service.service;

import com.tandem.chat_service.service.model.request.GetMessagesFilter;
import com.tandem.chat_service.service.model.request.SendMessageRequest;
import com.tandem.chat_service.service.model.request.UpdateMessageRequest;
import com.tandem.chat_service.service.model.response.MessageResponse;
import com.tandem.chat_service.service.model.response.PaginatedMessagesResponse;

import java.util.UUID;

public interface MessageService {
    PaginatedMessagesResponse getMessages(GetMessagesFilter filter);
    MessageResponse sendMessage(UUID chatId, UUID senderId, SendMessageRequest request);
    MessageResponse updateMessage(UUID messageId, UUID requesterId, UpdateMessageRequest request);
    void deleteMessage(UUID messageId, UUID requesterId);
}