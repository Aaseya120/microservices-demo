package com.demo.gateway.filter;

import com.demo.gateway.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.springframework.http.server.reactive.ServerHttpResponse;

/**
 * Validates the Authorization: Bearer <jwt> header on routed requests and,
 * once valid, enriches the downstream request with X-User-Id / X-User-Role
 * headers extracted from the token claims.
 *
 * Bean name convention: "JwtAuthFilterGatewayFilterFactory" -> referenced in
 * application.yml route filters as "name: JwtAuthFilter".
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilterGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final JwtUtil jwtUtil;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange.getResponse(), exchange.getRequest().getPath().toString());
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.isValid(token)) {
                return onError(exchange.getResponse(), exchange.getRequest().getPath().toString());
            }

            ServerHttpRequest enriched = exchange.getRequest().mutate()
                    .header("X-User-Id", jwtUtil.getUserId(token))
                    .header("X-User-Role", jwtUtil.getRole(token))
                    .build();

            return chain.filter(exchange.mutate().request(enriched).build());
        };
    }

    private Mono<Void> onError(ServerHttpResponse response, String path) {
        log.warn("Rejecting request {} - missing/invalid JWT", path);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}
