package com.example.apigateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserIdHeaderFilter implements WebFilter {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt jwt) {
                        String userId = jwt.getClaimAsString("sub");
                        List<String> authorities = jwt.getClaimAsStringList("authorities");
                        String role = (authorities != null && !authorities.isEmpty()) ? authorities.get(0) : "ROLE_USER";


                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header(USER_ID_HEADER, userId)
                                .header(USER_ROLE_HEADER, role)
                                .build();

                        ServerWebExchange mutatedExchange = exchange.mutate()
                                .request(mutatedRequest)
                                .build();

                        return chain.filter(mutatedExchange);
                    }

                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}
