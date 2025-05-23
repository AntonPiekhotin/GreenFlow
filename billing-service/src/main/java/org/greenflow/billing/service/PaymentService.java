package org.greenflow.billing.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.billing.model.constant.PaymentStatus;
import org.greenflow.billing.model.entity.Payment;
import org.greenflow.billing.output.persistent.PaymentRepository;
import org.greenflow.common.model.dto.event.BalanceChangeMessage;
import org.greenflow.common.model.exception.GreenFlowException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PayPalService payPalService;
    private final UserBalanceService userBalanceService;
//
//    public void createPayment(PaymentCreationMessage message) {
//        log.debug("Creating payment for user: {}, amount: {}, currency: {}, description: {}",
//                message.userId(), message.amount(), message.currency(), message.description());
//        Payment payment = Payment.builder()
//                .userId(message.userId())
//                .amount(message.amount())
//                .currency(message.currency())
//                .description(message.description())
//                .status(PaymentStatus.PENDING)
//                .build();
//        paymentRepository.save(payment);
//        log.info("Payment created: {}", payment.getId());
//    }

    //    public List<Payment> getMyPayments(@NotBlank String userId) {
//        return paymentRepository.findAllByUserId(userId);
//    }
//
    public String topUpBalance(String userId, @NotNull @DecimalMin("1.0") BigDecimal paymentAmount) {
        log.info("Creating payment for user: {}, amount: {}", userId, paymentAmount);
        Payment payment = Payment.builder()
                .userId(userId)
                .amount(paymentAmount)
                .currency("EUR")
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
        log.info("Payment created: {}", payment.getId());
        String redirectUrl = null;
        try {
            redirectUrl = payPalService.createPaymentUrl(payment);
        } catch (IOException e) {
            throw new GreenFlowException(503, "Error occurred calling PayPalApi: ", e);
        }
        log.info("Redirect URL: {}", redirectUrl);
        return redirectUrl;
    }

    public Payment handleSuccessPayment(@NotBlank String paymentId) {
        log.info("Got payment confirmation request {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new GreenFlowException(400, "Payment not found"));
        if (payment.getStatus().equals(PaymentStatus.COMPLETED)) {
            throw new GreenFlowException(400, "Payment is already completed");
        }
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);
        log.info("Payment completed: {}", paymentId);
        userBalanceService.changeBalance(BalanceChangeMessage.builder()
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .build()
        );
        return payment;
    }
}
