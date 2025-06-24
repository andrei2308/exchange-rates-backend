package webapp.exchangerates.service.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    /**
     * Create a Payment Intent for fiat transfer
     */
    public PaymentIntent createPaymentIntent(Long amountInCents, String currency,
                                             String recipientAddress, String userId) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency.toLowerCase())
                .setDescription("Fiat transfer to " + recipientAddress)
                .putMetadata("user_id", userId)
                .putMetadata("recipient_address", recipientAddress)
                .putMetadata("transfer_type", "fiat_transfer")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        return PaymentIntent.create(params);
    }

    /**
     * Create a customer for the user
     */
    public Customer createCustomer(String email, String name, String userId) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(email)
                .setName(name)
                .putMetadata("user_id", userId)
                .build();

        return Customer.create(params);
    }

    /**
     * Retrieve a customer by Stripe customer ID
     */
    public Customer retrieveCustomer(String customerId) throws StripeException {
        return Customer.retrieve(customerId);
    }

    /**
     * Create a Setup Intent for saving payment methods
     */
    public SetupIntent createSetupIntent(String customerId) throws StripeException {
        SetupIntentCreateParams params = SetupIntentCreateParams.builder()
                .setCustomer(customerId)
                .setAutomaticPaymentMethods(
                        SetupIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        return SetupIntent.create(params);
    }

    /**
     * List customer's payment methods
     */
    public PaymentMethodCollection listPaymentMethods(String customerId) throws StripeException {
        PaymentMethodListParams params = PaymentMethodListParams.builder()
                .setCustomer(customerId)
                .setType(PaymentMethodListParams.Type.CARD)
                .build();

        return PaymentMethod.list(params);
    }

    /**
     * Create a payout to bank account (for the actual fiat transfer)
     */
    public Transfer createTransfer(Long amountInCents, String currency,
                                   String destinationAccount, String description) throws StripeException {
        TransferCreateParams params = TransferCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency.toLowerCase())
                .setDestination(destinationAccount)
                .setDescription(description)
                .build();

        return Transfer.create(params);
    }

    /**
     * Retrieve a Payment Intent
     */
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }

    /**
     * Confirm a Payment Intent (for server-side confirmation)
     */
    public PaymentIntent confirmPaymentIntent(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return paymentIntent.confirm();
    }

    /**
     * Cancel a Payment Intent
     */
    public PaymentIntent cancelPaymentIntent(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return paymentIntent.cancel();
    }

    /**
     * Create a refund
     */
    public Refund createRefund(String paymentIntentId, Long amountInCents) throws StripeException {
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .setAmount(amountInCents)
                .build();

        return Refund.create(params);
    }
}
