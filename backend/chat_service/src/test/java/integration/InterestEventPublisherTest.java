package integration;

import com.tandem.chat_service.dao.enums.GroupRequestStatus;
import com.tandem.chat_service.dao.enums.GroupVisibility;
import com.tandem.chat_service.dao.model.GroupEntity;
import com.tandem.chat_service.dao.model.MessageEntity;
import com.tandem.chat_service.integration.InterestEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterestEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private InterestEventPublisher publisher;

    @Captor
    private ArgumentCaptor<Message<Map<String, Object>>> messageCaptor;

    private UUID messageId, chatId, senderId, recipientId1, recipientId2;
    private UUID groupId, creatorId, targetUserId, joinedUserId;
    private MessageEntity messageEntity;
    private GroupEntity groupEntity;

    @BeforeEach
    void setUp() {
        messageId = UUID.randomUUID();
        chatId = UUID.randomUUID();
        senderId = UUID.randomUUID();
        recipientId1 = UUID.randomUUID();
        recipientId2 = UUID.randomUUID();

        groupId = UUID.randomUUID();
        creatorId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();
        joinedUserId = UUID.randomUUID();

        messageEntity = MessageEntity.builder()
                .id(messageId)
                .chatId(chatId)
                .senderId(senderId)
                .build();

        groupEntity = GroupEntity.builder()
                .id(groupId)
                .name("Developers Group")
                .creatorId(creatorId)
                .visibility(GroupVisibility.PUBLIC)
                .build();
    }

    // publishMessageSent
    @Test
    void publishMessageSent_Success() {
        List<UUID> recipients = List.of(recipientId1, recipientId2);

        publisher.publishMessageSent(messageEntity, recipients);

        verify(kafkaTemplate, times(2)).send(messageCaptor.capture());

        List<Message<Map<String, Object>>> sentMessages = messageCaptor.getAllValues();

        Map<String, Object> payload1 = sentMessages.get(0).getPayload();
        assertThat(sentMessages.get(0).getHeaders().get(KafkaHeaders.TOPIC)).isEqualTo("message.sent");
        assertThat(payload1.get("eventType")).isEqualTo("message.sent");
        assertThat(payload1.get("recipientId")).isEqualTo(recipientId1.toString());
        assertThat(payload1.get("senderId")).isEqualTo(senderId.toString());

        Map<String, Object> payload2 = sentMessages.get(1).getPayload();
        assertThat(payload2.get("recipientId")).isEqualTo(recipientId2.toString());
    }

    @Test
    void publishMessageSent_ThrowsException_WhenKafkaFails() {
        List<UUID> recipients = List.of(recipientId1);

        doThrow(new RuntimeException("Kafka connection down"))
                .when(kafkaTemplate).send(any(Message.class));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisher.publishMessageSent(messageEntity, recipients));

        assertThat(exception.getMessage()).isEqualTo("Failed to publish event");
        verify(kafkaTemplate).send(any(Message.class));
    }

    // publishChatCreated
    @Test
    void publishChatCreated_Success() {
        List<UUID> participants = List.of(senderId, recipientId1);

        publisher.publishChatCreated(chatId, participants);

        verify(kafkaTemplate).send(messageCaptor.capture());
        Message<Map<String, Object>> message = messageCaptor.getValue();
        Map<String, Object> payload = message.getPayload();

        assertThat(message.getHeaders().get(KafkaHeaders.TOPIC)).isEqualTo("chat.created");
        assertThat(payload.get("eventType")).isEqualTo("chat.created");
        assertThat(payload.get("chatId")).isEqualTo(chatId.toString());

        @SuppressWarnings("unchecked")
        List<String> participantIds = (List<String>) payload.get("participantIds");
        assertThat(participantIds).contains(senderId.toString(), recipientId1.toString());
    }

    @Test
    void publishChatCreated_ThrowsException_WhenKafkaFails() {
        List<UUID> participants = List.of(senderId, recipientId1);

        doThrow(new RuntimeException("Kafka timeout"))
                .when(kafkaTemplate).send(any(Message.class));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisher.publishChatCreated(chatId, participants));

        assertThat(exception.getMessage()).isEqualTo("Failed to publish event");
    }

    // publishGroupRequestEvent
    @Test
    void publishGroupRequestEvent_Success() {
        UUID requestId = UUID.randomUUID();

        publisher.publishGroupRequestEvent(requestId, groupId, targetUserId, GroupRequestStatus.APPROVED);

        verify(kafkaTemplate).send(messageCaptor.capture());
        Map<String, Object> payload = messageCaptor.getValue().getPayload();

        assertThat(messageCaptor.getValue().getHeaders().get(KafkaHeaders.TOPIC)).isEqualTo("group.request.updated");
        assertThat(payload.get("eventType")).isEqualTo("group.request.updated");
        assertThat(payload.get("requestId")).isEqualTo(requestId.toString());
        assertThat(payload.get("status")).isEqualTo(GroupRequestStatus.APPROVED.getValue());
        assertThat(payload.get("targetUserId")).isEqualTo(targetUserId.toString());
    }

    @Test
    void publishGroupRequestEvent_ThrowsException_WhenKafkaFails() {
        UUID requestId = UUID.randomUUID();

        doThrow(new RuntimeException("Broker not available"))
                .when(kafkaTemplate).send(any(Message.class));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisher.publishGroupRequestEvent(requestId, groupId, targetUserId, GroupRequestStatus.PENDING));

        assertThat(exception.getMessage()).isEqualTo("Failed to publish event");
    }

    // publishUserJoinedGroup
    @Test
    void publishUserJoinedGroup_Success() {
        publisher.publishUserJoinedGroup(groupId, creatorId, joinedUserId);

        verify(kafkaTemplate).send(messageCaptor.capture());
        Map<String, Object> payload = messageCaptor.getValue().getPayload();

        assertThat(messageCaptor.getValue().getHeaders().get(KafkaHeaders.TOPIC)).isEqualTo("group.events");
        assertThat(payload.get("eventType")).isEqualTo("group.user.joined");
        assertThat(payload.get("ownerId")).isEqualTo(creatorId.toString());
        assertThat(payload.get("joinedUserId")).isEqualTo(joinedUserId.toString());
    }

    @Test
    void publishUserJoinedGroup_ThrowsException_WhenKafkaFails() {
        doThrow(new RuntimeException("Kafka error"))
                .when(kafkaTemplate).send(any(Message.class));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisher.publishUserJoinedGroup(groupId, creatorId, joinedUserId));

        assertThat(exception.getMessage()).isEqualTo("Failed to publish event");
    }

    // publishGroupCreated
    @Test
    void publishGroupCreated_Success() {
        List<String> interests = List.of("Java", "Spring Boot");

        publisher.publishGroupCreated(groupEntity, interests);

        verify(kafkaTemplate).send(messageCaptor.capture());
        Map<String, Object> payload = messageCaptor.getValue().getPayload();

        assertThat(messageCaptor.getValue().getHeaders().get(KafkaHeaders.TOPIC)).isEqualTo("group.created");
        assertThat(payload.get("eventType")).isEqualTo("group.created");
        assertThat(payload.get("groupName")).isEqualTo("Developers Group");
        assertThat(payload.get("visibility")).isEqualTo("PUBLIC");
        assertThat(payload.get("interestTags")).isEqualTo(interests.toString());
    }

    @Test
    void publishGroupCreated_ThrowsException_WhenKafkaFails() {
        List<String> interests = List.of("Java");

        doThrow(new RuntimeException("Serialization exception"))
                .when(kafkaTemplate).send(any(Message.class));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisher.publishGroupCreated(groupEntity, interests));

        assertThat(exception.getMessage()).isEqualTo("Failed to publish event");
    }
}