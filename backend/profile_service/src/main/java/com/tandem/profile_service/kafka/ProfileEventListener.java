package com.tandem.profile_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.profile_service.config.TandemKafkaConfig;
import com.tandem.profile_service.dto.ProfileEventDto;
import com.tandem.profile_service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileEventListener {

    private final ProfileService profileService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = TandemKafkaConfig.TOPIC_USER_REGISTERED,
            groupId = "profile-service-consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserRegistered(
            ConsumerRecord<String, String> record,
            Acknowledgment acknowledgment
    ) {
        log.info("EVENT: user.registered");
        try {
            ProfileEventDto event = objectMapper.readValue(record.value(), ProfileEventDto.class);
            profileService.createProfileFromRegistrationEvent(event);

            acknowledgment.acknowledge();
            log.info("Profile created for userId={}", event.getUserId());

        } catch (Exception e) {
            log.error("Failed to create profile", e);
        }
    }
}

