package com.tandem.notification_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.notification_service.dal.NotificationDal;
import com.tandem.notification_service.service.model.internal.DeliveryQueueTask;
import com.tandem.notification_service.service.model.request.DeliveryStatusCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDeliveryServiceImplTest {

    @Mock
    private NotificationDal notificationDal;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ZSetOperations<String, String> zSetOperations;
    @Mock
    private ListOperations<String, String> listOperations;

    @InjectMocks
    private NotificationDeliveryServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(service, "batchSize", 100);
        ReflectionTestUtils.setField(service, "maxRetries", 2);
        ReflectionTestUtils.setField(service, "baseBackoffMs", 10L);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void deliverCreatesPendingStatusAndQueuesEachChannel() {
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        service.deliver(notificationId, userId, "Title", "Body", Arrays.asList("PUSH", null));

        ArgumentCaptor<DeliveryStatusCreateRequest> captor = ArgumentCaptor.forClass(DeliveryStatusCreateRequest.class);
        verify(notificationDal, atLeast(2)).createDeliveryStatus(captor.capture());
        List<DeliveryStatusCreateRequest> values = captor.getAllValues();

        assertThat(values).hasSize(2);
        assertThat(values.get(0).getChannel()).isEqualTo("push");
        assertThat(values.get(1).getChannel()).isEqualTo("in-app");
        assertThat(values).allMatch(v -> "pending".equals(v.getStatus()) && "queued".equals(v.getProviderResponse()));
        verify(zSetOperations, atLeast(2)).add(eq("notifications:delivery:zset"), any(), anyDouble());
    }

    @Test
    void processQueuedDeliveriesWritesSentStatusForValidTask() throws Exception {
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String payload = new ObjectMapper().writeValueAsString(
                DeliveryQueueTask.builder()
                        .notificationId(notificationId)
                        .userId(userId)
                        .title("Hello")
                        .body("Body")
                        .channel("email")
                        .attempt(0)
                        .availableAtEpochMs(System.currentTimeMillis())
                        .build()
        );
        Set<String> duePayloads = new LinkedHashSet<>();
        duePayloads.add(payload);
        when(zSetOperations.rangeByScore(eq("notifications:delivery:zset"), eq(0.0), anyDouble(), eq(0L), eq(100L)))
                .thenReturn(duePayloads);
        when(zSetOperations.remove("notifications:delivery:zset", payload)).thenReturn(1L);

        service.processQueuedDeliveries();

        ArgumentCaptor<DeliveryStatusCreateRequest> captor = ArgumentCaptor.forClass(DeliveryStatusCreateRequest.class);
        verify(notificationDal, atLeast(1)).createDeliveryStatus(captor.capture());
        assertThat(captor.getAllValues())
                .anyMatch(v -> "sent".equals(v.getStatus()) && "email".equals(v.getChannel()));
    }

    @Test
    void processQueuedDeliveriesMovesToDlqWhenRetriesExceeded() throws Exception {
        ReflectionTestUtils.setField(service, "maxRetries", 1);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String payload = new ObjectMapper().writeValueAsString(
                DeliveryQueueTask.builder()
                        .notificationId(notificationId)
                        .userId(userId)
                        .title("Hello")
                        .body("Body")
                        .channel("invalid")
                        .attempt(0)
                        .availableAtEpochMs(System.currentTimeMillis())
                        .build()
        );
        Set<String> duePayloads = new LinkedHashSet<>();
        duePayloads.add(payload);
        when(zSetOperations.rangeByScore(eq("notifications:delivery:zset"), eq(0.0), anyDouble(), eq(0L), eq(100L)))
                .thenReturn(duePayloads);
        when(zSetOperations.remove("notifications:delivery:zset", payload)).thenReturn(1L);

        service.processQueuedDeliveries();

        verify(listOperations).rightPush(eq("notifications:delivery:dlq"), any());
        ArgumentCaptor<DeliveryStatusCreateRequest> captor = ArgumentCaptor.forClass(DeliveryStatusCreateRequest.class);
        verify(notificationDal, atLeast(1)).createDeliveryStatus(captor.capture());
        assertThat(captor.getAllValues()).anyMatch(v -> "failed".equals(v.getStatus()));
    }

    @Test
    void processQueuedDeliveriesReturnsWhenNothingDue() {
        when(zSetOperations.rangeByScore(eq("notifications:delivery:zset"), eq(0.0), anyDouble(), eq(0L), eq(100L)))
                .thenReturn(null);

        service.processQueuedDeliveries();

        verify(zSetOperations, never()).remove(any(), any());
        verify(notificationDal, never()).createDeliveryStatus(any());
    }

    @Test
    void processQueuedDeliveriesIgnoresMalformedPayload() {
        Set<String> duePayloads = new LinkedHashSet<>();
        duePayloads.add("{not-json");
        when(zSetOperations.rangeByScore(eq("notifications:delivery:zset"), eq(0.0), anyDouble(), eq(0L), eq(100L)))
                .thenReturn(duePayloads);
        when(zSetOperations.remove("notifications:delivery:zset", "{not-json")).thenReturn(1L);

        service.processQueuedDeliveries();

        verify(notificationDal, never()).createDeliveryStatus(any());
    }

    @Test
    void processQueuedDeliveriesSkipsWhenConcurrentWorkerAlreadyRemovedTask() {
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String payload;
        try {
            payload = new ObjectMapper().writeValueAsString(
                    DeliveryQueueTask.builder()
                            .notificationId(notificationId)
                            .userId(userId)
                            .title("Hello")
                            .body("Body")
                            .channel("email")
                            .attempt(0)
                            .availableAtEpochMs(System.currentTimeMillis())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Set<String> duePayloads = new LinkedHashSet<>();
        duePayloads.add(payload);
        when(zSetOperations.rangeByScore(eq("notifications:delivery:zset"), eq(0.0), anyDouble(), eq(0L), eq(100L)))
                .thenReturn(duePayloads);
        when(zSetOperations.remove("notifications:delivery:zset", payload)).thenReturn(0L);

        service.processQueuedDeliveries();

        verify(notificationDal, never()).createDeliveryStatus(any());
    }

    @Test
    void processQueuedDeliveriesReschedulesTaskBeforeMaxRetries() throws Exception {
        ReflectionTestUtils.setField(service, "maxRetries", 5);
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String payload = new ObjectMapper().writeValueAsString(
                DeliveryQueueTask.builder()
                        .notificationId(notificationId)
                        .userId(userId)
                        .title("Hello")
                        .body("Body")
                        .channel("broken-channel")
                        .attempt(0)
                        .availableAtEpochMs(System.currentTimeMillis())
                        .build()
        );
        Set<String> duePayloads = new LinkedHashSet<>();
        duePayloads.add(payload);
        when(zSetOperations.rangeByScore(eq("notifications:delivery:zset"), eq(0.0), anyDouble(), eq(0L), eq(100L)))
                .thenReturn(duePayloads);
        when(zSetOperations.remove("notifications:delivery:zset", payload)).thenReturn(1L);

        service.processQueuedDeliveries();

        verify(zSetOperations, times(1)).add(eq("notifications:delivery:zset"), any(), anyDouble());
        ArgumentCaptor<DeliveryStatusCreateRequest> captor = ArgumentCaptor.forClass(DeliveryStatusCreateRequest.class);
        verify(notificationDal, times(1)).createDeliveryStatus(captor.capture());
        assertThat(captor.getValue().getProviderResponse()).isEqualTo("retry scheduled");
        assertThat(captor.getValue().getRetryCount()).isEqualTo(1);
    }

    @Test
    void deliverProcessesPushAndSmsChannels() {
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        service.deliver(notificationId, userId, "T", "B", List.of("push", "sms"));

        ArgumentCaptor<DeliveryStatusCreateRequest> captor = ArgumentCaptor.forClass(DeliveryStatusCreateRequest.class);
        verify(notificationDal, times(2)).createDeliveryStatus(captor.capture());
        assertThat(captor.getAllValues()).extracting(DeliveryStatusCreateRequest::getChannel).containsExactlyInAnyOrder("push", "sms");
        verify(zSetOperations, times(2)).add(eq("notifications:delivery:zset"), any(), anyDouble());
    }
}
