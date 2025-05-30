package org.greenflow.billing.service;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.billing.model.constant.PaymentStatus;
import org.greenflow.billing.model.entity.Payment;
import org.greenflow.billing.output.persistent.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * Service class for handling PayPal payment operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PayPalService {

    /**
     * The backend host URL, used for constructing return and cancel URLs.
     */
    @Value("${api.host.backend}")
    private String backendHost;

    private final PayPalHttpClient client;
    private final PaymentRepository paymentRepository;

    /**
     * Creates a PayPal Order, saves its ID and approval URL in the Payment entity,
     * and returns the approval URL for redirecting the user.
     *
     * @param payment The Payment entity containing details of the payment to be processed.
     * @return The approval URL for redirecting the user to PayPal.
     * @throws IOException If an error occurs while communicating with the PayPal API.
     */
    public String createPaymentUrl(Payment payment) throws IOException {
        // Format the payment amount to two decimal places using US locale.
        DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));
        String amount = df.format(payment.getAmount());

        // Create a PayPal OrderRequest with the payment details.
        OrderRequest orderRequest = new OrderRequest()
                .checkoutPaymentIntent("CAPTURE")
                .purchaseUnits(List.of(
                        new PurchaseUnitRequest()
                                .referenceId(payment.getId())
                                .amountWithBreakdown(new AmountWithBreakdown()
                                        .currencyCode(payment.getCurrency())
                                        .value(amount)
                                )
                ))
                .applicationContext(new ApplicationContext()
                        .returnUrl("http://" + backendHost + "/api/v1/billing/success?paymentId=" + payment.getId())
                        .cancelUrl("http://" + backendHost + "/api/v1/billing/cancel/" + payment.getId() + "?cancelled=true")
                );

        // Send the order creation request to PayPal.
        OrdersCreateRequest request = new OrdersCreateRequest()
                .requestBody(orderRequest);

        HttpResponse<Order> response = client.execute(request);
        Order order = response.result();

        // Extract the approval URL from the PayPal response.
        String approveUrl = order.links().stream()
                .filter(l -> "approve".equals(l.rel()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No approve link in PayPal response"))
                .href();

        // Update the Payment entity with the external payment ID, approval URL, and status.
        payment.setExternalPaymentId(order.id());
        payment.setExternalPaymentUrl(approveUrl);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        log.info("Created PayPal Order {} for payment {} – redirect URL: {}", order.id(), payment.getId(), approveUrl);
        return approveUrl;
    }
}
