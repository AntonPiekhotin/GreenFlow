package org.greenflow.openorder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenOrderService {

//    redis
//    private final OpenOrderRepository openOrderRepository;

    public List<?> getOpenOrders() {
        return List.of();
    }
}
