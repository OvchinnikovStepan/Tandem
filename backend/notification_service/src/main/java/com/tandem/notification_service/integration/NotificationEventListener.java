package com.tandem.notification_service.integration;

import com.tandem.notification_service.service.NotificationEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationEventService notificationEventService;

    @KafkaListener(
            topics = {
                    "${tandem.kafka.topic.user-registered}",
                    "${tandem.kafka.topic.profile-onboarding-completed}",
                    "${tandem.kafka.topic.message-sent}",
                    "${tandem.kafka.topic.chat-created}",
                    "${tandem.kafka.topic.group-created}",
                    "${tandem.kafka.topic.member-joined}",
                    "${tandem.kafka.topic.member-banned}",
                    "${tandem.kafka.topic.group-message-sent}"
            },
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleEvent(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            log.info("Received notification event topic={} key={}", record.topic(), record.key());
            notificationEventService.processEvent(record.topic(), record.value());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process notification event topic={}", record.topic(), e);
        }
    }
}
