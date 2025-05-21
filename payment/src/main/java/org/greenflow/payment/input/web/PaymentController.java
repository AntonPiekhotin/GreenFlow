package org.greenflow.payment.input.web;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.greenflow.common.model.constant.CustomHeaders;
import org.greenflow.payment.model.dto.StripeRedirectUrl;
import org.greenflow.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_WORKER')")
    public ResponseEntity<?> my(@RequestHeader(CustomHeaders.X_USER_ID) String userId) {
        var payments = paymentService.getMyPayments(userId);
        if (payments.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_WORKER')")
    public StripeRedirectUrl pay(@RequestHeader(CustomHeaders.X_USER_ID) String userId,
                                 @PathVariable @NotBlank String paymentId) {
        return new StripeRedirectUrl(paymentService.pay(userId, paymentId));
    }

    @GetMapping("/success")
    public ResponseEntity<?> handleSuccessPayment(@RequestParam("paymentId") String paymentId) {
        return ResponseEntity.ok(paymentService.handleSuccessPayment(paymentId));
    }
}
