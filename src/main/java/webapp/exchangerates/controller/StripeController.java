package webapp.exchangerates.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import webapp.exchangerates.domain.dto.CreatePaymentIntentRequest;
import webapp.exchangerates.domain.dto.PaymentIntentResponse;
import webapp.exchangerates.domain.dto.PaymentIntentStatusResponse;
import webapp.exchangerates.service.stripe.FiatTransferService;
import webapp.exchangerates.service.stripe.StripeService;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;
    private final FiatTransferService fiatTransferService;

    @PostMapping("/payment-intent")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(
            @RequestBody CreatePaymentIntentRequest request,
            @AuthenticationPrincipal Principal principal) {
        try {
            String userId = "1";

            Long amountInCents = Math.round(request.getAmount() * 100);

            PaymentIntent intent = stripeService.createPaymentIntent(
                    amountInCents,
                    request.getCurrency(),
                    request.getRecipientAddress(),
                    userId
            );

            fiatTransferService.createTransferRecord(
                    userId,
                    request.getRecipientAddress(),
                    request.getAmount(),
                    request.getCurrency(),
                    intent.getId()
            );

            return ResponseEntity.ok(PaymentIntentResponse.builder()
                    .clientSecret(intent.getClientSecret())
                    .paymentIntentId(intent.getId())
                    .amount(intent.getAmount())
                    .currency(intent.getCurrency())
                    .status(intent.getStatus())
                    .build());
        } catch (StripeException e) {
            log.error("Error creating payment intent", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/payment-intent/{id}")
    public ResponseEntity<PaymentIntentStatusResponse> getPaymentIntentStatus(
            @PathVariable String id,
            @AuthenticationPrincipal Principal principal) {
        try {
            PaymentIntent intent = stripeService.retrievePaymentIntent(id);

            return ResponseEntity.ok(PaymentIntentStatusResponse.builder()
                    .paymentIntentId(intent.getId())
                    .status(intent.getStatus())
                    .amount(intent.getAmount())
                    .currency(intent.getCurrency())
                    .build());
        } catch (StripeException e) {
            log.error("Error retrieving payment intent", e);
            return ResponseEntity.notFound().build();
        }
    }
}
