package dal;

import com.tandem.chat_service.dal.ChatDal;
import com.tandem.chat_service.dal.impl.ChatDalImpl;
import com.tandem.chat_service.dao.ChatDao;
import com.tandem.chat_service.dao.ChatParticipantDao;
import com.tandem.chat_service.dao.GroupDao;
import com.tandem.chat_service.dao.enums.GroupVisibility;
import com.tandem.chat_service.dao.model.ChatEntity;
import com.tandem.chat_service.dao.model.GroupEntity;
import com.tandem.chat_service.integration.InterestEventPublisher;
import com.tandem.chat_service.service.model.request.CreateGroupChatRequest;
import com.tandem.chat_service.service.model.request.UpdateGroupRequest;
import com.tandem.chat_service.service.model.response.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatDalTest {

    @Mock
    private ChatDao chatDao;
    @Mock
    private GroupDao groupDao;
    @Mock
    private ChatParticipantDao participantDao;
    @Mock
    private InterestEventPublisher publisher;

    private ChatDal chatDal;

    private UUID userId, targetId, chatId, groupId;
    private ChatEntity personalChat, groupChat;
    private GroupEntity publicGroup, privateGroup;

    @BeforeEach
    void setUp() {
        chatDal = new ChatDalImpl(chatDao, groupDao, participantDao, publisher);

        userId = UUID.randomUUID();
        targetId = UUID.randomUUID();
        chatId = UUID.randomUUID();
        groupId = UUID.randomUUID();

        personalChat = ChatEntity.builder().id(chatId).build();
        groupChat = ChatEntity.builder().id(chatId).groupId(groupId).build();

        publicGroup = GroupEntity.builder().id(groupId).creatorId(userId).visibility(GroupVisibility.PUBLIC).build();
        privateGroup = GroupEntity.builder().id(groupId).creatorId(userId).visibility(GroupVisibility.PRIVATE).build();
    }

    // createPersonalChat
    @Test
    void createPersonalChat_Success() {
        when(chatDao.findById(any())).thenReturn(Optional.of(personalChat));

        ChatResponse result = chatDal.createPersonalChat(userId, targetId);

        assertThat(result).isNotNull();
        verify(chatDao).insert(any(ChatEntity.class));
        verify(participantDao, times(2)).insert(any());
    }

    // createGroupChat
    @Test
    void createGroupChat_Success() {
        CreateGroupChatRequest request = CreateGroupChatRequest.builder()
                .name("New Group")
                .creatorId(userId)
                .groupInterests(List.of("Java", "Spring"))
                .build();

        when(chatDao.findById(any())).thenReturn(Optional.of(groupChat));

        ChatResponse result = chatDal.createGroupChat(request);

        assertThat(result).isNotNull();
        verify(groupDao).insert(any(GroupEntity.class));
        verify(chatDao).insert(any(ChatEntity.class));
    }

    // joinPublicGroupChat
    @Test
    void joinPublicGroupChat_Success() {
        when(chatDao.findById(chatId)).thenReturn(Optional.of(groupChat));
        when(groupDao.findById(groupId)).thenReturn(Optional.of(publicGroup));
        when(participantDao.isParticipant(chatId, targetId)).thenReturn(false);

        chatDal.joinPublicGroupChat(chatId, targetId);

        verify(participantDao).insert(any());
    }

    @Test
    void joinPublicGroupChat_ThrowsException_WhenPrivateGroup() {
        when(chatDao.findById(chatId)).thenReturn(Optional.of(groupChat));
        when(groupDao.findById(groupId)).thenReturn(Optional.of(privateGroup));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatDal.joinPublicGroupChat(chatId, targetId));

        assertThat(exception.getMessage()).isEqualTo("Cannot join a private group directly");
        verify(participantDao, never()).insert(any());
    }

    @Test
    void joinPublicGroupChat_ThrowsException_WhenAlreadyParticipant() {
        when(chatDao.findById(chatId)).thenReturn(Optional.of(groupChat));
        when(groupDao.findById(groupId)).thenReturn(Optional.of(publicGroup));
        when(participantDao.isParticipant(chatId, targetId)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatDal.joinPublicGroupChat(chatId, targetId));

        assertThat(exception.getMessage()).isEqualTo("User is already a participant");
        verify(participantDao, never()).insert(any());
    }

    // leaveGroupChat
    @Test
    void leaveGroupChat_Success() {
        when(chatDao.findById(chatId)).thenReturn(Optional.of(groupChat));
        when(participantDao.isParticipant(chatId, userId)).thenReturn(true);

        chatDal.leaveGroupChat(chatId, userId);

        verify(participantDao).deleteByChatIdAndUserId(chatId, userId);
    }

    @Test
    void leaveGroupChat_ThrowsException_WhenPersonalChat() {
        when(chatDao.findById(chatId)).thenReturn(Optional.of(personalChat)); // Нет groupId

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatDal.leaveGroupChat(chatId, userId));

        assertThat(exception.getMessage()).isEqualTo("Cannot leave a personal chat. Use delete instead.");
    }

    @Test
    void leaveGroupChat_ThrowsException_WhenNotParticipant() {
        when(chatDao.findById(chatId)).thenReturn(Optional.of(groupChat));
        when(participantDao.isParticipant(chatId, userId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatDal.leaveGroupChat(chatId, userId));

        assertThat(exception.getMessage()).isEqualTo("User is not a participant of this chat");
    }

    // getUserChats
    @Test
    void getUserChats_Success() {
        when(chatDao.findPersonalChatsByUser(userId)).thenReturn(List.of(personalChat));
        when(chatDao.findGroupChatsByUser(userId)).thenReturn(List.of(groupChat));
        when(groupDao.findById(groupId)).thenReturn(Optional.of(publicGroup));
        when(participantDao.findByChatId(any(UUID.class))).thenReturn(List.of());

        List<ChatResponse> result = chatDal.getUserChats(userId);

        assertThat(result).hasSize(2);
        verify(chatDao).findPersonalChatsByUser(userId);
        verify(chatDao).findGroupChatsByUser(userId);
    }

    // getChatById
    @Test
    void getChatById_Success() {
        when(chatDao.findById(chatId)).thenReturn(Optional.of(personalChat));
        when(participantDao.findByChatId(chatId)).thenReturn(List.of());

        ChatResponse result = chatDal.getChatById(chatId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(chatId);
    }

    @Test
    void getChatById_ThrowsException_WhenChatNotFound() {
        when(chatDao.findById(chatId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatDal.getChatById(chatId));

        assertThat(exception.getMessage()).contains("Chat not found with id");
    }

    // getGroupChatByName
    @Test
    void getGroupChatByName_Success() {
        String groupName = "My Group";
        when(groupDao.findByName(groupName)).thenReturn(Optional.of(publicGroup));
        when(chatDao.findByGroupId(groupId)).thenReturn(Optional.of(groupChat));
        when(participantDao.findByChatId(chatId)).thenReturn(List.of());

        ChatResponse result = chatDal.getGroupChatByName(groupName);

        assertThat(result).isNotNull();
    }

    @Test
    void getGroupChatByName_ThrowsException_WhenGroupNotFound() {
        String groupName = "Unknown Group";
        when(groupDao.findByName(groupName)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatDal.getGroupChatByName(groupName));

        assertThat(exception.getMessage()).contains("Group not found with name");
    }

    // deletePersonalChat
    @Test
    void deletePersonalChat_Success() {
        when(chatDao.findById(chatId)).thenReturn(Optional.of(personalChat));

        chatDal.deletePersonalChat(chatId);

        verify(chatDao).delete(chatId);
    }

    @Test
    void deletePersonalChat_ThrowsException_WhenGroupChat() {
        when(chatDao.findById(chatId)).thenReturn(Optional.of(groupChat));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatDal.deletePersonalChat(chatId));

        assertThat(exception.getMessage()).isEqualTo("Cannot delete group chat using this method.");
        verify(chatDao, never()).delete(any());
    }

    // muteChat
    @Test
    void muteChat_Success() {
        when(participantDao.isParticipant(chatId, userId)).thenReturn(true);

        chatDal.muteChat(chatId, userId);

        verify(participantDao).updateMutedStatus(chatId, userId, true);
    }

    @Test
    void muteChat_ThrowsException_WhenNotParticipant() {
        when(participantDao.isParticipant(chatId, userId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatDal.muteChat(chatId, userId));

        assertThat(exception.getMessage()).isEqualTo("User is not a participant of this chat");
    }

    // kickUserFromGroupChat
    @Test
    void kickUserFromGroupChat_Success() {
        when(chatDao.findById(chatId)).thenReturn(Optional.of(groupChat));
        when(groupDao.findById(groupId)).thenReturn(Optional.of(publicGroup)); // userId is creator

        chatDal.kickUserFromGroupChat(chatId, targetId, userId);

        verify(participantDao).deleteByChatIdAndUserId(chatId, targetId);
    }

    @Test
    void kickUserFromGroupChat_ThrowsException_WhenNotCreator() {
        UUID nonCreatorId = UUID.randomUUID();
        when(chatDao.findById(chatId)).thenReturn(Optional.of(groupChat));
        when(groupDao.findById(groupId)).thenReturn(Optional.of(publicGroup));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatDal.kickUserFromGroupChat(chatId, targetId, nonCreatorId));

        assertThat(exception.getMessage()).isEqualTo("Only the group creator can kick users");
    }

    // updateGroupSettings
    @Test
    void updateGroupSettings_Success() {
        UpdateGroupRequest request = UpdateGroupRequest.builder().name("Updated").build();
        when(chatDao.findById(chatId)).thenReturn(Optional.of(groupChat));
        when(groupDao.findById(groupId)).thenReturn(Optional.of(publicGroup));

        chatDal.updateGroupSettings(chatId, userId, request);

        verify(groupDao).update(any(GroupEntity.class));
    }

    @Test
    void updateGroupSettings_ThrowsException_WhenGroupNotFound() {
        UpdateGroupRequest request = UpdateGroupRequest.builder().name("Updated").build();
        when(chatDao.findById(chatId)).thenReturn(Optional.of(groupChat));
        when(groupDao.findById(groupId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatDal.updateGroupSettings(chatId, userId, request));

        assertThat(exception.getMessage()).isEqualTo("Group not found");
    }

    // searchGroupChatsByPrefix
    @Test
    void searchGroupChatsByPrefix_Success() {
        when(groupDao.searchByNamePrefix("Test", 10)).thenReturn(List.of(publicGroup));
        when(chatDao.findByGroupId(groupId)).thenReturn(Optional.of(groupChat));
        when(participantDao.findByChatId(chatId)).thenReturn(List.of());

        List<ChatResponse> result = chatDal.searchGroupChatsByPrefix("Test", 10);

        assertThat(result).hasSize(1);
    }
}