package org.greenflow.order.input.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.greenflow.common.model.constant.CustomHeaders;
import org.greenflow.order.model.dto.OrderCreationDto;
import org.greenflow.order.model.dto.OrderUpdateDto;
import org.greenflow.order.model.entity.Order;
import org.greenflow.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> createOrder(@RequestHeader(CustomHeaders.X_USER_ID) String clientId,
                                         @RequestHeader(CustomHeaders.X_EMAIL) String clientEmail,
                                         @RequestBody @NotNull @Valid OrderCreationDto orderDto) {
        return ResponseEntity.status(201).body(orderService.createOrder(clientId, clientEmail, orderDto));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> getMyOrders(@RequestHeader(CustomHeaders.X_USER_ID) String clientId) {
        List<Order> orders = orderService.getOrdersByOwnerId(clientId);
        if (orders.isEmpty()) {
            return ResponseEntity.ok().body(Collections.emptyList());
        }
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> deleteOrder(@RequestHeader(CustomHeaders.X_USER_ID) String clientId,
                                         @PathVariable String orderId) {
        orderService.deleteOrder(clientId, orderId);
        return ResponseEntity.status(204).body("Order deleted successfully");
    }

    @PutMapping("/{orderId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> updateOrder(@RequestHeader(CustomHeaders.X_USER_ID) String clientId,
                                         @PathVariable String orderId,
                                         @RequestBody @Valid OrderUpdateDto orderDto) {
        Order order = orderService.updateOrder(clientId, orderId, orderDto);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/complete/{orderId}")
    @PreAuthorize("hasAuthority('WORKER')")
    public ResponseEntity<?> completeOrder(@RequestHeader(CustomHeaders.X_USER_ID) String workerId,
                                           @PathVariable String orderId) {
        return ResponseEntity.ok(orderService.completeOrder(workerId, orderId));
    }
}
