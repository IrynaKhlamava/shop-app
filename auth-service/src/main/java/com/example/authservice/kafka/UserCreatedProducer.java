package com.example.authservice.kafka;

import com.example.authservice.event.UserCreatedFromAuthEvent;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreatedProducer {

    private final KafkaTemplate<String, UserCreatedFromAuthEvent> kafkaTemplate;

    @Value("${kafka.topics.user-created}")
    private String topic;

    private static final Logger logger = LoggerFactory.getLogger(UserCreatedProducer.class);

    public void publishUserCreatedEvent(String userId, String firstName, String lastName) {
        UserCreatedFromAuthEvent event = new UserCreatedFromAuthEvent(userId, firstName, lastName);
        kafkaTemplate.send(topic, event);
        logger.info("Kafka event sent for user {} to topic '{}'", userId, topic);

        kafkaTemplate.send(topic, event).whenComplete((result, ex) -> {
            if (ex != null) {
                logger.error("Failed to send Kafka event for user {}: {}", userId, ex.getMessage(), ex);
            } else {
                logger.info("Kafka event sent for user {} to topic '{}', partition {}",
                        userId,
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition());
            }
        });
    }
}
