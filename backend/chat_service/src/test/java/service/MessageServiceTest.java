package service;

import com.tandem.chat_service.dal.MessageDal;
import com.tandem.chat_service.service.MessageService;
import com.tandem.chat_service.service.exception.ChatAccessDeniedException;
import com.tandem.chat_service.service.exception.MessageNotFoundException;
import com.tandem.chat_service.service.impl.MessageServiceImpl;
import com.tandem.chat_service.service.model.request.SendMessageRequest;
import com.tandem.chat_service.service.model.request.UpdateMessageRequest;
import com.tandem.chat_service.service.model.response.MessageResponse;
import com.tandem.chat_service.service.model.response.PaginatedMessagesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class MessageServiceTest {

    @Mock
    private MessageDal messageDal;

    private MessageService messageService;

    private UUID chatId;
    private UUID requesterId;
    private UUID messageId;
    private UUID senderId;
    private LocalDateTime before;
    private LocalDateTime after;
    private SendMessageRequest sendMessageRequest;
    private UpdateMessageRequest updateMessageRequest;
    private MessageResponse messageResponse;
    private PaginatedMessagesResponse paginatedMessagesResponse;

    @BeforeEach
    void setUp() {
        messageService = new MessageServiceImpl(messageDal);
        initializeTestData();
    }

    private void initializeTestData() {
        chatId = UUID.randomUUID();
        requesterId = UUID.randomUUID();
        messageId = UUID.randomUUID();
        senderId = UUID.randomUUID();
        before = LocalDateTime.now();
        after = before.minusDays(7);

        sendMessageRequest = SendMessageRequest.builder()
                .content("Test message content")
                .build();

        updateMessageRequest = UpdateMessageRequest.builder()
                .content("Updated content")
                .build();

        messageResponse = MessageResponse.builder()
                .messageId(messageId)
                .chatId(chatId)
                .senderId(senderId)
                .content("Test message content")
                .sentAt(LocalDateTime.now())
                .build();

        paginatedMessagesResponse = PaginatedMessagesResponse.builder()
                .messages(List.of(messageResponse))
                .hasMore(false)
                .build();
    }

    // getMessages
    @Test
    void getMessages_Success() {
        int limit = 20;

        when(messageDal.getMessagesByChatId(chatId, requesterId, before, after, limit))
                .thenReturn(paginatedMessagesResponse);

        PaginatedMessagesResponse result = messageService.getMessages(chatId, requesterId, before, after, limit);

        assertThat(result).isNotNull();
        assertThat(result.getMessages()).hasSize(1);
        assertThat(result.isHasMore()).isFalse();
        assertThat(result.getMessages().get(0).getMessageId()).isEqualTo(messageId);

        verify(messageDal).getMessagesByChatId(chatId, requesterId, before, after, limit);
    }

    @Test
    void getMessages_ThrowsChatAccessDeniedException_WhenNotParticipant() {
        int limit = 20;
        String errorMessage = "User is not a participant of this chat";

        when(messageDal.getMessagesByChatId(chatId, requesterId, before, after, limit))
                .thenThrow(new RuntimeException(errorMessage));

        ChatAccessDeniedException exception = assertThrows(
                ChatAccessDeniedException.class,
                () -> messageService.getMessages(chatId, requesterId, before, after, limit)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(messageDal).getMessagesByChatId(chatId, requesterId, before, after, limit);
    }

    @Test
    void getMessages_ThrowsOriginalException_WhenNotParticipantRelated() {
        int limit = 20;
        String errorMessage = "Database connection error";

        when(messageDal.getMessagesByChatId(chatId, requesterId, before, after, limit))
                .thenThrow(new RuntimeException(errorMessage));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> messageService.getMessages(chatId, requesterId, before, after, limit)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(messageDal).getMessagesByChatId(chatId, requesterId, before, after, limit);
    }

    @Test
    void getMessages_WithNullDateParams() {
        int limit = 10;

        when(messageDal.getMessagesByChatId(chatId, requesterId, null, null, limit))
                .thenReturn(paginatedMessagesResponse);

        PaginatedMessagesResponse result = messageService.getMessages(chatId, requesterId, null, null, limit);

        assertThat(result).isNotNull();
        verify(messageDal).getMessagesByChatId(chatId, requesterId, null, null, limit);
    }

    // sendMessage
    @Test
    void sendMessage_Success() {
        when(messageDal.sendMessage(chatId, senderId, sendMessageRequest))
                .thenReturn(messageResponse);

        MessageResponse result = messageService.sendMessage(chatId, senderId, sendMessageRequest);

        assertThat(result).isNotNull();
        assertThat(result.getMessageId()).isEqualTo(messageId);
        assertThat(result.getChatId()).isEqualTo(chatId);
        assertThat(result.getSenderId()).isEqualTo(senderId);
        assertThat(result.getContent()).isEqualTo("Test message content");

        verify(messageDal).sendMessage(chatId, senderId, sendMessageRequest);
    }

    @Test
    void sendMessage_PropagatesDalException() {
        when(messageDal.sendMessage(chatId, senderId, sendMessageRequest))
                .thenThrow(new RuntimeException("Failed to send message"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> messageService.sendMessage(chatId, senderId, sendMessageRequest)
        );

        assertThat(exception.getMessage()).contains("Failed to send message");
        verify(messageDal).sendMessage(chatId, senderId, sendMessageRequest);
    }

    @Test
    void sendMessage_WithEmptyContent() {
        SendMessageRequest emptyRequest = SendMessageRequest.builder()
                .content("")
                .build();

        when(messageDal.sendMessage(chatId, senderId, emptyRequest))
                .thenThrow(new RuntimeException("Message content cannot be empty"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> messageService.sendMessage(chatId, senderId, emptyRequest)
        );

        assertThat(exception.getMessage()).contains("Message content cannot be empty");
        verify(messageDal).sendMessage(chatId, senderId, emptyRequest);
    }

    // updateMessage
    @Test
    void updateMessage_Success() {
        MessageResponse updatedResponse = MessageResponse.builder()
                .messageId(messageId)
                .chatId(chatId)
                .senderId(senderId)
                .content("Updated content")
                .editedAt(LocalDateTime.now())
                .build();

        when(messageDal.updateMessage(messageId, requesterId, updateMessageRequest))
                .thenReturn(updatedResponse);

        MessageResponse result = messageService.updateMessage(messageId, requesterId, updateMessageRequest);

        assertThat(result).isNotNull();
        assertThat(result.getMessageId()).isEqualTo(messageId);
        assertThat(result.getContent()).isEqualTo("Updated content");
        assertThat(result.getEditedAt()).isNotNull();

        verify(messageDal).updateMessage(messageId, requesterId, updateMessageRequest);
    }

    @Test
    void updateMessage_ThrowsMessageNotFoundException_WhenMessageNotFound() {
        when(messageDal.updateMessage(messageId, requesterId, updateMessageRequest))
                .thenThrow(new RuntimeException("Message not found"));

        MessageNotFoundException exception = assertThrows(
                MessageNotFoundException.class,
                () -> messageService.updateMessage(messageId, requesterId, updateMessageRequest)
        );

        assertThat(exception.getMessage()).contains(messageId.toString());
        verify(messageDal).updateMessage(messageId, requesterId, updateMessageRequest);
    }

    @Test
    void updateMessage_ThrowsChatAccessDeniedException_WhenNotAuthorized() {
        String errorMessage = "User is not the author of this message";

        when(messageDal.updateMessage(messageId, requesterId, updateMessageRequest))
                .thenThrow(new RuntimeException(errorMessage));

        ChatAccessDeniedException exception = assertThrows(
                ChatAccessDeniedException.class,
                () -> messageService.updateMessage(messageId, requesterId, updateMessageRequest)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(messageDal).updateMessage(messageId, requesterId, updateMessageRequest);
    }

    @Test
    void updateMessage_PropagatesOtherRuntimeException() {
        String errorMessage = "Database error";

        when(messageDal.updateMessage(messageId, requesterId, updateMessageRequest))
                .thenThrow(new RuntimeException(errorMessage));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> messageService.updateMessage(messageId, requesterId, updateMessageRequest)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(messageDal).updateMessage(messageId, requesterId, updateMessageRequest);
    }

    // deleteMessage
    @Test
    void deleteMessage_Success() {
        doNothing().when(messageDal).deleteMessage(messageId, requesterId);

        messageService.deleteMessage(messageId, requesterId);

        verify(messageDal).deleteMessage(messageId, requesterId);
    }

    @Test
    void deleteMessage_ThrowsMessageNotFoundException_WhenMessageNotFound() {
        doThrow(new RuntimeException("Message not found"))
                .when(messageDal).deleteMessage(messageId, requesterId);

        MessageNotFoundException exception = assertThrows(
                MessageNotFoundException.class,
                () -> messageService.deleteMessage(messageId, requesterId)
        );

        assertThat(exception.getMessage()).contains(messageId.toString());
        verify(messageDal).deleteMessage(messageId, requesterId);
    }

    @Test
    void deleteMessage_ThrowsChatAccessDeniedException_WhenNotAuthorized() {
        String errorMessage = "User is not the author of this message";

        doThrow(new RuntimeException(errorMessage))
                .when(messageDal).deleteMessage(messageId, requesterId);

        ChatAccessDeniedException exception = assertThrows(
                ChatAccessDeniedException.class,
                () -> messageService.deleteMessage(messageId, requesterId)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(messageDal).deleteMessage(messageId, requesterId);
    }

    @Test
    void deleteMessage_PropagatesOtherRuntimeException() {
        String errorMessage = "Database error";

        doThrow(new RuntimeException(errorMessage))
                .when(messageDal).deleteMessage(messageId, requesterId);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> messageService.deleteMessage(messageId, requesterId)
        );

        assertThat(exception.getMessage()).contains(errorMessage);
        verify(messageDal).deleteMessage(messageId, requesterId);
    }

    @Test
    void deleteMessage_WithNullRequesterId() {
        UUID nullRequesterId = null;

        doThrow(new RuntimeException("Requester ID cannot be null"))
                .when(messageDal).deleteMessage(messageId, nullRequesterId);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> messageService.deleteMessage(messageId, nullRequesterId)
        );

        assertThat(exception.getMessage()).contains("Requester ID cannot be null");
        verify(messageDal).deleteMessage(messageId, nullRequesterId);
    }
}