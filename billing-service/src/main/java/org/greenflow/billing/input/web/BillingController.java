package org.greenflow.billing.input.web;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.greenflow.billing.service.PaymentService;
import org.greenflow.billing.service.UserBalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final PaymentService paymentService;
    private final UserBalanceService balanceService;

    @GetMapping("/balance")
    public ResponseEntity<?> balanceByUserId(@RequestParam("userId") @NotBlank String userId) {
        return ResponseEntity.ok(balanceService.getUserBalance(userId));
    }
    
    @PostMapping("/register")
    public Boolean registerUser(@RequestParam("userId") @NotBlank String userId) {
        return balanceService.registerUser(userId);
    }

//    @PostMapping("/{paymentId}")
//    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_WORKER')")
//    public StripeRedirectUrl pay(@RequestHeader(CustomHeaders.X_USER_ID) String userId,
//                                 @PathVariable @NotBlank String paymentId) {
//        return new StripeRedirectUrl(paymentService.pay(userId, paymentId));
//    }
//
//    @GetMapping("/success")
//    public ResponseEntity<?> handleSuccessPayment(@RequestParam("paymentId") String paymentId) {
//        return ResponseEntity.ok(paymentService.handleSuccessPayment(paymentId));
//    }
}
