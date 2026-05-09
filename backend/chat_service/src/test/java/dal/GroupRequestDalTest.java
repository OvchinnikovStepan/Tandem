package dal;

import com.tandem.chat_service.dal.GroupRequestDal;
import com.tandem.chat_service.dal.impl.GroupRequestDalImpl;
import com.tandem.chat_service.dao.ChatDao;
import com.tandem.chat_service.dao.ChatParticipantDao;
import com.tandem.chat_service.dao.GroupDao;
import com.tandem.chat_service.dao.GroupRequestDao;
import com.tandem.chat_service.dao.enums.GroupRequestStatus;
import com.tandem.chat_service.dao.enums.GroupVisibility;
import com.tandem.chat_service.dao.model.ChatEntity;
import com.tandem.chat_service.dao.model.GroupEntity;
import com.tandem.chat_service.dao.model.GroupRequestEntity;
import com.tandem.chat_service.integration.InterestEventPublisher;
import com.tandem.chat_service.service.model.response.GroupRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupRequestDalTest {

    @Mock
    private GroupRequestDao requestDao;
    @Mock
    private GroupDao groupDao;
    @Mock
    private ChatDao chatDao;
    @Mock
    private ChatParticipantDao participantDao;
    @Mock
    private InterestEventPublisher publisher;

    private GroupRequestDal requestDal;

    private UUID groupId, userId, creatorId, chatId, requestId;
    private GroupEntity privateGroup, publicGroup;
    private ChatEntity chatEntity;
    private GroupRequestEntity requestEntity;

    @BeforeEach
    void setUp() {
        requestDal = new GroupRequestDalImpl(requestDao, groupDao, chatDao, participantDao, publisher);

        groupId = UUID.randomUUID();
        userId = UUID.randomUUID();
        creatorId = UUID.randomUUID();
        chatId = UUID.randomUUID();
        requestId = UUID.randomUUID();

        privateGroup = GroupEntity.builder().id(groupId).creatorId(creatorId).visibility(GroupVisibility.PRIVATE).build();
        publicGroup = GroupEntity.builder().id(groupId).creatorId(creatorId).visibility(GroupVisibility.PUBLIC).build();
        chatEntity = ChatEntity.builder().id(chatId).groupId(groupId).build();

        requestEntity = GroupRequestEntity.builder()
                .id(requestId)
                .groupId(groupId)
                .userId(userId)
                .status(GroupRequestStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    // createRequest
    @Test
    void createRequest_Success() {
        when(groupDao.findById(groupId)).thenReturn(Optional.of(privateGroup));
        when(chatDao.findByGroupId(groupId)).thenReturn(Optional.of(chatEntity));
        when(participantDao.isParticipant(chatId, userId)).thenReturn(false);

        GroupRequestDto result = requestDal.createRequest(groupId, userId, "Let me in");

        assertThat(result).isNotNull();
        verify(requestDao).insert(any(GroupRequestEntity.class));
        verify(publisher).publishGroupRequestEvent(any(), eq(groupId), eq(creatorId), eq(GroupRequestStatus.PENDING));
    }

    @Test
    void createRequest_ThrowsException_WhenGroupPublic() {
        when(groupDao.findById(groupId)).thenReturn(Optional.of(publicGroup));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> requestDal.createRequest(groupId, userId, "Let me in"));

        assertThat(exception.getMessage()).isEqualTo("Group is public. Join directly instead of requesting.");
        verify(requestDao, never()).insert(any());
    }

    @Test
    void createRequest_ThrowsException_WhenAlreadyParticipant() {
        when(groupDao.findById(groupId)).thenReturn(Optional.of(privateGroup));
        when(chatDao.findByGroupId(groupId)).thenReturn(Optional.of(chatEntity));
        when(participantDao.isParticipant(chatId, userId)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> requestDal.createRequest(groupId, userId, "Let me in"));

        assertThat(exception.getMessage()).isEqualTo("User is already a participant");
        verify(requestDao, never()).insert(any());
    }

    // approveRequest
    @Test
    void approveRequest_Success() {
        when(requestDao.findById(requestId)).thenReturn(Optional.of(requestEntity));
        when(chatDao.findByGroupId(groupId)).thenReturn(Optional.of(chatEntity));
        when(participantDao.isParticipant(chatId, userId)).thenReturn(false);
        when(groupDao.findById(groupId)).thenReturn(Optional.of(privateGroup));

        requestDal.approveRequest(requestId, creatorId);

        verify(requestDao).updateStatus(eq(requestId), eq(GroupRequestStatus.APPROVED), any(), eq(creatorId));
        verify(participantDao).insert(any());
    }

    @Test
    void approveRequest_ThrowsException_WhenNotCreator() {
        when(requestDao.findById(requestId)).thenReturn(Optional.of(requestEntity));
        when(chatDao.findByGroupId(groupId)).thenReturn(Optional.of(chatEntity));
        when(participantDao.isParticipant(chatId, userId)).thenReturn(false);
        when(groupDao.findById(groupId)).thenReturn(Optional.of(privateGroup));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> requestDal.approveRequest(requestId, UUID.randomUUID()));

        assertThat(exception.getMessage()).isEqualTo("Only group creator can perform this action");
    }

    @Test
    void approveRequest_ThrowsException_WhenNotPending() {
        GroupRequestEntity rejectedRequestEntity = GroupRequestEntity.builder()
                .id(requestId)
                .groupId(groupId)
                .userId(userId)
                .status(GroupRequestStatus.REJECTED) // Устанавливаем статус REJECTED
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        when(requestDao.findById(requestId)).thenReturn(Optional.of(rejectedRequestEntity));
        when(chatDao.findByGroupId(groupId)).thenReturn(Optional.of(chatEntity));
        when(participantDao.isParticipant(chatId, userId)).thenReturn(false);
        when(groupDao.findById(groupId)).thenReturn(Optional.of(privateGroup));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> requestDal.approveRequest(requestId, creatorId));

        assertThat(exception.getMessage()).isEqualTo("Only pending requests can be approved");
    }

    // rejectRequest
    @Test
    void rejectRequest_Success() {
        when(requestDao.findById(requestId)).thenReturn(Optional.of(requestEntity));
        when(groupDao.findById(groupId)).thenReturn(Optional.of(privateGroup));

        requestDal.rejectRequest(requestId, creatorId);

        verify(requestDao).updateStatus(eq(requestId), eq(GroupRequestStatus.REJECTED), any(), eq(creatorId));
        verify(publisher).publishGroupRequestEvent(eq(requestId), eq(groupId), eq(userId), eq(GroupRequestStatus.REJECTED));
    }

    @Test
    void rejectRequest_ThrowsException_WhenRequestNotFound() {
        when(requestDao.findById(requestId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> requestDal.rejectRequest(requestId, creatorId));

        assertThat(exception.getMessage()).isEqualTo("Request not found");
    }

    // cancelRequest
    @Test
    void cancelRequest_Success() {
        when(requestDao.findById(requestId)).thenReturn(Optional.of(requestEntity));

        requestDal.cancelRequest(requestId, userId);

        verify(requestDao).updateStatus(eq(requestId), eq(GroupRequestStatus.CANCELLED), any(), eq(userId));
    }

    @Test
    void cancelRequest_ThrowsException_WhenNotOwner() {
        when(requestDao.findById(requestId)).thenReturn(Optional.of(requestEntity));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> requestDal.cancelRequest(requestId, UUID.randomUUID()));

        assertThat(exception.getMessage()).isEqualTo("You can only cancel your own requests");
    }

    // getPendingRequestsForGroup
    @Test
    void getPendingRequestsForGroup_Success() {
        when(groupDao.findById(groupId)).thenReturn(Optional.of(privateGroup)); // creatorId совпадает
        when(requestDao.findPendingByGroupId(groupId)).thenReturn(List.of(requestEntity));

        List<GroupRequestDto> result = requestDal.getPendingRequestsForGroup(groupId, creatorId);

        assertThat(result).hasSize(1);
        verify(requestDao).findPendingByGroupId(groupId);
    }

    @Test
    void getPendingRequestsForGroup_ThrowsException_WhenNotCreator() {
        when(groupDao.findById(groupId)).thenReturn(Optional.of(privateGroup));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> requestDal.getPendingRequestsForGroup(groupId, UUID.randomUUID()));

        assertThat(exception.getMessage()).isEqualTo("Only group creator can perform this action");
    }

    @Test
    void getPendingRequestsForGroup_ThrowsException_WhenGroupNotFound() {
        when(groupDao.findById(groupId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> requestDal.getPendingRequestsForGroup(groupId, creatorId));

        assertThat(exception.getMessage()).isEqualTo("Group not found");
    }

    // getMyRequests
    @Test
    void getMyRequests_Success() {
        when(requestDao.findByUserId(userId)).thenReturn(List.of(requestEntity));

        List<GroupRequestDto> result = requestDal.getMyRequests(userId);

        assertThat(result).hasSize(1);
        verify(requestDao).findByUserId(userId);
    }
}