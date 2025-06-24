package webapp.exchangerates.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webapp.exchangerates.service.stripe.FiatTransferService;

@Slf4j
@RestController
@RequestMapping("/api/v1/stripe/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final FiatTransferService fiatTransferService;
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            log.error("Invalid signature", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElse(null);
                if (paymentIntent != null) {
                    handlePaymentIntentSucceeded(paymentIntent);
                }
                break;

            case "payment_intent.payment_failed":
                PaymentIntent failedIntent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElse(null);
                if (failedIntent != null) {
                    handlePaymentIntentFailed(failedIntent);
                }
                break;

            case "charge.succeeded":
                Charge charge = (Charge) event.getDataObjectDeserializer()
                        .getObject().orElse(null);
                if (charge != null) {
                    handleChargeSucceeded(charge);
                }
                break;

            default:
                log.info("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("Event received");
    }

    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent) {
        log.info("Payment succeeded: {}", paymentIntent.getId());

        fiatTransferService.updateTransferStatus(
                paymentIntent.getId(),
                "COMPLETED"
        );

        String recipientAddress = paymentIntent.getMetadata().get("recipient_address");
        String userId = paymentIntent.getMetadata().get("user_id");

        fiatTransferService.processFiatTransfer(
                paymentIntent.getId(),
                userId,
                recipientAddress,
                paymentIntent.getAmount(),
                paymentIntent.getCurrency()
        );
    }

    private void handlePaymentIntentFailed(PaymentIntent paymentIntent) {
        log.error("Payment failed: {}", paymentIntent.getId());

        fiatTransferService.updateTransferStatus(
                paymentIntent.getId(),
                "FAILED"
        );
    }

    private void handleChargeSucceeded(Charge charge) {
        log.info("Charge succeeded: {}", charge.getId());
    }
}