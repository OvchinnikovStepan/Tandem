package com.tandem.interest_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.interest_service.configuration.TandemKafkaConfig;
import com.tandem.interest_service.integration.model.GroupCreatedEvent;
import com.tandem.interest_service.integration.model.OnboardingCompletedEvent;
import com.tandem.interest_service.service.DirectoryService;
import com.tandem.interest_service.service.GroupInterestService;
import com.tandem.interest_service.service.UserInterestService;
import com.tandem.interest_service.service.model.request.GroupInterestRequest;
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

    private final ObjectMapper objectMapper;
    private final UserInterestService userInterestService;
    private final GroupInterestService groupInterestService;
    private final DirectoryService directoryService;

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
            OnboardingCompletedEvent onboardingEvent =
                    objectMapper.readValue(record.value(), OnboardingCompletedEvent.class);
            directoryService.syncUserFromOnboarding(onboardingEvent);

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

    @KafkaListener(
            topics = TandemKafkaConfig.TOPIC_GROUP_CREATED,
            groupId = "interest-service-group-consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleGroupCreated(
            ConsumerRecord<String, String> record,
            Acknowledgment acknowledgment
    ) {
        log.info("EVENT: group.created");
        try {
            GroupCreatedEvent groupCreatedEvent =
                    objectMapper.readValue(record.value(), GroupCreatedEvent.class);
            directoryService.syncGroupFromEvent(groupCreatedEvent);

            List<GroupInterestRequest> requests = groupInterestService.parseToGroupInterestRequest(record.value());

            if (requests.isEmpty()) {
                log.info("No valid interests to add");
                acknowledgment.acknowledge();
                return;
            }

            groupInterestService.addGroupInterest(requests);

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process group.created event", e);
        }
    }
}

