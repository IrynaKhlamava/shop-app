package com.example.orderservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfigProperties {
    private String bootstrapServers;
    private Consumer consumer;
    private Topics topics;

    @Getter
    @Setter
    public static class Consumer {
        private String groupId;
    }

    @Getter
    @Setter
    public static class Topics {
        private String orderStockUpdate;
        private String orderStockRestore;
    }
}
