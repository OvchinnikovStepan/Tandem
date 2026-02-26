package com.tandem.auth_service.kafka;

import com.tandem.auth_service.kafka.events.UserLoggedInEvent;
import com.tandem.auth_service.kafka.events.UserRegisteredEvent;

public interface UserEventPublisher {

    void publishUserRegistered(UserRegisteredEvent event);

    void publishUserLogin(UserLoggedInEvent event);
}
