package com.tandem.chat_service.dal.mapper;

import com.tandem.chat_service.dao.model.ChatParticipantEntity;
import com.tandem.chat_service.dao.model.GroupRequestEntity;
import com.tandem.chat_service.service.model.response.GroupRequestDto;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class GroupRequestEntityMapper {

    public GroupRequestEntity toEntity(UUID groupId, UUID userId, UUID requestedBy, String message) {
        return GroupRequestEntity.builder()
                .groupId(groupId)
                .userId(userId)
                .requestedBy(requestedBy)
                .message(message)
                .expiresAt(LocalDateTime.now().plusDays(2))
                .build();
    }

    public ChatParticipantEntity toParticipantEntity(UUID chatId, UUID userId) {
        return ChatParticipantEntity.builder()
                .chatId(chatId)
                .userId(userId)
                .build();
    }

    public GroupRequestDto toDto(GroupRequestEntity entity) {
        if (entity == null) {
            return null;
        }

        return GroupRequestDto.builder()
                .id(entity.getId())
                .groupId(entity.getGroupId())
                .userId(entity.getUserId())
                .status(entity.getStatus())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}