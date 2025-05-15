package com.example.apigateway.filter;

import com.example.apigateway.utils.JWTUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
@Order(-1)
public class JWTAuthenticationFilter implements GlobalFilter {
    private final JWTUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Autowired
    public JWTAuthenticationFilter(JWTUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        String token = request.getHeaders().getFirst("Authorization");
        logger.info("Headers: {}", request.getHeaders());
        logger.info("JWT Token: {}", token);

        logger.info("API Gateway Filter is starting... {}, {}", request.getMethod(), request.getURI());

        try {
            if (!isAuthMissing(token)){
                assert token != null;
                token = token.substring(7);
                logger.info("Token is: {}", token);

                if(jwtUtils.validateToken(token)) {
                    String roles = String.join(",", jwtUtils.getRoles(token));
                    String username = jwtUtils.extractUsername(token);
                    String userId = jwtUtils.extractUserId(token);
                    logger.info("Username is: {}", username);
                    logger.info("Roles is: {}", roles);
                    logger.info("UserId is: {}", userId);

                    ServerHttpRequest mutatedRequest = request.mutate()
                            .header("X-username", username)
                            .header("X-roles", roles)
                            .header("X-userId", userId)
                            .build();

                    logger.info("Pass token API Gateway!");

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(mutatedRequest)
                            .build();
                    return chain.filter(mutatedExchange);
                } else logger.warn("JWT Token is not valid!");
            } else logger.warn("JWT Token is missing!");

            String[] publicUrls = {"auth", "v3", "swagger"};

            if(Arrays.stream(publicUrls).anyMatch(path::contains)) {
                logger.info("Pass filter API Gateway!");
                return chain.filter(exchange);
            }

            logger.error("Can not authorize!");
            return onError(exchange, "Can not authorize!");
        }catch (Exception e) {
            logger.error("Error Filter API Gateway!");
            return onError(exchange, e.getMessage());
        }

    }

    public Mono<Void> onError(ServerWebExchange exchange, String message) {
        logger.warn("Error in API GATEWAY Filter: {}", message);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    public boolean isAuthMissing(String token) {
        return token == null || !token.contains("Bearer");
    }
}