package com.tandem.notification_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.notification_service.dal.NotificationDal;
import com.tandem.notification_service.service.NotificationDeliveryService;
import com.tandem.notification_service.service.model.internal.DeliveryQueueTask;
import com.tandem.notification_service.service.model.request.DeliveryStatusCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDeliveryServiceImpl implements NotificationDeliveryService {

    private static final String DELIVERY_QUEUE_KEY = "notifications:delivery:zset";
    private static final String DELIVERY_DLQ_KEY = "notifications:delivery:dlq";

    private final NotificationDal notificationDal;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${notification.delivery.batch-size:100}")
    private int batchSize;

    @Value("${notification.delivery.max-retries:5}")
    private int maxRetries;

    @Value("${notification.delivery.base-backoff-ms:250}")
    private long baseBackoffMs;

    @Override
    public void deliver(UUID notificationId, UUID userId, String title, String body, List<String> channels) {
        for (String channel : channels) {
            String normalizedChannel = channel == null ? "in-app" : channel.trim().toLowerCase();
            long now = System.currentTimeMillis();
            DeliveryQueueTask task = DeliveryQueueTask.builder()
                    .notificationId(notificationId)
                    .userId(userId)
                    .title(title)
                    .body(body)
                    .channel(normalizedChannel)
                    .attempt(0)
                    .availableAtEpochMs(now)
                    .build();

            notificationDal.createDeliveryStatus(DeliveryStatusCreateRequest.builder()
                    .notificationId(notificationId)
                    .channel(normalizedChannel)
                    .status("pending")
                    .providerResponse("queued")
                    .retryCount(0)
                    .build());

            pushToQueue(task, now);
        }
    }

    @Scheduled(
            fixedDelayString = "${notification.delivery.poll-interval-ms:500}",
            initialDelayString = "${notification.delivery.initial-delay-ms:1000}"
    )
    public void processQueuedDeliveries() {
        long now = System.currentTimeMillis();
        Set<String> payloads = redisTemplate.opsForZSet().rangeByScore(DELIVERY_QUEUE_KEY, 0, now, 0, batchSize);
        if (payloads == null || payloads.isEmpty()) {
            return;
        }

        for (String payload : payloads) {
            Long removed = redisTemplate.opsForZSet().remove(DELIVERY_QUEUE_KEY, payload);
            if (removed == null || removed == 0) {
                continue;
            }
            DeliveryQueueTask task = parseTask(payload);
            if (task == null) {
                continue;
            }
            processTask(task, now);
        }
    }

    private void processTask(DeliveryQueueTask task, long now) {
        try {
            emulateChannelDelivery(task.getChannel(), task.getUserId(), task.getTitle(), task.getBody());
            notificationDal.createDeliveryStatus(DeliveryStatusCreateRequest.builder()
                    .notificationId(task.getNotificationId())
                    .channel(task.getChannel())
                    .status("sent")
                    .providerResponse("delivered by stub provider")
                    .retryCount(task.getAttempt())
                    .build());
        } catch (Exception ex) {
            int nextAttempt = task.getAttempt() + 1;
            if (nextAttempt < maxRetries) {
                long delayMs = computeBackoffMs(nextAttempt);
                task.setAttempt(nextAttempt);
                task.setAvailableAtEpochMs(now + delayMs);
                pushToQueue(task, task.getAvailableAtEpochMs());
                notificationDal.createDeliveryStatus(DeliveryStatusCreateRequest.builder()
                        .notificationId(task.getNotificationId())
                        .channel(task.getChannel())
                        .status("pending")
                        .providerResponse("retry scheduled")
                        .errorMessage(ex.getMessage())
                        .retryCount(nextAttempt)
                        .build());
            } else {
                log.error("Delivery moved to DLQ notificationId={} channel={}", task.getNotificationId(), task.getChannel(), ex);
                pushToDlq(task);
                notificationDal.createDeliveryStatus(DeliveryStatusCreateRequest.builder()
                        .notificationId(task.getNotificationId())
                        .channel(task.getChannel())
                        .status("failed")
                        .providerResponse("stub provider failure")
                        .errorMessage(ex.getMessage())
                        .retryCount(nextAttempt)
                        .build());
            }
        }
    }

    private void emulateChannelDelivery(String channel, UUID userId, String title, String body) {
        switch (channel) {
            case "push" -> log.info("Push notification sent userId={} title='{}' body='{}'", userId, title, body);
            case "email" -> log.info("Email notification sent userId={} title='{}' body='{}'", userId, title, body);
            case "sms" -> log.info("SMS notification sent userId={} title='{}' body='{}'", userId, title, body);
            case "in-app" -> log.info("In-app notification stored userId={} title='{}'", userId, title);
            default -> throw new IllegalArgumentException("Unsupported delivery channel: " + channel);
        }
    }

    private void pushToQueue(DeliveryQueueTask task, long score) {
        try {
            redisTemplate.opsForZSet().add(DELIVERY_QUEUE_KEY, objectMapper.writeValueAsString(task), score);
        } catch (JsonProcessingException e) {
            log.error("Unable to serialize delivery task", e);
        }
    }

    private void pushToDlq(DeliveryQueueTask task) {
        try {
            redisTemplate.opsForList().rightPush(DELIVERY_DLQ_KEY, objectMapper.writeValueAsString(task));
        } catch (JsonProcessingException e) {
            log.error("Unable to serialize DLQ task", e);
        }
    }

    private DeliveryQueueTask parseTask(String payload) {
        try {
            return objectMapper.readValue(payload, DeliveryQueueTask.class);
        } catch (JsonProcessingException e) {
            log.error("Unable to parse delivery task payload", e);
            return null;
        }
    }

    private long computeBackoffMs(int attempt) {
        return baseBackoffMs * (1L << Math.max(attempt - 1, 0));
    }
}
