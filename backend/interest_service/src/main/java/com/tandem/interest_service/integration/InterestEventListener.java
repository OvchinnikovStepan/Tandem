package com.tandem.interest_service.integration;

import com.tandem.interest_service.configuration.TandemKafkaConfig;
import com.tandem.interest_service.service.UserInterestService;
import com.tandem.interest_service.service.model.request.UserInterestRequest;
import com.tandem.interest_service.service.model.response.UserInterestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterestEventListener {

    private final UserInterestService userInterestService;

    @KafkaListener(
            topics = TandemKafkaConfig.TOPIC_ONBOARDING_COMPLETED,
            groupId = "interest-service-consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOnboardingCompleted(
            ConsumerRecord<String, String> record,
            Acknowledgment acknowledgment
    ) {
        log.info("EVENT: profile.onboarding.completed");
        try {
            List<UserInterestRequest> requests = userInterestService.parseToUserInterestRequests(record.value());

            if (requests.isEmpty()) {
                log.info("No valid interests to add, acknowledging message");
                acknowledgment.acknowledge();
                return;
            }

            List<UserInterestResponse> created = userInterestService.addUserInterest(requests);

            log.info("Successfully added {} interests", created.size());

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to create profile", e);
        }
    }
}

