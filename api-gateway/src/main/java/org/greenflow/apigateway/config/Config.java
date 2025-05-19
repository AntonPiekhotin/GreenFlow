package org.greenflow.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.greenflow.apigateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Config {

    private final AuthenticationFilter filter;

    @Value("${service.auth.host}")
    private String AUTH_SERVICE_HOST;

    @Value("${service.client.host}")
    private String CLIENT_SERVICE_HOST;

    @Value("${service.worker.host}")
    private String WORKER_SERVICE_HOST;

    @Value("${service.garden.host}")
    private String GARDEN_SERVICE_HOST;

    @Value("${service.order.host}")
    private String ORDER_SERVICE_HOST;

    @Value("${service.open-order.host}")
    private String OPEN_ORDER_SERVICE_HOST;

    @Value("${service.equipment.host}")
    private String EQUIPMENT_HOST;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        .uri("http://" + AUTH_SERVICE_HOST))
                .route("client", r -> r.path("/api/v1/client/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://" + CLIENT_SERVICE_HOST))
                .route("worker", r -> r.path("/api/v1/worker/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://" + WORKER_SERVICE_HOST))
                .route("garden", r -> r.path("/api/v1/garden/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://" + GARDEN_SERVICE_HOST))
                .route("order", r -> r.path("/api/v1/order/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://" + ORDER_SERVICE_HOST))
                .route("open-order", r -> r.path("/api/v1/open-order/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://" + OPEN_ORDER_SERVICE_HOST))
                .route("equipment", r -> r.path("/api/v1/equipment/**", "/api/v1/warehouse/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://" + EQUIPMENT_HOST))
                .build();
    }
}