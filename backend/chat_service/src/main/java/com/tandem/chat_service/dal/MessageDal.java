package com.tandem.chat_service.dal;

import com.tandem.chat_service.service.model.request.SendMessageRequest;
import com.tandem.chat_service.service.model.request.UpdateMessageRequest;
import com.tandem.chat_service.service.model.response.MessageResponse;
import com.tandem.chat_service.service.model.response.PaginatedMessagesResponse;
import java.time.LocalDateTime;
import java.util.UUID;

public interface MessageDal {
    PaginatedMessagesResponse getMessagesByChatId(UUID chatId, UUID requesterId, LocalDateTime before, LocalDateTime after, int limit);
    MessageResponse sendMessage(UUID chatId, UUID senderId, SendMessageRequest request);
    MessageResponse updateMessage(UUID messageId, UUID requesterId, UpdateMessageRequest request);
    void deleteMessage(UUID messageId, UUID requesterId);
}
