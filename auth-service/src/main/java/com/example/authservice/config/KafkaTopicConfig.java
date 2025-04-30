package com.example.authservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topics.user-created}")
    private String userCreatedTopic;

    @Value("${kafka.topics.partitions:1}")
    private int partitions;

    @Value("${kafka.topics.replicas:1}")
    private short replicas;

    private static final Logger logger = LoggerFactory.getLogger(KafkaTopicConfig.class);

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic userCreatedTopic() {
        logger.info("Creating Kafka topic '{}'", userCreatedTopic);
        return TopicBuilder.name(userCreatedTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}