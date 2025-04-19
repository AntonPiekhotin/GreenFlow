package org.greenflow.openorder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.dto.event.OrderOpeningMessageDto;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenOrderService {

    private static final String GEO_KEY = "orders_geo";
    private static final String HASH_KEY_PREFIX = "order:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveOpenOrder(OrderOpeningMessageDto order) {
        redisTemplate.opsForGeo()
                .add(GEO_KEY, new Point(order.getLongitude(), order.getLatitude()), order.getOrderId());

        String orderJson;
        try {
            orderJson = objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.opsForValue().set(HASH_KEY_PREFIX + order.getOrderId(), orderJson);
    }

}
