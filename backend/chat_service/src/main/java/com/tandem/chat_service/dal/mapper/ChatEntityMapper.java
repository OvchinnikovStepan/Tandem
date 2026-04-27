package com.tandem.chat_service.dal.mapper;

import com.tandem.chat_service.dao.enums.ParticipantRole;
import com.tandem.chat_service.dao.model.ChatEntity;
import com.tandem.chat_service.dao.model.ChatParticipantEntity;
import com.tandem.chat_service.dao.model.GroupEntity;
import com.tandem.chat_service.service.model.request.CreateGroupChatRequest;
import com.tandem.chat_service.service.model.request.UpdateGroupRequest;
import com.tandem.chat_service.service.model.response.ChatParticipantDto;
import com.tandem.chat_service.service.model.response.ChatResponse;
import com.tandem.chat_service.service.model.response.GroupDto;
import io.micrometer.common.lang.Nullable;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class ChatEntityMapper {

    public ChatEntity mapToChatEntity(@Nullable UUID groupId) {
        if (groupId == null) {
            return ChatEntity.builder()
                    .groupId(null) // Личный чат
                    .build();
        }
        else {
            return ChatEntity.builder()
                    .groupId(groupId) // Групповой чат
                    .build();
        }
    }

    public ChatParticipantEntity mapToAdminParticipantEntity(UUID chatId, UUID userId) {
        return ChatParticipantEntity.builder()
                .chatId(chatId)
                .userId(userId)
                .role(ParticipantRole.ADMIN)
                .build();
    }

    public ChatParticipantEntity mapToMemberParticipantEntity(UUID chatId, UUID userId) {
        return ChatParticipantEntity.builder()
                .chatId(chatId)
                .userId(userId)
                .build();
    }

    public GroupEntity mapToGroupEntity(CreateGroupChatRequest request) {
        return GroupEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .avatarUrl(request.getAvatarUrl())
                .creatorId(request.getCreatorId())
                .visibility(request.getVisibility())
                .build();
    }

    public GroupEntity mapToUpdatedGroupEntity(GroupEntity existingGroup, UpdateGroupRequest request) {
        return GroupEntity.builder()
                .id(existingGroup.getId())
                .creatorId(existingGroup.getCreatorId())
                .name(request.getName())
                .description(request.getDescription())
                .avatarUrl(request.getAvatarUrl())
                .visibility(request.getVisibility())
                .build();
    }


    public GroupDto mapToGroupDto(GroupEntity entity) {
        if (entity == null) return null;

        return GroupDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .avatarUrl(entity.getAvatarUrl())
                .creatorId(entity.getCreatorId())
                .visibility(entity.getVisibility())
                .build();
    }

    public ChatParticipantDto mapToParticipantDto(ChatParticipantEntity entity) {
        if (entity == null) return null;

        return ChatParticipantDto.builder()
                .id(entity.getId())
                .chatId(entity.getChatId())
                .userId(entity.getUserId())
                .role(entity.getRole())
                .isMuted(entity.getIsMuted())
                .isBanned(entity.getIsBanned())
                .joinedAt(entity.getJoinedAt())
                .exitedAt(entity.getExitedAt())
                .build();
    }

    public ChatResponse mapToChatResponse(ChatEntity chat, GroupEntity group, List<ChatParticipantEntity> participants) {
        List<ChatParticipantDto> participantDtos = participants.stream()
                .map(ChatEntityMapper::mapToParticipantDto)
                .collect(Collectors.toList());

        return ChatResponse.builder()
                .id(chat.getId())
                .group(mapToGroupDto(group))
                .createdAt(chat.getCreatedAt())
                .lastMessageAt(chat.getLastMessageAt())
                .participants(participantDtos)
                .build();
    }
}