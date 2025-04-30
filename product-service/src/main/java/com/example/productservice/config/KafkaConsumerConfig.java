package com.example.productservice.config;

import com.example.productservice.dto.CartItemDto;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.*;

@RequiredArgsConstructor
@Configuration
public class KafkaConsumerConfig {

    private final KafkaConfigProperties kafkaProps;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProps.getConsumer().getGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ConsumerFactory<String, CartItemDto> consumerFactory( Map<String, Object> consumerConfigs) {
        JsonDeserializer<CartItemDto> deserializer =
                new JsonDeserializer<>(CartItemDto.class, false);
        deserializer.setRemoveTypeHeaders(true);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs,
                new StringDeserializer(),
                deserializer
        );
    }


    @Bean(name = "listKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, List<CartItemDto>> listKafkaListenerContainerFactory(
            ConsumerFactory<String, List<CartItemDto>> listConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, List<CartItemDto>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(listConsumerFactory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CartItemDto> kafkaListenerContainerFactory(
            ConsumerFactory<String, CartItemDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, CartItemDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, List<CartItemDto>> listConsumerFactory(Map<String, Object> consumerConfigs) {
        JsonDeserializer<List<CartItemDto>> deserializer = new JsonDeserializer<>(new TypeReference<List<CartItemDto>>() {}, false);
        deserializer.setRemoveTypeHeaders(true);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs,
                new StringDeserializer(),
                deserializer
        );
    }
}

