package org.greenflow.openorder.input.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.greenflow.openorder.model.dto.OpenOrdersRequestDto;
import org.greenflow.openorder.service.OpenOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/open-order")
@RequiredArgsConstructor
public class OpenOrderController {

    private final OpenOrderService openOrderService;

    @GetMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public ResponseEntity<?> getOpenOrders(@RequestBody @Valid OpenOrdersRequestDto request) {
        var openOrders = openOrderService.getOpenOrdersWithinRadius(request);
        if (openOrders.isEmpty()) {
            return ResponseEntity.status(204).body(List.of());
        }
        return ResponseEntity.ok(openOrders);
    }

}
