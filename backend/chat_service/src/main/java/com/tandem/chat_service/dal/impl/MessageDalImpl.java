package com.tandem.chat_service.dal.impl;

import com.tandem.chat_service.dal.MessageDal;
import com.tandem.chat_service.dal.mapper.MessageEntityMapper;
import com.tandem.chat_service.dao.ChatDao;
import com.tandem.chat_service.dao.ChatParticipantDao;
import com.tandem.chat_service.dao.MessageDao;
import com.tandem.chat_service.dao.enums.MessageType;
import com.tandem.chat_service.dao.model.MessageEntity;
import com.tandem.chat_service.dao.model.ChatParticipantEntity;
import com.tandem.chat_service.integration.InterestEventPublisher;
import com.tandem.chat_service.service.model.dto.MessageMetadata;
import com.tandem.chat_service.service.model.request.GetMessagesFilter;
import com.tandem.chat_service.service.model.request.SendMessageRequest;
import com.tandem.chat_service.service.model.request.UpdateMessageRequest;
import com.tandem.chat_service.service.model.response.MessageResponse;
import com.tandem.chat_service.service.model.response.PaginatedMessagesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MessageDalImpl implements MessageDal {

    private final MessageDao messageDao;
    private final ChatParticipantDao participantDao;
    private final ChatDao chatDao;
    private final MessageEntityMapper mapper;
    private final InterestEventPublisher publisher;

    @Override
    public PaginatedMessagesResponse getMessagesByChatId(GetMessagesFilter filter) {
        UUID chatId = filter.getChatId();
        UUID requesterId = filter.getRequesterId();
        LocalDateTime before = filter.getBefore();
        LocalDateTime after = filter.getAfter();
        int limit = filter.getLimit();

        if (!participantDao.isParticipant(chatId, requesterId)) {
            throw new RuntimeException("User is not a participant of this chat");
        }

        List<MessageEntity> messages;
        boolean hasMore = false;

        if (before != null) {
            messages = messageDao.findMessagesByChatIdBefore(chatId, before, limit + 1);
        } else if (after != null) {
            messages = messageDao.findMessagesByChatIdAfter(chatId, after, limit + 1);
        } else {
            messages = messageDao.findMessagesByChatIdBefore(chatId, LocalDateTime.now(), limit + 1);
        }

        if (messages.size() > limit) {
            hasMore = true;
            messages = messages.subList(0, limit);
        }

        return mapper.mapToPaginatedResponse(messages, hasMore);
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(UUID chatId, UUID senderId, SendMessageRequest request) {
        if (!participantDao.isParticipant(chatId, senderId)) {
            throw new RuntimeException("User is not a participant of this chat");
        }

        validateMetadata(request.getType(), request.getMetadata());

        MessageEntity entity = mapper.mapToEntity(chatId, senderId, request);
        messageDao.insert(entity);
        chatDao.updateLastMessageAt(chatId, entity.getSentAt());

        return mapper.mapToResponse(entity);
    }

    @Override
    public void publishMessageSentEvent(UUID messageId) {
        MessageEntity message = messageDao.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        List<UUID> recipientIds = participantDao.findByChatId(message.getChatId()).stream()
                .map(ChatParticipantEntity::getUserId)
                .filter(userId -> !userId.equals(message.getSenderId()))
                .toList();

        publisher.publishMessageSent(message, recipientIds);
    }

    @Override
    @Transactional
    public MessageResponse updateMessage(UUID messageId, UUID requesterId, UpdateMessageRequest request) {
        MessageEntity message = messageDao.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getSenderId().equals(requesterId)) {
            throw new RuntimeException("Only the message sender can edit it");
        }

        MessageEntity updatedMessage = mapper.mapToUpdatedEntity(message, request);
        messageDao.update(updatedMessage);

        return mapper.mapToResponse(updatedMessage);
    }

    @Override
    public void deleteMessage(UUID messageId, UUID requesterId) {
        MessageEntity message = messageDao.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getSenderId().equals(requesterId)) {
            throw new RuntimeException("Only the message sender can delete it");
        }

        messageDao.softDelete(messageId, LocalDateTime.now());
    }

    private void validateMetadata(MessageType type, MessageMetadata meta) {
        if (type == MessageType.TEXT) return;

        if (meta == null) {
            throw new RuntimeException("Metadata is required for type: " + type.getValue());
        }

        switch (type) {
            case FILE -> {
                if (meta.getFileId() == null) {
                    throw new RuntimeException("fileId is required for FILE message");
                }
            }
            case LINK -> {
                if (meta.getLinkUrl() == null) {
                    throw new RuntimeException("linkUrl is required for LINK message");
                }
            }
            case CODE -> {
                if (meta.getCode() == null || meta.getLanguage() == null) {
                    throw new RuntimeException("code and language are required for CODE message");
                }
            }
            case EMOJI -> {
                if (meta.getEmoji() == null) {
                    throw new RuntimeException("emoji is required for EMOJI message");
                }
            }
            case STICKER -> {
                if (meta.getStickerId() == null && meta.getStickerUrl() == null) {
                    throw new RuntimeException("stickerId or stickerUrl is required for STICKER message");
                }
            }
        }
    }
}
