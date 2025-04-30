package com.example.orderservice.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaConfigProperties.class)
public class KafkaTopicConfig {

    private final KafkaConfigProperties kafka;

    private static final Logger logger = LoggerFactory.getLogger(KafkaTopicConfig.class);

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic orderStockUpdateTopic() {
        logger.info("Creating Kafka topic '{}'", kafka.getTopics().getOrderStockUpdate());
        return TopicBuilder.name(kafka.getTopics().getOrderStockUpdate())
                .partitions(1)
                .replicas((short) 1)
                .build();
    }

    @Bean
    public NewTopic orderStockRestoreTopic() {
        logger.info("Creating Kafka topic '{}'", kafka.getTopics().getOrderStockRestore());
        return TopicBuilder.name(kafka.getTopics().getOrderStockRestore())
                .partitions(1)
                .replicas((short) 1)
                .build();
    }
}