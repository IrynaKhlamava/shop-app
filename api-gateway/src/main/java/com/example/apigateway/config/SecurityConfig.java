package com.example.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import reactor.core.publisher.Mono;


@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final Converter<Jwt, Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter;

    private final ReactiveJwtDecoder jwtDecoder;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/auth/**").permitAll()

                        .pathMatchers(org.springframework.http.HttpMethod.GET, "/api/products/**").permitAll()
                        .pathMatchers(org.springframework.http.HttpMethod.POST, "/api/products/batch").authenticated()
                        .pathMatchers(org.springframework.http.HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .pathMatchers(org.springframework.http.HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .pathMatchers(org.springframework.http.HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        .pathMatchers(org.springframework.http.HttpMethod.POST, "/api/orders/*/place").authenticated()
                        .pathMatchers(org.springframework.http.HttpMethod.GET, "/api/users/*/profile").authenticated()
                        .pathMatchers(org.springframework.http.HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .pathMatchers(org.springframework.http.HttpMethod.GET, "/api/orders/*/history").authenticated()

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                                .jwtDecoder(jwtDecoder)
                        )
                )
                .build();
    }

}