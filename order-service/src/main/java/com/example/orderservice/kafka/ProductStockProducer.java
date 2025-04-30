package com.example.orderservice.kafka;

import com.example.orderservice.config.KafkaConfigProperties;
import com.example.orderservice.dto.CartItemDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductStockProducer {

    private final KafkaTemplate<String, List<CartItemDto>> stockUpdateKafkaTemplate;
    private final KafkaTemplate<String, CartItemDto> stockRestoreKafkaTemplate;
    private final KafkaConfigProperties kafkaConfig;

    private static final Logger logger = LoggerFactory.getLogger(ProductStockProducer.class);

    public void sendStockUpdate(List<CartItemDto> items) {
        String topic = kafkaConfig.getTopics().getOrderStockUpdate();
        stockUpdateKafkaTemplate.send(topic, items);
        logger.info("Sent stock update to topic '{}': {}", topic, items);
    }

    public void sendStockRestore(CartItemDto item) {
        String topic = kafkaConfig.getTopics().getOrderStockRestore();
        stockRestoreKafkaTemplate.send(topic, item);
        logger.info("Sent stock restore to topic '{}': {}", topic, item);
    }
}