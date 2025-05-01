package com.example.apigateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static com.example.apigateway.util.AppConstants.BEARER_PREFIX;
import static com.example.apigateway.util.AppConstants.TOKEN_QUERY_PARAM;

@Component
@RequiredArgsConstructor
public class BlacklistFilter implements GlobalFilter {

    private final WebClient.Builder webClientBuilder;

    @Value("${auth-service.url}")
    private String authServiceUrl;

    @Value("${auth-service.blacklist-check-path}")
    private String blacklistCheckPath;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        String uri = UriComponentsBuilder.fromHttpUrl(authServiceUrl)
                .path(blacklistCheckPath)
                .queryParam(TOKEN_QUERY_PARAM, token)
                .toUriString();

        return webClientBuilder.build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Boolean.class)
                .flatMap(blacklisted -> {
                    if (Boolean.TRUE.equals(blacklisted)) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                    return chain.filter(exchange);
                });
    }

}
