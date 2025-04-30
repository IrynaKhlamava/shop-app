package com.example.userservice.client;

import com.example.userservice.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceClient {

    @Value("${order.service.url}")
    private String orderServiceUrl;

    @Value("${order.service.user-orders-path}")
    private String userOrdersPath;

    private final WebClient webClient;

    public List<OrderDto> getOrdersForUser(String userId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(orderServiceUrl)
                .path(userOrdersPath)
                .buildAndExpand(userId)
                .toUriString();

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(OrderDto.class)
                .collectList()
                .block();
    }
}
