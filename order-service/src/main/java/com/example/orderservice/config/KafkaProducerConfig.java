package com.example.orderservice.config;

import com.example.orderservice.dto.CartItemDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaConfigProperties.class)
public class KafkaProducerConfig {

    private final KafkaConfigProperties kafkaProps;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, List<CartItemDto>> stockUpdateProducerFactory(
            Map<String, Object> producerConfigs) {
        return new DefaultKafkaProducerFactory<>(producerConfigs);
    }

    @Bean
    public ProducerFactory<String, CartItemDto> stockRestoreProducerFactory(
            Map<String, Object> producerConfigs) {
        return new DefaultKafkaProducerFactory<>(producerConfigs);
    }

    @Bean
    public KafkaTemplate<String, List<CartItemDto>> stockUpdateKafkaTemplate(
            ProducerFactory<String, List<CartItemDto>> stockUpdateProducerFactory) {
        return new KafkaTemplate<>(stockUpdateProducerFactory);
    }

    @Bean
    public KafkaTemplate<String, CartItemDto> stockRestoreKafkaTemplate(
            ProducerFactory<String, CartItemDto> stockRestoreProducerFactory) {
        return new KafkaTemplate<>(stockRestoreProducerFactory);
    }

    @PostConstruct
    public void logKafkaBootstrapServers() {
        System.out.println("Kafka bootstrap servers (from config): " + kafkaProps.getBootstrapServers());
    }
}

