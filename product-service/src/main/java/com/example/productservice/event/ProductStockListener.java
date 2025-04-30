package com.example.productservice.event;

import com.example.productservice.dto.CartItemDto;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductStockListener {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductStockListener.class);

    @KafkaListener(
            topics = "#{@kafkaConfigProperties.topics.orderStockUpdate}",
            groupId = "#{@kafkaConfigProperties.consumer.groupId}",
            containerFactory = "listKafkaListenerContainerFactory"
    )
    public void handleStockUpdate(List<CartItemDto> items) {
        logger.info("Received stock update for order: {}", items);
        productService.updateStockAfterOrder(items);
    }

    @KafkaListener(
            topics = "#{@kafkaConfigProperties.topics.orderStockRestore}",
            groupId = "#{@kafkaConfigProperties.consumer.groupId}"
    )
    public void handleStockRestore(CartItemDto item) {
        logger.info("Received stock restore for product {} with quantity {}", item.getProductId(), item.getQuantity());
        productService.increaseStock(item.getProductId(), item.getQuantity());
    }

}


