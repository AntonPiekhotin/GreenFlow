package org.greenflow.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.greenflow.apigateway.filter.AuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Config {

    private final AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/register")
                        .uri("lb://auth-service"))
                .route("auth-service", r -> r.path("/api/v1/auth/login")
                        .uri("lb://auth-service"))
                .route("client", r -> r.path("/api/v1/client/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://client"))
                .route("garden", r -> r.path("/api/v1/garden/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://garden"))
                .route("order", r -> r.path("/api/v1/order/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://order"))
                .route("open-order", r -> r.path("/api/v1/open-order/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://open-order"))
                .build();
    }
}