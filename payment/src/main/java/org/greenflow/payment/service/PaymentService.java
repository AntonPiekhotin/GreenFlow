package org.greenflow.payment.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.dto.event.PaymentCreationMessage;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.payment.model.constant.PaymentStatus;
import org.greenflow.payment.model.entity.Payment;
import org.greenflow.payment.output.persistent.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void createPayment(PaymentCreationMessage message) {
        log.info("Creating payment for user: {}, amount: {}, currency: {}, description: {}",
                message.userId(), message.amount(), message.currency(), message.description());
        Payment payment = Payment.builder()
                .userId(message.userId())
                .amount(message.amount())
                .currency(message.currency())
                .description(message.description())
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
        log.info("Payment created: {}", payment.getId());
    }

    public List<Payment> getMyPayments(@NotBlank String userId) {
        return paymentRepository.findAllByUserId(userId);
    }


    public String pay(String userId, @NotBlank String paymentId) {
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        if(payment.getUserId() != null && !payment.getUserId().equals(userId)) {
            throw new GreenFlowException(403, "You are not allowed to pay this payment");
        }
        if (!payment.getStatus().equals(PaymentStatus.PENDING)) {
            throw new GreenFlowException(400, "Payment is not pending");
        }
        //create stripe url


        //return stripe url
        return "";
    }

    public void confirm() {
        //confirm payment
        //update payment status
        //send notification
    }
}
