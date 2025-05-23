package org.greenflow.billing.input.web;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.greenflow.billing.model.dto.RedirectUrl;
import org.greenflow.billing.service.PaymentService;
import org.greenflow.billing.service.UserBalanceService;
import org.greenflow.common.model.constant.CustomHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final PaymentService paymentService;
    private final UserBalanceService balanceService;

    @Value("${frontend.host}")
    private String frontendHost;
    
    private String paymentRedirectUrl;

    @PostConstruct
    public void init() {
        paymentRedirectUrl = frontendHost + "/payment-success";
    }

    @GetMapping("/balance")
    public ResponseEntity<?> balanceByUserId(@RequestParam("userId") @NotBlank String userId) {
        return ResponseEntity.ok(balanceService.getUserBalance(userId));
    }

    @GetMapping("/register")
    public Boolean registerUser(@RequestParam("userId") @NotBlank String userId) {
        return balanceService.registerUser(userId);
    }

    @PostMapping("/topup")
    public String topUpBalance(@RequestParam("userId") String userId,
                           @RequestParam("amount") @NotNull @DecimalMin("1.0") BigDecimal paymentAmount) {
        return paymentService.topUpBalance(userId, paymentAmount);
    }

    @GetMapping("/success")
    public ResponseEntity<?> handleSuccessPayment(@RequestParam("paymentId") String paymentId) {
        return ResponseEntity.ok(paymentService.handleSuccessPayment(paymentId));
    }
}
