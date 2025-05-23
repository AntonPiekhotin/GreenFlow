package org.greenflow.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import static org.greenflow.common.model.constant.CustomHeaders.X_INTERNAL_TOKEN;

@Configuration
public class Config {

    @Value("${api.internalApiToken}")
    private String internalToken;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .additionalInterceptors((request, body, execution) -> {
                    request.getHeaders().add(X_INTERNAL_TOKEN, internalToken);
                    return execution.execute(request, body);
                })
                .build();
    }

}
