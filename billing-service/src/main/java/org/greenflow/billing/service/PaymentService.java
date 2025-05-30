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

/**
 * Service responsible for handling payment operations in the GreenFlow billing system.
 * This service manages payment creation, processing, and balance updates through PayPal integration.
 * It interacts with payment repositories and user balance services to maintain payment state
 * and update user balances upon successful payments.
 */
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

    /**
     * Creates a payment for topping up a user's balance and generates a PayPal payment URL.
     *
     * @param userId The unique identifier of the user making the payment
     * @param paymentAmount The amount to be paid, must be at least 1.0
     * @return A URL to redirect the user to complete payment via PayPal
     * @throws GreenFlowException with code 503 if there's an error communicating with PayPal API
     */
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

    /**
     * Processes a successful payment confirmation and updates the user's balance.
     * This method is called when a payment is confirmed as successful from PayPal.
     *
     * @param paymentId The unique identifier of the payment to be processed
     * @return The updated Payment entity with COMPLETED status
     * @throws GreenFlowException with code 400 if payment is not found or already completed
     */
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
