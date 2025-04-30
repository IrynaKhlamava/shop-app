package com.example.orderservice.client;

import com.example.orderservice.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceClient {

    @Value("${product.service-url}")
    private String productServiceUrl;

    private final WebClient webClient;

    public ProductDto getProductById(String productId) {
        return webClient.get()
                .uri(productServiceUrl + "/api/products/{id}", productId)
                .retrieve()
                .bodyToMono(ProductDto.class)
                .block();
    }

    public List<ProductDto> getProductsByIds(List<String> ids) {
        return webClient.post()
                .uri(productServiceUrl + "/api/products/batch")
                .bodyValue(ids)
                .retrieve()
                .bodyToFlux(ProductDto.class)
                .collectList()
                .block();
    }
}
