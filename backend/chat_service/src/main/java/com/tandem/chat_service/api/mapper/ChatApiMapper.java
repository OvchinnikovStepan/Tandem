package com.tandem.chat_service.api.mapper;

import com.tandem.chat_service.api.model.request.CreateGroupChatRequestJson;
import com.tandem.chat_service.api.model.request.UpdateGroupRequestJson;
import com.tandem.chat_service.api.model.response.ChatParticipantDtoJson;
import com.tandem.chat_service.api.model.response.ChatResponseJson;
import com.tandem.chat_service.api.model.response.GroupDtoJson;
import com.tandem.chat_service.service.model.request.CreateGroupChatRequest;
import com.tandem.chat_service.service.model.request.UpdateGroupRequest;
import com.tandem.chat_service.service.model.response.ChatParticipantDto;
import com.tandem.chat_service.service.model.response.ChatResponse;
import com.tandem.chat_service.service.model.response.GroupDto;
import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class ChatApiMapper {

    public CreateGroupChatRequest toServiceModel(UUID creatorId, CreateGroupChatRequestJson request) {
        return CreateGroupChatRequest.builder()
                .name(request.getName())
                .description(request.getDescription())
                .avatarUrl(request.getAvatarUrl())
                .visibility(request.getVisibility())
                .creatorId(creatorId)
                .groupInterests(request.getGroupInterests())
                .build();
    }

    public ChatResponseJson toJson(ChatResponse response) {
        if (response == null) {
            return null;
        }

        return ChatResponseJson.builder()
                .id(response.getId())
                .group(mapGroupToJson(response.getGroup()))
                .createdAt(response.getCreatedAt())
                .lastMessageAt(response.getLastMessageAt())
                .participants(
                        response.getParticipants() == null ? null :
                                response.getParticipants().stream()
                                        .map(ChatApiMapper::mapParticipantToJson)
                                        .collect(Collectors.toList())
                )
                .build();
    }

    public GroupDtoJson mapGroupToJson(GroupDto group) {
        if (group == null) {
            return null;
        }
        return GroupDtoJson.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .avatarUrl(group.getAvatarUrl())
                .creatorId(group.getCreatorId())
                .visibility(group.getVisibility())
                .build();
    }

    private ChatParticipantDtoJson mapParticipantToJson(ChatParticipantDto participant) {
        if (participant == null) {
            return null;
        }
        return ChatParticipantDtoJson.builder()
                .id(participant.getId())
                .chatId(participant.getChatId())
                .userId(participant.getUserId())
                .role(participant.getRole())
                .isMuted(participant.getIsMuted())
                .isBanned(participant.getIsBanned())
                .joinedAt(participant.getJoinedAt())
                .exitedAt(participant.getExitedAt())
                .build();
    }

    public UpdateGroupRequest toUpdateServiceModel(UpdateGroupRequestJson request) {
        return UpdateGroupRequest.builder()
                .name(request.getName())
                .description(request.getDescription())
                .avatarUrl(request.getAvatarUrl())
                .visibility(request.getVisibility())
                .build();
    }
}