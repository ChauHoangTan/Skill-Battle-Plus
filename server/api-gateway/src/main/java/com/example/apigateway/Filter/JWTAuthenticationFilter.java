package com.example.apigateway.Filter;

import com.example.apigateway.Utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.annotation.NonNull;

import java.io.IOException;
import java.net.http.HttpRequest;

@Component
@Order(-1)
public class JWTAuthenticationFilter implements GlobalFilter {
    private final JWTUtils jwtUtils;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Autowired
    public JWTAuthenticationFilter(JWTUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst("Authorization");

        logger.info("API Gateway Filter is starting...", request.getMethod(), request.getURI());

        try {
            if (isAuthMissing(token)){
                return onError(exchange, "Is missing Token!");
            }

            assert token != null;
            token = token.substring(7);
            logger.debug("Token is: ", token);

            if(jwtUtils.validateToken(token)) {
                String roles = String.join(",", jwtUtils.getRoles(token));
                String username = jwtUtils.extractUsername(token);
                logger.debug("Username is: ", username);
                logger.debug("Roles is: ", roles);

                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-username", username)
                        .header("X-roles", roles)
                        .build();

                return chain.filter(
                        exchange
                                .mutate()
                                .request(mutatedRequest)
                                .build());
            }

            return onError(exchange, "Can not authorize!");
        }catch (Exception e) {
            return onError(exchange, e.getMessage());
        }

    }

    public Mono<Void> onError(ServerWebExchange exchange, String message) {
        logger.warn("Error in API GATEWAY Filter: ", message);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    public boolean isAuthMissing(String token) {
        return token == null || !token.contains("Bearer");
    }
}