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
    private static String AUTH_SERVICE_HOST;
    @Value("${service.auth.port}")
    private static String AUTH_SERVICE_PORT;

    @Value("${service.client.host}")
    private static String CLIENT_SERVICE_HOST;
    @Value("${service.client.port}")
    private static String CLIENT_SERVICE_PORT;

    @Value("${service.worker.host}")
    private static String WORKER_SERVICE_HOST;
    @Value("${service.worker.port}")
    private static String WORKER_SERVICE_PORT;

    @Value("${service.garden.host}")
    private static String GARDEN_SERVICE_HOST;
    @Value("${service.garden.port}")
    private static String GARDEN_SERVICE_PORT;

    @Value("${service.order.host}")
    private static String ORDER_SERVICE_HOST;
    @Value("${service.order.port}")
    private static String ORDER_SERVICE_PORT;

    @Value("${service.open-order.host}")
    private static String OPEN_ORDER_SERVICE_HOST;
    @Value("${service.open-order.port}")
    private static String OPEN_ORDER_SERVICE_PORT;


    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/register")
                        .uri("http://" + AUTH_SERVICE_HOST + ":" + AUTH_SERVICE_PORT))
                .route("auth-service", r -> r.path("/api/v1/auth/login")
                        .uri("http://" + AUTH_SERVICE_HOST + ":" + AUTH_SERVICE_PORT))
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