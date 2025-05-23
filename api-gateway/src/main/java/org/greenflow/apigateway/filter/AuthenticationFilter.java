package org.greenflow.apigateway.filter;

import lombok.RequiredArgsConstructor;
import org.greenflow.apigateway.config.JwtUtil;
import org.greenflow.common.model.constant.CustomHeaders;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RefreshScope
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;
    private static final List<String> CLOSED_ROUTES = List.of(
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (isClosedPath(request)) {
            return onError(exchange, HttpStatus.FORBIDDEN);
        }

        if (isAuthMissing(request)) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        final String token = getAuthHeader(request).substring(7);
        if (jwtUtil.isInvalid(token)) {
            return onError(exchange, HttpStatus.FORBIDDEN);
        }

        ServerWebExchange updatedExchange = updateRequest(exchange, token);

        return chain.filter(updatedExchange);
    }

    /**
     * Update the request with the user information. Add info to the request headers.
     */
    private ServerWebExchange updateRequest(ServerWebExchange exchange, String token) {
        String userId = jwtUtil.extractUserId(token);
        String roles = jwtUtil.extractRoles(token);
        String email = jwtUtil.extractEmail(token);

        ServerHttpRequest updatedRequest = exchange.getRequest().mutate()
                .header(CustomHeaders.X_USER_ID, userId)
                .header(CustomHeaders.X_ROLES, roles)
                .header(CustomHeaders.X_EMAIL, email)
                .build();

        return exchange.mutate().request(updatedRequest).build();
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").getFirst();
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private boolean isClosedPath(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return CLOSED_ROUTES.stream().anyMatch(path::contains);
    }
}