package org.greenflow.openorder.input.web;

import lombok.RequiredArgsConstructor;
import org.greenflow.openorder.service.OpenOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/open-order")
@RequiredArgsConstructor
public class OpenOrderController {

    private final OpenOrderService openOrderService;

    @GetMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public ResponseEntity<?> getOpenOrders() {
        return ResponseEntity.ok(openOrderService.getOpenOrders());
    }

}
