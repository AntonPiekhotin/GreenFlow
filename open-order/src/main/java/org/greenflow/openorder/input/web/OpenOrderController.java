package org.greenflow.openorder.input.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.greenflow.common.model.constant.CustomHeaders;
import org.greenflow.openorder.model.dto.OpenOrdersRequestDto;
import org.greenflow.openorder.service.OpenOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
            return ResponseEntity.status(404).body(List.of());
        }
        return ResponseEntity.ok(openOrders);
    }

    @PostMapping("/{orderId}")
    @PreAuthorize("hasAuthority('WORKER')")
    public ResponseEntity<?> assignOrder(@RequestHeader(CustomHeaders.X_USER_ID) String workerId,
                                         @PathVariable String orderId) {
        openOrderService.assignOrderToWorker(orderId, workerId);
        return ResponseEntity.ok("Order assigned successfully");
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllOpenOrders() {
        var openOrders = openOrderService.getAllOpenOrders();
        if (openOrders.isEmpty()) {
            return ResponseEntity.status(404).body(List.of());
        }
        return ResponseEntity.ok(openOrders);
    }

}
