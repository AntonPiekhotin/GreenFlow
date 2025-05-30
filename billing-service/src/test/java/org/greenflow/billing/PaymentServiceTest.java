package org.greenflow.billing;

import org.greenflow.billing.model.constant.PaymentStatus;
import org.greenflow.billing.model.entity.Payment;
import org.greenflow.billing.output.persistent.PaymentRepository;
import org.greenflow.billing.service.PayPalService;
import org.greenflow.billing.service.PaymentService;
import org.greenflow.billing.service.UserBalanceService;
import org.greenflow.common.model.dto.event.BalanceChangeMessage;
import org.greenflow.common.model.exception.GreenFlowException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PayPalService payPalService;

    @Mock
    private UserBalanceService userBalanceService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void topUpBalance_createsPaymentAndReturnsRedirectUrl() throws IOException {
        String userId = "user123";
        BigDecimal paymentAmount = BigDecimal.valueOf(10.0);
        Payment payment = Payment.builder()
                .userId(userId)
                .amount(paymentAmount)
                .currency("EUR")
                .status(PaymentStatus.PENDING)
                .build();
        String redirectUrl = "http://paypal.com/redirect";

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(payPalService.createPaymentUrl(any(Payment.class))).thenReturn(redirectUrl);

        String result = paymentService.topUpBalance(userId, paymentAmount);

        assertEquals(redirectUrl, result);
        verify(paymentRepository).save(any(Payment.class));
        verify(payPalService).createPaymentUrl(any(Payment.class));
    }

    @Test
    void topUpBalance_throwsExceptionWhenPayPalFails() throws IOException {
        String userId = "user123";
        BigDecimal paymentAmount = BigDecimal.valueOf(10.0);
        Payment payment = Payment.builder()
                .userId(userId)
                .amount(paymentAmount)
                .currency("EUR")
                .status(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(payPalService.createPaymentUrl(any(Payment.class))).thenThrow(new IOException("PayPal error"));

        GreenFlowException exception = assertThrows(GreenFlowException.class, () ->
            paymentService.topUpBalance(userId, paymentAmount)
        );

        assertEquals(503, exception.getStatusCode());
        verify(paymentRepository).save(any(Payment.class));
        verify(payPalService).createPaymentUrl(any(Payment.class));
    }

    @Test
    void handleSuccessPayment_updatesPaymentAndUserBalance() {
        String paymentId = "payment123";
        Payment payment = Payment.builder()
                .id(paymentId)
                .userId("user123")
                .amount(BigDecimal.valueOf(10.0))
                .status(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        Payment result = paymentService.handleSuccessPayment(paymentId);

        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
        verify(paymentRepository).save(payment);
        verify(userBalanceService).changeBalance(any(BalanceChangeMessage.class));
    }

    @Test
    void handleSuccessPayment_throwsExceptionWhenPaymentNotFound() {
        String paymentId = "invalidPaymentId";

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        GreenFlowException exception = assertThrows(GreenFlowException.class, () ->
            paymentService.handleSuccessPayment(paymentId)
        );

        assertEquals(400, exception.getStatusCode());
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(userBalanceService, never()).changeBalance(any(BalanceChangeMessage.class));
    }

    @Test
    void handleSuccessPayment_throwsExceptionWhenPaymentAlreadyCompleted() {
        String paymentId = "payment123";
        Payment payment = Payment.builder()
                .id(paymentId)
                .userId("user123")
                .amount(BigDecimal.valueOf(10.0))
                .status(PaymentStatus.COMPLETED)
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        GreenFlowException exception = assertThrows(GreenFlowException.class, () ->
            paymentService.handleSuccessPayment(paymentId)
        );

        assertEquals(400, exception.getStatusCode());
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(userBalanceService, never()).changeBalance(any(BalanceChangeMessage.class));
    }
}
