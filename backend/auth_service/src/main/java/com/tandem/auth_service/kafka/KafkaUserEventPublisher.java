package com.tandem.auth_service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

import com.tandem.auth_service.kafka.events.UserLoggedInEvent;
import com.tandem.auth_service.kafka.events.UserRegisteredEvent;

@Component
@RequiredArgsConstructor
public class KafkaUserEventPublisher implements UserEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String USER_REGISTERED_TOPIC = "user-registered";
    private static final String USER_LOGIN_TOPIC = "user-logged-in";

    @SuppressWarnings("null")
    @Override
    public void publishUserRegistered(UserRegisteredEvent event) {

        String key = Objects.requireNonNull(event.userId()).toString();

        kafkaTemplate.send(
                USER_REGISTERED_TOPIC,
                key,
                event
        );
    }

    @SuppressWarnings("null")
    @Override
    public void publishUserLogin(UserLoggedInEvent event) {

        String key = Objects.requireNonNull(event.userId()).toString();

        kafkaTemplate.send(
                USER_LOGIN_TOPIC,
                key,
                event
        );
    }
}