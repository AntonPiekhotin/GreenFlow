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

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        .uri("http://" + AUTH_SERVICE_HOST))
                .route("client", r -> r.path("/api/v1/client/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://client"))
                .route("worker", r -> r.path("/api/v1/worker/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://worker"))
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