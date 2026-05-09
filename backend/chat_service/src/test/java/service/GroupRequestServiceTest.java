package service;

import com.tandem.chat_service.dal.GroupRequestDal;
import com.tandem.chat_service.service.GroupRequestService;
import com.tandem.chat_service.service.exception.ChatAccessDeniedException;
import com.tandem.chat_service.service.exception.GroupNotFoundException;
import com.tandem.chat_service.service.exception.GroupRequestNotFoundException;
import com.tandem.chat_service.service.exception.InvalidChatOperationException;
import com.tandem.chat_service.service.impl.GroupRequestServiceImpl;
import com.tandem.chat_service.service.model.response.GroupRequestDto;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupRequestServiceTest {

    @Mock
    private GroupRequestDal requestDal;

    private GroupRequestService groupRequestService;

    private UUID groupId;
    private UUID userId;
    private UUID requestId;
    private UUID reviewerId;
    private String joinMessage;
    private GroupRequestDto groupRequestDto;

    @BeforeEach
    void setUp() {
        groupRequestService = new GroupRequestServiceImpl(requestDal);
        initializeTestData();
    }

    private void initializeTestData() {
        groupId = UUID.randomUUID();
        userId = UUID.randomUUID();
        requestId = UUID.randomUUID();
        reviewerId = UUID.randomUUID();
        joinMessage = "I would like to join this group";

        groupRequestDto = GroupRequestDto.builder()
                .id(requestId)
                .groupId(groupId)
                .userId(userId)
                .message(joinMessage)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // createRequest
    @Test
    void createRequest_Success() {
        when(requestDal.createRequest(groupId, userId, joinMessage))
                .thenReturn(groupRequestDto);

        GroupRequestDto result = groupRequestService.createRequest(groupId, userId, joinMessage);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(requestId);
        assertThat(result.getGroupId()).isEqualTo(groupId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getMessage()).isEqualTo(joinMessage);

        verify(requestDal).createRequest(groupId, userId, joinMessage);
    }

    @Test
    void createRequest_ThrowsGroupNotFoundException_WhenGroupNotFound() {
        String errorMessage = "Group not found";

        when(requestDal.createRequest(groupId, userId, joinMessage))
                .thenThrow(new RuntimeException(errorMessage));

        GroupNotFoundException exception = assertThrows(
                GroupNotFoundException.class,
                () -> groupRequestService.createRequest(groupId, userId, joinMessage)
        );

        assertThat(exception.getMessage()).contains(groupId.toString());
        verify(requestDal).createRequest(groupId, userId, joinMessage);
    }

    @Test
    void createRequest_ThrowsInvalidChatOperationException_WhenAlreadyRequested() {
        String errorMessage = "User already has a pending request for this group";

        when(requestDal.createRequest(groupId, userId, joinMessage))
                .thenThrow(new RuntimeException(errorMessage));

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> groupRequestService.createRequest(groupId, userId, joinMessage)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(requestDal).createRequest(groupId, userId, joinMessage);
    }

    @Test
    void createRequest_ThrowsInvalidChatOperationException_WhenUserAlreadyMember() {
        String errorMessage = "User is already a member of this group";

        when(requestDal.createRequest(groupId, userId, joinMessage))
                .thenThrow(new RuntimeException(errorMessage));

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> groupRequestService.createRequest(groupId, userId, joinMessage)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(requestDal).createRequest(groupId, userId, joinMessage);
    }

    @Test
    void createRequest_WithNullMessage() {
        String nullMessage = null;

        when(requestDal.createRequest(groupId, userId, nullMessage))
                .thenReturn(groupRequestDto);

        GroupRequestDto result = groupRequestService.createRequest(groupId, userId, nullMessage);

        assertThat(result).isNotNull();
        verify(requestDal).createRequest(groupId, userId, nullMessage);
    }

    // approveRequest
    @Test
    void approveRequest_Success() {
        doNothing().when(requestDal).approveRequest(requestId, reviewerId);

        groupRequestService.approveRequest(requestId, reviewerId);

        verify(requestDal).approveRequest(requestId, reviewerId);
    }

    @Test
    void approveRequest_ThrowsGroupRequestNotFoundException_WhenRequestNotFound() {
        String errorMessage = "Request not found";

        doThrow(new RuntimeException(errorMessage))
                .when(requestDal).approveRequest(requestId, reviewerId);

        GroupRequestNotFoundException exception = assertThrows(
                GroupRequestNotFoundException.class,
                () -> groupRequestService.approveRequest(requestId, reviewerId)
        );

        assertThat(exception.getMessage()).contains(requestId.toString());
        verify(requestDal).approveRequest(requestId, reviewerId);
    }

    @Test
    void approveRequest_ThrowsInvalidChatOperationException_WhenNotAuthorized() {
        String errorMessage = "Only group creator or admins can approve requests";

        doThrow(new RuntimeException(errorMessage))
                .when(requestDal).approveRequest(requestId, reviewerId);

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> groupRequestService.approveRequest(requestId, reviewerId)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(requestDal).approveRequest(requestId, reviewerId);
    }

    @Test
    void approveRequest_ThrowsInvalidChatOperationException_WhenRequestAlreadyProcessed() {
        String errorMessage = "Request has already been processed";

        doThrow(new RuntimeException(errorMessage))
                .when(requestDal).approveRequest(requestId, reviewerId);

        InvalidChatOperationException exception = assertThrows(
                InvalidChatOperationException.class,
                () -> groupRequestService.approveRequest(requestId, reviewerId)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(requestDal).approveRequest(requestId, reviewerId);
    }

    // rejectRequest
    @Test
    void rejectRequest_Success() {
        doNothing().when(requestDal).rejectRequest(requestId, reviewerId);

        groupRequestService.rejectRequest(requestId, reviewerId);

        verify(requestDal).rejectRequest(requestId, reviewerId);
    }

    @Test
    void rejectRequest_ThrowsGroupRequestNotFoundException_WhenRequestNotFound() {
        String errorMessage = "Request not found";

        doThrow(new RuntimeException(errorMessage))
                .when(requestDal).rejectRequest(requestId, reviewerId);

        GroupRequestNotFoundException exception = assertThrows(
                GroupRequestNotFoundException.class,
                () -> groupRequestService.rejectRequest(requestId, reviewerId)
        );

        assertThat(exception.getMessage()).contains(requestId.toString());
        verify(requestDal).rejectRequest(requestId, reviewerId);
    }

    @Test
    void rejectRequest_ThrowsInvalidChatOperationException_WhenNotAuthorized() {
        String errorMessage = "Only group creator or admins can reject requests";

        doThrow(new RuntimeException(errorMessage))
                .when(requestDal).rejectRequest(requestId, reviewerId);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> groupRequestService.rejectRequest(requestId, reviewerId)
        );

        verify(requestDal).rejectRequest(requestId, reviewerId);
    }

    // cancelRequest
    @Test
    void cancelRequest_Success() {
        doNothing().when(requestDal).cancelRequest(requestId, userId);

        groupRequestService.cancelRequest(requestId, userId);

        verify(requestDal).cancelRequest(requestId, userId);
    }

    @Test
    void cancelRequest_ThrowsChatAccessDeniedException_WhenCancellingOthersRequest() {
        String errorMessage = "You can only cancel your own requests";
        UUID differentUser = UUID.randomUUID();

        doThrow(new RuntimeException(errorMessage))
                .when(requestDal).cancelRequest(requestId, differentUser);

        ChatAccessDeniedException exception = assertThrows(
                ChatAccessDeniedException.class,
                () -> groupRequestService.cancelRequest(requestId, differentUser)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(requestDal).cancelRequest(requestId, differentUser);
    }

    @Test
    void cancelRequest_ThrowsGroupRequestNotFoundException_WhenRequestNotFound() {
        String errorMessage = "Request not found";

        doThrow(new RuntimeException(errorMessage))
                .when(requestDal).cancelRequest(requestId, userId);

        GroupRequestNotFoundException exception = assertThrows(
                GroupRequestNotFoundException.class,
                () -> groupRequestService.cancelRequest(requestId, userId)
        );

        assertThat(exception.getMessage()).contains(requestId.toString());
        verify(requestDal).cancelRequest(requestId, userId);
    }

    @Test
    void cancelRequest_ThrowsInvalidChatOperationException_WhenRequestAlreadyProcessed() {
        String errorMessage = "You can only cancel your own requests";

        doThrow(new RuntimeException(errorMessage))
                .when(requestDal).cancelRequest(requestId, userId);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> groupRequestService.cancelRequest(requestId, userId)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(requestDal).cancelRequest(requestId, userId);
    }

    // getPendingRequestsForGroup
    @Test
    void getPendingRequestsForGroup_Success() {
        List<GroupRequestDto> expectedRequests = List.of(groupRequestDto);

        when(requestDal.getPendingRequestsForGroup(groupId, reviewerId))
                .thenReturn(expectedRequests);

        List<GroupRequestDto> result = groupRequestService.getPendingRequestsForGroup(groupId, reviewerId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(requestId);
        assertThat(result.get(0).getGroupId()).isEqualTo(groupId);

        verify(requestDal).getPendingRequestsForGroup(groupId, reviewerId);
    }

    @Test
    void getPendingRequestsForGroup_ReturnsEmptyList() {
        when(requestDal.getPendingRequestsForGroup(groupId, reviewerId))
                .thenReturn(List.of());

        List<GroupRequestDto> result = groupRequestService.getPendingRequestsForGroup(groupId, reviewerId);

        assertThat(result).isEmpty();
        verify(requestDal).getPendingRequestsForGroup(groupId, reviewerId);
    }

    @Test
    void getPendingRequestsForGroup_ThrowsGroupNotFoundException_WhenGroupNotFound() {
        String errorMessage = "Group not found";

        when(requestDal.getPendingRequestsForGroup(groupId, reviewerId))
                .thenThrow(new RuntimeException(errorMessage));

        GroupNotFoundException exception = assertThrows(
                GroupNotFoundException.class,
                () -> groupRequestService.getPendingRequestsForGroup(groupId, reviewerId)
        );

        assertThat(exception.getMessage()).contains(groupId.toString());
        verify(requestDal).getPendingRequestsForGroup(groupId, reviewerId);
    }

    // getMyRequests
    @Test
    void getMyRequests_Success() {
        List<GroupRequestDto> expectedRequests = List.of(groupRequestDto);

        when(requestDal.getMyRequests(userId))
                .thenReturn(expectedRequests);

        List<GroupRequestDto> result = groupRequestService.getMyRequests(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);

        verify(requestDal).getMyRequests(userId);
    }

    @Test
    void getMyRequests_ReturnsEmptyList_WhenNoRequests() {
        when(requestDal.getMyRequests(userId))
                .thenReturn(List.of());

        List<GroupRequestDto> result = groupRequestService.getMyRequests(userId);

        assertThat(result).isEmpty();
        verify(requestDal).getMyRequests(userId);
    }

    @Test
    void getMyRequests_WithNullUserId() {
        UUID nullUserId = null;

        when(requestDal.getMyRequests(nullUserId))
                .thenThrow(new RuntimeException("User ID cannot be null"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> groupRequestService.getMyRequests(nullUserId)
        );

        assertThat(exception.getMessage()).contains("User ID cannot be null");
        verify(requestDal).getMyRequests(nullUserId);
    }
}
