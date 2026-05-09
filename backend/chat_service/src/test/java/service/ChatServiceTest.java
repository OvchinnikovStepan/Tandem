package service;

import com.tandem.chat_service.dal.ChatDal;
import com.tandem.chat_service.service.ChatService;
import com.tandem.chat_service.service.exception.ChatAccessDeniedException;
import com.tandem.chat_service.service.exception.ChatNotFoundException;
import com.tandem.chat_service.service.exception.GroupNotFoundException;
import com.tandem.chat_service.service.exception.InvalidChatOperationException;
import com.tandem.chat_service.service.impl.ChatServiceImpl;
import com.tandem.chat_service.service.model.request.CreateGroupChatRequest;
import com.tandem.chat_service.service.model.request.UpdateGroupRequest;
import com.tandem.chat_service.service.model.response.ChatParticipantDto;
import com.tandem.chat_service.service.model.response.ChatResponse;
import com.tandem.chat_service.service.model.response.GroupDto;
import com.tandem.chat_service.dao.enums.GroupVisibility;
import com.tandem.chat_service.dao.enums.ParticipantRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatDal chatDal;

    private ChatService chatService;

    private UUID chatId;
    private UUID userId;
    private UUID initiatorId;
    private UUID targetUserId;
    private UUID requesterId;
    private String groupName;
    private String chatName;
    private CreateGroupChatRequest createGroupRequest;
    private UpdateGroupRequest updateGroupRequest;
    private ChatResponse chatResponse;
    private GroupDto groupDto;
    private List<ChatResponse> userChats;
    private ChatParticipantDto participantDto;

    @BeforeEach
    void setUp() {
        chatService = new ChatServiceImpl(chatDal);
        initializeTestData();
    }

    private void initializeTestData() {
        chatId = UUID.randomUUID();
        userId = UUID.randomUUID();
        initiatorId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();
        requesterId = UUID.randomUUID();
        groupName = "Test Group";
        chatName = "Test Chat";

        participantDto = ChatParticipantDto.builder()
                .id(UUID.randomUUID())
                .chatId(chatId)
                .userId(userId)
                .role(ParticipantRole.MEMBER)
                .isMuted(false)
                .isBanned(false)
                .joinedAt(LocalDateTime.now())
                .build();

        groupDto = GroupDto.builder()
                .id(UUID.randomUUID())
                .name(groupName)
                .description("Test group description")
                .creatorId(initiatorId)
                .visibility(GroupVisibility.PUBLIC)
                .build();

        chatResponse = ChatResponse.builder()
                .id(chatId)
                .group(groupDto)
                .createdAt(LocalDateTime.now())
                .lastMessageAt(LocalDateTime.now())
                .participants(List.of(participantDto))
                .build();

        userChats = List.of(chatResponse);

        createGroupRequest = CreateGroupChatRequest.builder()
                .name(groupName)
                .description("Test group description")
                .creatorId(initiatorId)
                .visibility(GroupVisibility.PUBLIC)
                .build();

        updateGroupRequest = UpdateGroupRequest.builder()
                .name("Updated Group Name")
                .description("Updated description")
                .visibility(GroupVisibility.PRIVATE)
                .build();
    }

    // getUserChats
    @Test
    void getUserChats_Success() {
        when(chatDal.getUserChats(userId)).thenReturn(userChats);

        List<ChatResponse> result = chatService.getUserChats(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(chatId);
        assertThat(result.get(0).getGroup().getName()).isEqualTo(groupName);

        verify(chatDal).getUserChats(userId);
    }

    @Test
    void getUserChats_ReturnsEmptyList_WhenNoChats() {
        when(chatDal.getUserChats(userId)).thenReturn(List.of());

        List<ChatResponse> result = chatService.getUserChats(userId);

        assertThat(result).isEmpty();
        verify(chatDal).getUserChats(userId);
    }

    @Test
    void getUserChats_PropagatesDalException() {
        when(chatDal.getUserChats(userId))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> chatService.getUserChats(userId)
        );

        assertThat(exception.getMessage()).contains("Database error");
        verify(chatDal).getUserChats(userId);
    }

    // getChatById
    @Test
    void getChatById_Success() {
        when(chatDal.getChatById(chatId)).thenReturn(chatResponse);

        ChatResponse result = chatService.getChatById(chatId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(chatId);
        assertThat(result.getGroup().getName()).isEqualTo(groupName);

        verify(chatDal).getChatById(chatId);
    }

    @Test
    void getChatById_ThrowsChatNotFoundException_WhenChatNotFound() {
        when(chatDal.getChatById(chatId))
                .thenThrow(new RuntimeException("Chat not found"));

        ChatNotFoundException exception = assertThrows(
                ChatNotFoundException.class,
                () -> chatService.getChatById(chatId)
        );

        assertThat(exception.getMessage()).contains(chatId.toString());
        verify(chatDal).getChatById(chatId);
    }

    // getGroupChatByName
    @Test
    void getGroupChatByName_Success() {
        when(chatDal.getGroupChatByName(groupName)).thenReturn(chatResponse);

        ChatResponse result = chatService.getGroupChatByName(groupName);

        assertThat(result).isNotNull();
        assertThat(result.getGroup().getName()).isEqualTo(groupName);

        verify(chatDal).getGroupChatByName(groupName);
    }

    @Test
    void getGroupChatByName_ThrowsGroupNotFoundException_WhenGroupNotFound() {
        when(chatDal.getGroupChatByName(groupName))
                .thenThrow(new RuntimeException("Group not found"));

        GroupNotFoundException exception = assertThrows(
                GroupNotFoundException.class,
                () -> chatService.getGroupChatByName(groupName)
        );

        assertThat(exception.getMessage()).contains(groupName);
        verify(chatDal).getGroupChatByName(groupName);
    }

    // createPersonalChat
    @Test
    void createPersonalChat_Success() {
        when(chatDal.createPersonalChat(initiatorId, targetUserId)).thenReturn(chatResponse);

        ChatResponse result = chatService.createPersonalChat(initiatorId, targetUserId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(chatId);

        verify(chatDal).createPersonalChat(initiatorId, targetUserId);
    }

    @Test
    void createPersonalChat_ThrowsInvalidChatOperationException_WhenSameUser() {
        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> chatService.createPersonalChat(initiatorId, initiatorId)
        );

        assertThat(exception.getMessage()).contains("Cannot create a personal chat with yourself");
        verify(chatDal, never()).createPersonalChat(any(), any());
    }

    @Test
    void createPersonalChat_PropagatesDalException() {
        when(chatDal.createPersonalChat(initiatorId, targetUserId))
                .thenThrow(new RuntimeException("User not found"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> chatService.createPersonalChat(initiatorId, targetUserId)
        );

        assertThat(exception.getMessage()).contains("User not found");
        verify(chatDal).createPersonalChat(initiatorId, targetUserId);
    }

    // createGroupChat
    @Test
    void createGroupChat_Success() {
        when(chatDal.createGroupChat(createGroupRequest)).thenReturn(chatResponse);

        ChatResponse result = chatService.createGroupChat(createGroupRequest);

        assertThat(result).isNotNull();
        assertThat(result.getGroup().getName()).isEqualTo(groupName);

        verify(chatDal).createGroupChat(createGroupRequest);
    }

    @Test
    void createGroupChat_ThrowsInvalidChatOperationException_WhenNameEmpty() {
        CreateGroupChatRequest invalidRequest = CreateGroupChatRequest.builder()
                .name("")
                .creatorId(initiatorId)
                .build();

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> chatService.createGroupChat(invalidRequest)
        );

        assertThat(exception.getMessage()).contains("Group chat name cannot be empty");
        verify(chatDal, never()).createGroupChat(any());
    }

    @Test
    void createGroupChat_ThrowsInvalidChatOperationException_WhenNameNull() {
        CreateGroupChatRequest invalidRequest = CreateGroupChatRequest.builder()
                .name(null)
                .creatorId(initiatorId)
                .build();

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> chatService.createGroupChat(invalidRequest)
        );

        assertThat(exception.getMessage()).contains("Group chat name cannot be empty");
        verify(chatDal, never()).createGroupChat(any());
    }

    @Test
    void createGroupChat_WithNameContainingOnlySpaces() {
        CreateGroupChatRequest invalidRequest = CreateGroupChatRequest.builder()
                .name("   ")
                .creatorId(initiatorId)
                .build();

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> chatService.createGroupChat(invalidRequest)
        );

        assertThat(exception.getMessage()).contains("Group chat name cannot be empty");
        verify(chatDal, never()).createGroupChat(any());
    }

    @Test
    void createGroupChat_PropagatesDalException() {
        when(chatDal.createGroupChat(createGroupRequest))
                .thenThrow(new RuntimeException("Group name already exists"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> chatService.createGroupChat(createGroupRequest)
        );

        assertThat(exception.getMessage()).contains("Group name already exists");
        verify(chatDal).createGroupChat(createGroupRequest);
    }

    // leaveGroupChat
    @Test
    void leaveGroupChat_Success() {
        doNothing().when(chatDal).leaveGroupChat(chatId, userId);

        chatService.leaveGroupChat(chatId, userId);

        verify(chatDal).leaveGroupChat(chatId, userId);
    }

    @Test
    void leaveGroupChat_ThrowsInvalidChatOperationException_WhenLeavingPersonalChat() {
        String errorMessage = "Cannot leave a personal chat";

        doThrow(new RuntimeException(errorMessage))
                .when(chatDal).leaveGroupChat(chatId, userId);

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> chatService.leaveGroupChat(chatId, userId)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(chatDal).leaveGroupChat(chatId, userId);
    }

    @Test
    void leaveGroupChat_ThrowsChatNotFoundException_WhenChatNotFound() {
        doThrow(new RuntimeException("Chat not found"))
                .when(chatDal).leaveGroupChat(chatId, userId);

        ChatNotFoundException exception = assertThrows(
                ChatNotFoundException.class,
                () -> chatService.leaveGroupChat(chatId, userId)
        );

        assertThat(exception.getMessage()).contains(chatId.toString());
        verify(chatDal).leaveGroupChat(chatId, userId);
    }

    @Test
    void leaveGroupChat_ThrowsInvalidChatOperationException_WhenCreatorLeaving() {
        String errorMessage = "Cannot leave a personal chat. Use delete instead.";

        doThrow(new RuntimeException(errorMessage))
                .when(chatDal).leaveGroupChat(chatId, userId);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> chatService.leaveGroupChat(chatId, userId)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(chatDal).leaveGroupChat(chatId, userId);
    }

    // deletePersonalChat
    @Test
    void deletePersonalChat_Success() {
        doNothing().when(chatDal).deletePersonalChat(chatId);

        chatService.deletePersonalChat(chatId);

        verify(chatDal).deletePersonalChat(chatId);
    }

    @Test
    void deletePersonalChat_ThrowsInvalidChatOperationException_WhenDeletingGroupChat() {
        String errorMessage = "Cannot delete group chat using this method";

        doThrow(new RuntimeException(errorMessage))
                .when(chatDal).deletePersonalChat(chatId);

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> chatService.deletePersonalChat(chatId)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(chatDal).deletePersonalChat(chatId);
    }

    @Test
    void deletePersonalChat_ThrowsChatNotFoundException_WhenChatNotFound() {
        doThrow(new RuntimeException("Chat not found"))
                .when(chatDal).deletePersonalChat(chatId);

        ChatNotFoundException exception = assertThrows(
                ChatNotFoundException.class,
                () -> chatService.deletePersonalChat(chatId)
        );

        assertThat(exception.getMessage()).contains(chatId.toString());
        verify(chatDal).deletePersonalChat(chatId);
    }

    // muteChat
    @Test
    void muteChat_Success() {
        doNothing().when(chatDal).muteChat(chatId, userId);

        chatService.muteChat(chatId, userId);

        verify(chatDal).muteChat(chatId, userId);
    }

    @Test
    void muteChat_ThrowsInvalidChatOperationException_WhenNotParticipant() {
        String errorMessage = "User is not a participant of this chat";

        doThrow(new RuntimeException(errorMessage))
                .when(chatDal).muteChat(chatId, userId);

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> chatService.muteChat(chatId, userId)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(chatDal).muteChat(chatId, userId);
    }

    // kickUserFromGroupChat
    @Test
    void kickUserFromGroupChat_Success() {
        doNothing().when(chatDal).kickUserFromGroupChat(chatId, targetUserId, requesterId);

        chatService.kickUserFromGroupChat(chatId, targetUserId, requesterId);

        verify(chatDal).kickUserFromGroupChat(chatId, targetUserId, requesterId);
    }

    @Test
    void kickUserFromGroupChat_ThrowsInvalidChatOperationException_WhenKickingSelf() {
        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> chatService.kickUserFromGroupChat(chatId, requesterId, requesterId)
        );

        assertThat(exception.getMessage()).contains("You cannot kick yourself");
        verify(chatDal, never()).kickUserFromGroupChat(any(), any(), any());
    }

    @Test
    void kickUserFromGroupChat_ThrowsChatAccessDeniedException_WhenNotCreator() {
        String errorMessage = "Only group creator can kick users";

        doThrow(new RuntimeException(errorMessage))
                .when(chatDal).kickUserFromGroupChat(chatId, targetUserId, requesterId);

        ChatAccessDeniedException exception = assertThrows(
                ChatAccessDeniedException.class,
                () -> chatService.kickUserFromGroupChat(chatId, targetUserId, requesterId)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(chatDal).kickUserFromGroupChat(chatId, targetUserId, requesterId);
    }

    @Test
    void kickUserFromGroupChat_ThrowsInvalidChatOperationException_WhenNotGroupChat() {
        String errorMessage = "Group chat not found";

        doThrow(new RuntimeException(errorMessage))
                .when(chatDal).kickUserFromGroupChat(chatId, targetUserId, requesterId);

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> chatService.kickUserFromGroupChat(chatId, targetUserId, requesterId)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(chatDal).kickUserFromGroupChat(chatId, targetUserId, requesterId);
    }

    @Test
    void kickUserFromGroupChat_ThrowsChatNotFoundException_WhenChatNotFound() {
        doThrow(new RuntimeException("Chat not found"))
                .when(chatDal).kickUserFromGroupChat(chatId, targetUserId, requesterId);

        ChatNotFoundException exception = assertThrows(
                ChatNotFoundException.class,
                () -> chatService.kickUserFromGroupChat(chatId, targetUserId, requesterId)
        );

        assertThat(exception.getMessage()).contains(chatId.toString());
        verify(chatDal).kickUserFromGroupChat(chatId, targetUserId, requesterId);
    }

    // updateGroupSettings
    @Test
    void updateGroupSettings_Success() {
        GroupDto updatedGroupDto = GroupDto.builder()
                .id(groupDto.getId())
                .name("Updated Group Name")
                .description("Updated description")
                .creatorId(initiatorId)
                .visibility(GroupVisibility.PRIVATE)
                .build();

        when(chatDal.updateGroupSettings(chatId, requesterId, updateGroupRequest))
                .thenReturn(updatedGroupDto);

        GroupDto result = chatService.updateGroupSettings(chatId, requesterId, updateGroupRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Group Name");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getVisibility()).isEqualTo(GroupVisibility.PRIVATE);

        verify(chatDal).updateGroupSettings(chatId, requesterId, updateGroupRequest);
    }

    @Test
    void updateGroupSettings_ThrowsChatAccessDeniedException_WhenNotCreator() {
        String errorMessage = "Only group creator can update settings";

        doThrow(new RuntimeException(errorMessage))
                .when(chatDal).updateGroupSettings(chatId, requesterId, updateGroupRequest);

        ChatAccessDeniedException exception = assertThrows(
                ChatAccessDeniedException.class,
                () -> chatService.updateGroupSettings(chatId, requesterId, updateGroupRequest)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(chatDal).updateGroupSettings(chatId, requesterId, updateGroupRequest);
    }

    @Test
    void updateGroupSettings_ThrowsInvalidChatOperationException_WhenNotGroupChat() {
        String errorMessage = "Group chat not found";

        doThrow(new RuntimeException(errorMessage))
                .when(chatDal).updateGroupSettings(chatId, requesterId, updateGroupRequest);

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> chatService.updateGroupSettings(chatId, requesterId, updateGroupRequest)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(chatDal).updateGroupSettings(chatId, requesterId, updateGroupRequest);
    }

    @Test
    void updateGroupSettings_ThrowsChatNotFoundException_WhenChatNotFound() {
        doThrow(new RuntimeException("Chat not found"))
                .when(chatDal).updateGroupSettings(chatId, requesterId, updateGroupRequest);

        ChatNotFoundException exception = assertThrows(
                ChatNotFoundException.class,
                () -> chatService.updateGroupSettings(chatId, requesterId, updateGroupRequest)
        );

        assertThat(exception.getMessage()).contains(chatId.toString());
        verify(chatDal).updateGroupSettings(chatId, requesterId, updateGroupRequest);
    }

    // searchGroupChatsByPrefix
    @Test
    void searchGroupChatsByPrefix_Success() {
        int limit = 10;
        String prefix = "Test";

        when(chatDal.searchGroupChatsByPrefix(prefix, limit))
                .thenReturn(userChats);

        List<ChatResponse> result = chatService.searchGroupChatsByPrefix(prefix, limit);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGroup().getName()).isEqualTo(groupName);

        verify(chatDal).searchGroupChatsByPrefix(prefix, limit);
    }

    @Test
    void searchGroupChatsByPrefix_ReturnsEmptyList_WhenNoMatches() {
        int limit = 10;
        String prefix = "NonExistent";

        when(chatDal.searchGroupChatsByPrefix(prefix, limit))
                .thenReturn(List.of());

        List<ChatResponse> result = chatService.searchGroupChatsByPrefix(prefix, limit);

        assertThat(result).isEmpty();
        verify(chatDal).searchGroupChatsByPrefix(prefix, limit);
    }

    @Test
    void searchGroupChatsByPrefix_WithLimitZero() {
        int limit = 0;
        String prefix = "Test";

        when(chatDal.searchGroupChatsByPrefix(prefix, limit))
                .thenReturn(List.of());

        List<ChatResponse> result = chatService.searchGroupChatsByPrefix(prefix, limit);

        assertThat(result).isEmpty();
        verify(chatDal).searchGroupChatsByPrefix(prefix, limit);
    }

    @Test
    void searchGroupChatsByPrefix_WithEmptyPrefix() {
        int limit = 10;
        String prefix = "";

        when(chatDal.searchGroupChatsByPrefix(prefix, limit))
                .thenReturn(List.of());

        List<ChatResponse> result = chatService.searchGroupChatsByPrefix(prefix, limit);

        assertThat(result).isEmpty();
        verify(chatDal).searchGroupChatsByPrefix(prefix, limit);
    }

    // joinPublicGroupChat
    @Test
    void joinPublicGroupChat_Success() {
        doNothing().when(chatDal).joinPublicGroupChat(chatId, userId);

        chatService.joinPublicGroupChat(chatId, userId);

        verify(chatDal).joinPublicGroupChat(chatId, userId);
    }

    @Test
    void joinPublicGroupChat_ThrowsChatNotFoundException_WhenChatNotFound() {
        String errorMessage = "Chat not found";

        doThrow(new RuntimeException(errorMessage))
                .when(chatDal).joinPublicGroupChat(chatId, userId);

        ChatNotFoundException exception = assertThrows(
                ChatNotFoundException.class,
                () -> chatService.joinPublicGroupChat(chatId, userId)
        );

        assertThat(exception.getMessage()).contains(chatId.toString());
        verify(chatDal).joinPublicGroupChat(chatId, userId);
    }

    @Test
    void joinPublicGroupChat_ThrowsInvalidChatOperationException_WhenNotPublic() {
        String errorMessage = "Cannot join private group chat without request";

        doThrow(new RuntimeException(errorMessage))
                .when(chatDal).joinPublicGroupChat(chatId, userId);

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> chatService.joinPublicGroupChat(chatId, userId)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(chatDal).joinPublicGroupChat(chatId, userId);
    }

    @Test
    void joinPublicGroupChat_ThrowsInvalidChatOperationException_WhenAlreadyMember() {
        String errorMessage = "User is already a member of this chat";

        doThrow(new RuntimeException(errorMessage))
                .when(chatDal).joinPublicGroupChat(chatId, userId);

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> chatService.joinPublicGroupChat(chatId, userId)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(chatDal).joinPublicGroupChat(chatId, userId);
    }
}