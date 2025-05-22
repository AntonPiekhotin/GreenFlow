package org.greenflow.billing.service;

import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.billing.model.constant.PaymentStatus;
import org.greenflow.billing.model.entity.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayPalService {

    @Value("${host}")
    private String backendHost;

    private final PayPalHttpClient client;

    /**
     * Creates a PayPal order for the given payment. Fills the external payment ID and URL in the given payment object.
     * If the payment already has an external payment ID and URL, it will not create a new order.
     * @param payment the payment object to create an order for
     * @return the updated payment object with the external payment ID and URL
     */
    public Payment createPayPalOrderForPayment(Payment payment){
        if (payment.getExternalPaymentId() == null || payment.getExternalPaymentUrl() == null) {
            long amountCents = payment.getAmount()
                    .multiply(new BigDecimal(100))
                    .longValue();

            OrdersCreateRequest request = createRequest(payment, amountCents);
            Order order;
            try {
                order = client.execute(request).result();
            } catch (IOException e) {
                throw new GreenFlowException(500, "Exception during Paypal api call", e);
            }

            String approveUrl = order.links().stream()
                    .filter(l -> "approve".equals(l.rel()))
                    .findFirst()
                    .orElseThrow(() -> new GreenFlowException(500, "No approve link in PayPal response"))
                    .href();

            payment.setExternalPaymentId(order.id());
            payment.setExternalPaymentUrl(approveUrl);
            payment.setStatus(PaymentStatus.PENDING);
        }
        return payment;
    }

    private OrdersCreateRequest createRequest(Payment payment, long amountCents) {
        return new OrdersCreateRequest()
                .requestBody(new OrderRequest()
                        .checkoutPaymentIntent("CAPTURE")
                        .purchaseUnits(List.of(
                                new PurchaseUnitRequest()
                                        .referenceId(payment.getId())
                                        .amountWithBreakdown(new AmountWithBreakdown()
                                                .currencyCode(payment.getCurrency())
                                                .value(String.format("%.2f", amountCents / 100.0))
                                        )
                        ))
                        .applicationContext(new ApplicationContext()
                                .returnUrl("http://" + backendHost + "/api/v1/payment/success?paymentId=" + payment.getId())
                                .cancelUrl("http://" + backendHost + "/api/v1/payment/cancel?paymentId=" + payment.getId())
                        )
                );
    }
}
