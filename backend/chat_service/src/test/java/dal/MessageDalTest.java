package dal;

import com.tandem.chat_service.dal.MessageDal;
import com.tandem.chat_service.dal.impl.MessageDalImpl;
import com.tandem.chat_service.dal.mapper.MessageEntityMapper;
import com.tandem.chat_service.dao.ChatDao;
import com.tandem.chat_service.dao.ChatParticipantDao;
import com.tandem.chat_service.dao.MessageDao;
import com.tandem.chat_service.dao.enums.MessageType;
import com.tandem.chat_service.dao.model.ChatParticipantEntity;
import com.tandem.chat_service.dao.model.MessageEntity;
import com.tandem.chat_service.integration.InterestEventPublisher;
import com.tandem.chat_service.service.model.dto.MessageMetadata;
import com.tandem.chat_service.service.model.request.SendMessageRequest;
import com.tandem.chat_service.service.model.request.UpdateMessageRequest;
import com.tandem.chat_service.service.model.response.MessageResponse;
import com.tandem.chat_service.service.model.response.PaginatedMessagesResponse;
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
class MessageDalTest {

    @Mock
    private MessageDao messageDao;
    @Mock
    private ChatParticipantDao participantDao;
    @Mock
    private ChatDao chatDao;
    @Mock
    private MessageEntityMapper mapper;
    @Mock
    private InterestEventPublisher publisher;

    private MessageDal messageDal;

    private UUID chatId;
    private UUID requesterId;
    private UUID messageId;
    private MessageEntity messageEntity;
    private SendMessageRequest sendRequest;
    private UpdateMessageRequest updateRequest;
    private MessageResponse messageResponse;

    @BeforeEach
    void setUp() {
        messageDal = new MessageDalImpl(messageDao, participantDao, chatDao, mapper, publisher);

        chatId = UUID.randomUUID();
        requesterId = UUID.randomUUID();
        messageId = UUID.randomUUID();

        messageEntity = MessageEntity.builder()
                .id(messageId)
                .chatId(chatId)
                .senderId(requesterId)
                .content("Test Content")
                .messageType(MessageType.TEXT)
                .sentAt(LocalDateTime.now())
                .build();

        sendRequest = SendMessageRequest.builder()
                .content("Test Content")
                .type(MessageType.TEXT)
                .build();

        updateRequest = UpdateMessageRequest.builder()
                .content("Updated Content")
                .build();

        messageResponse = MessageResponse.builder()
                .messageId(messageId)
                .content("Test Content")
                .build();
    }

    // getMessagesByChatId
    @Test
    void getMessagesByChatId_Success() {
        when(participantDao.isParticipant(chatId, requesterId)).thenReturn(true);
        when(messageDao.findMessagesByChatIdBefore(eq(chatId), any(LocalDateTime.class), eq(21)))
                .thenReturn(List.of(messageEntity));

        PaginatedMessagesResponse mockResponse = PaginatedMessagesResponse.builder()
                .messages(List.of(messageResponse))
                .hasMore(false)
                .build();
        when(mapper.mapToPaginatedResponse(anyList(), eq(false))).thenReturn(mockResponse);

        PaginatedMessagesResponse result = messageDal.getMessagesByChatId(chatId, requesterId, null, null, 20);

        assertThat(result).isNotNull();
        assertThat(result.getMessages()).hasSize(1);
    }

    @Test
    void getMessagesByChatId_ThrowsException_WhenNotParticipant() {
        when(participantDao.isParticipant(chatId, requesterId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> messageDal.getMessagesByChatId(chatId, requesterId, null, null, 20));

        assertThat(exception.getMessage()).isEqualTo("User is not a participant of this chat");
        verifyNoInteractions(messageDao);
    }

    // sendMessage
    @Test
    void sendMessage_Success() {
        when(participantDao.isParticipant(chatId, requesterId)).thenReturn(true);
        when(mapper.mapToEntity(chatId, requesterId, sendRequest)).thenReturn(messageEntity);
        doNothing().when(messageDao).insert(messageEntity);
        when(mapper.mapToResponse(messageEntity)).thenReturn(messageResponse);

        ChatParticipantEntity participant = ChatParticipantEntity.builder()
                .userId(UUID.randomUUID())
                .build();

        when(participantDao.findByChatId(chatId)).thenReturn(List.of(participant));

        MessageResponse result = messageDal.sendMessage(chatId, requesterId, sendRequest);

        assertThat(result).isNotNull();
        verify(messageDao).insert(messageEntity);
        verify(publisher).publishMessageSent(eq(messageEntity), anyList());
    }

    @Test
    void sendMessage_ThrowsException_WhenNotParticipant() {
        when(participantDao.isParticipant(chatId, requesterId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> messageDal.sendMessage(chatId, requesterId, sendRequest));

        assertThat(exception.getMessage()).isEqualTo("User is not a participant of this chat");
        verifyNoInteractions(messageDao);
    }

    @Test
    void sendMessage_ThrowsException_WhenMetadataMissingForFile() {
        SendMessageRequest fileRequest = SendMessageRequest.builder()
                .type(MessageType.FILE)
                .build();

        when(participantDao.isParticipant(chatId, requesterId)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> messageDal.sendMessage(chatId, requesterId, fileRequest));

        assertThat(exception.getMessage()).contains("Metadata is required for type");
        verifyNoInteractions(messageDao);
    }

    // updateMessage
    @Test
    void updateMessage_Success() {
        when(messageDao.findById(messageId)).thenReturn(Optional.of(messageEntity));
        when(mapper.mapToUpdatedEntity(messageEntity, updateRequest)).thenReturn(messageEntity);
        when(mapper.mapToResponse(messageEntity)).thenReturn(messageResponse);

        MessageResponse result = messageDal.updateMessage(messageId, requesterId, updateRequest);

        assertThat(result).isNotNull();
        verify(messageDao).update(messageEntity);
    }

    @Test
    void updateMessage_ThrowsException_WhenMessageNotFound() {
        when(messageDao.findById(messageId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> messageDal.updateMessage(messageId, requesterId, updateRequest));

        assertThat(exception.getMessage()).isEqualTo("Message not found");
        verify(messageDao, never()).update(any());
    }

    @Test
    void updateMessage_ThrowsException_WhenNotSender() {
        MessageEntity foreignMessage = MessageEntity.builder().senderId(UUID.randomUUID()).build();
        when(messageDao.findById(messageId)).thenReturn(Optional.of(foreignMessage));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> messageDal.updateMessage(messageId, requesterId, updateRequest));

        assertThat(exception.getMessage()).isEqualTo("Only the message sender can edit it");
        verify(messageDao, never()).update(any());
    }

    // deleteMessage
    @Test
    void deleteMessage_Success() {
        when(messageDao.findById(messageId)).thenReturn(Optional.of(messageEntity));

        messageDal.deleteMessage(messageId, requesterId);

        verify(messageDao).softDelete(eq(messageId), any(LocalDateTime.class));
    }

    @Test
    void deleteMessage_ThrowsException_WhenMessageNotFound() {
        when(messageDao.findById(messageId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> messageDal.deleteMessage(messageId, requesterId));

        assertThat(exception.getMessage()).isEqualTo("Message not found");
        verify(messageDao, never()).softDelete(any(), any());
    }

    @Test
    void deleteMessage_ThrowsException_WhenNotSender() {
        MessageEntity foreignMessage = MessageEntity.builder().senderId(UUID.randomUUID()).build();
        when(messageDao.findById(messageId)).thenReturn(Optional.of(foreignMessage));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> messageDal.deleteMessage(messageId, requesterId));

        assertThat(exception.getMessage()).isEqualTo("Only the message sender can delete it");
        verify(messageDao, never()).softDelete(any(), any());
    }

    // getMessagesByChatId
    @Test
    void getMessagesByChatId_WithAfterDate_Success() {
        LocalDateTime afterDate = LocalDateTime.now();
        when(participantDao.isParticipant(chatId, requesterId)).thenReturn(true);
        when(messageDao.findMessagesByChatIdAfter(eq(chatId), eq(afterDate), eq(21)))
                .thenReturn(List.of(messageEntity));
        when(mapper.mapToPaginatedResponse(anyList(), eq(false))).thenReturn(PaginatedMessagesResponse.builder().build());

        messageDal.getMessagesByChatId(chatId, requesterId, null, afterDate, 20);

        verify(messageDao).findMessagesByChatIdAfter(eq(chatId), eq(afterDate), eq(21));
    }

    @Test
    void getMessagesByChatId_WithNoDates_Success() {
        when(participantDao.isParticipant(chatId, requesterId)).thenReturn(true);
        when(messageDao.findMessagesByChatIdBefore(eq(chatId), any(LocalDateTime.class), eq(21)))
                .thenReturn(List.of(messageEntity));
        when(mapper.mapToPaginatedResponse(anyList(), eq(false))).thenReturn(PaginatedMessagesResponse.builder().build());

        messageDal.getMessagesByChatId(chatId, requesterId, null, null, 20);

        verify(messageDao).findMessagesByChatIdBefore(eq(chatId), any(LocalDateTime.class), eq(21));
    }

    @Test
    void getMessagesByChatId_HasMore_Success() {
        when(participantDao.isParticipant(chatId, requesterId)).thenReturn(true);
        List<MessageEntity> threeMessages = List.of(messageEntity, messageEntity, messageEntity);
        when(messageDao.findMessagesByChatIdBefore(eq(chatId), any(LocalDateTime.class), eq(3)))
                .thenReturn(threeMessages);

        when(mapper.mapToPaginatedResponse(anyList(), eq(true))).thenReturn(PaginatedMessagesResponse.builder().hasMore(true).build());

        PaginatedMessagesResponse result = messageDal.getMessagesByChatId(chatId, requesterId, null, null, 2);

        assertThat(result.isHasMore()).isTrue();
    }

    // sendMessage
    @Test
    void sendMessage_ThrowsException_WhenLinkMissingUrl() {
        SendMessageRequest request = SendMessageRequest.builder()
                .type(MessageType.LINK)
                .metadata(MessageMetadata.builder().build()) // Пустые метаданные
                .build();
        when(participantDao.isParticipant(chatId, requesterId)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> messageDal.sendMessage(chatId, requesterId, request));

        assertThat(exception.getMessage()).contains("linkUrl is required for LINK message");
    }

    @Test
    void sendMessage_ThrowsException_WhenCodeMissingData() {
        SendMessageRequest request = SendMessageRequest.builder()
                .type(MessageType.CODE)
                .metadata(MessageMetadata.builder().code("System.out").build()) // Нет language
                .build();
        when(participantDao.isParticipant(chatId, requesterId)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> messageDal.sendMessage(chatId, requesterId, request));

        assertThat(exception.getMessage()).contains("code and language are required for CODE message");
    }

    @Test
    void sendMessage_ThrowsException_WhenEmojiMissingData() {
        SendMessageRequest request = SendMessageRequest.builder()
                .type(MessageType.EMOJI)
                .metadata(MessageMetadata.builder().build())
                .build();
        when(participantDao.isParticipant(chatId, requesterId)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> messageDal.sendMessage(chatId, requesterId, request));

        assertThat(exception.getMessage()).contains("emoji is required for EMOJI message");
    }

    @Test
    void sendMessage_ThrowsException_WhenStickerMissingData() {
        SendMessageRequest request = SendMessageRequest.builder()
                .type(MessageType.STICKER)
                .metadata(MessageMetadata.builder().build())
                .build();
        when(participantDao.isParticipant(chatId, requesterId)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> messageDal.sendMessage(chatId, requesterId, request));

        assertThat(exception.getMessage()).contains("stickerId or stickerUrl is required for STICKER message");
    }
}