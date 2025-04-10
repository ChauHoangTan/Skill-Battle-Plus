package com.example.apigateway.Config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
            .route("user-service", r -> r
                    .path("/users/**")
                    .uri("http://localhost:8081"))
            .route("quiz-service", r->r
                    .path("/quizzes/**")
                    .uri("http://localhost:8082"))
            .route("question-service", r->r
                    .path("/questions/**")
                    .uri("http://localhost:8083"))
            .route("notification-service", r->r
                    .path("/notifications/**")
                    .uri("http://localhost:8084"))
            .route("exam-service", r->r
                    .path("/exams/**")
                    .uri("http://localhost:8085"))
            .route("analytic-service", r->r
                    .path("/analytics/**")
                    .uri("http://localhost:8086"))
            .build();
    }
}
