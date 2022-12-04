package ru.ssemenov.configs;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {
    @Autowired
    private JwtUtil jwtUtil;

    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            UUID trace = UUID.randomUUID();
            ServerHttpRequest request = exchange.getRequest();
            log.info("Start JwtAuthFilter, request={}, trace={}", request, trace);
            if (request.getHeaders().containsKey("username")) {
                log.error("Invalid header username={}, trace={}", request.getHeaders().containsKey("username"), trace);
                return this.onError(exchange, "Invalid header username", HttpStatus.BAD_REQUEST);
            }
            if (request.getHeaders().containsKey("vatCode")) {
                log.error("Invalid header vatCode={}, trace={}", request.getHeaders().containsKey("vatCode"), trace);
                return this.onError(exchange, "Invalid header vatCode", HttpStatus.BAD_REQUEST);
            }

            if (!isAuthMissing(request)) {
                log.info("Authorization header is provided trace={}", trace);
                final String token = getAuthHeader(request);
                if (jwtUtil.isInvalid(token)) {
                    log.error("Authorization header is invalid={}, trace={}", token, trace);
                    return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);
                }
                populateRequestWithHeaders(exchange, token);
            } else {
                log.info("Authorization header is not provided, go to NotForGuestsFilter, trace={}", trace);
            }
            exchange.getAttributes().put("trace", trace);
            return chain.filter(exchange);
        };
    }

    public static class Config {
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0).substring(7);
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        if (!request.getHeaders().containsKey("Authorization")) {
            return true;
        }
        if (!request.getHeaders().getOrEmpty("Authorization").get(0).startsWith("Bearer ")) {
            return true;
        }
        return false;
    }

    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
        Claims claims = jwtUtil.getAllClaimsFromToken(token);
        exchange.getRequest().mutate()
                .header("username", claims.getSubject())
                .header("roles", String.valueOf(claims.get("roles")))
                .header("vatCode", String.valueOf(claims.get("vatCode")))
                .build();
    }
}
