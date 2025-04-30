package com.example.userservice.event;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserEventListener.class);

    @KafkaListener(
            topics = "#{@kafkaConfigProperties.topics.userCreated}",
            groupId = "#{@kafkaConfigProperties.consumer.groupId}"
    )
    public void handleUserCreated(UserCreatedFromAuthEvent event) {
        logger.info("Received user created event: {}", event.getUserId());
        userService.createUserFromEvent(event);
    }

}