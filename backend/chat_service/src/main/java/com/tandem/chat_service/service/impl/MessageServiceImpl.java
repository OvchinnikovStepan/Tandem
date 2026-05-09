package com.tandem.chat_service.service.impl;

import com.tandem.chat_service.dal.MessageDal;
import com.tandem.chat_service.service.MessageService;
import com.tandem.chat_service.service.exception.ChatAccessDeniedException;
import com.tandem.chat_service.service.exception.MessageNotFoundException;
import com.tandem.chat_service.service.model.request.SendMessageRequest;
import com.tandem.chat_service.service.model.request.UpdateMessageRequest;
import com.tandem.chat_service.service.model.response.MessageResponse;
import com.tandem.chat_service.service.model.response.PaginatedMessagesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageDal messageDal;

    @Override
    public PaginatedMessagesResponse getMessages(UUID chatId, UUID requesterId, LocalDateTime before, LocalDateTime after, int limit) {
        log.info("Fetching messages for chat {} by user {}", chatId, requesterId);
        try {
            return messageDal.getMessagesByChatId(chatId, requesterId, before, after, limit);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("participant")) {
                throw new ChatAccessDeniedException(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public MessageResponse sendMessage(UUID chatId, UUID senderId, SendMessageRequest request) {
        log.info("User {} sending message to chat {}", senderId, chatId);
        return messageDal.sendMessage(chatId, senderId, request);
    }

    @Override
    public MessageResponse updateMessage(UUID messageId, UUID requesterId, UpdateMessageRequest request) {
        log.info("User {} updating message {}", requesterId, messageId);
        try {
            return messageDal.updateMessage(messageId, requesterId, request);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                throw new MessageNotFoundException(messageId);
            }
            throw new ChatAccessDeniedException(e.getMessage());
        }
    }

    @Override
    public void deleteMessage(UUID messageId, UUID requesterId) {
        log.info("User {} deleting message {}", requesterId, messageId);
        try {
            messageDal.deleteMessage(messageId, requesterId);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                throw new MessageNotFoundException(messageId);
            }
            throw new ChatAccessDeniedException(e.getMessage());
        }
    }
}