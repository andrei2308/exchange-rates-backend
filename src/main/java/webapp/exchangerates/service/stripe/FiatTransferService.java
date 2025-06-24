package webapp.exchangerates.service.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.exchangerates.domain.model.FiatTransfer;
import webapp.exchangerates.domain.model.User;
import webapp.exchangerates.repository.FiatTransferRepository;
import webapp.exchangerates.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class FiatTransferService {

    private final FiatTransferRepository fiatTransferRepository;
    private final UserRepository userRepository;
    private final StripeService stripeService;

    @Transactional
    public FiatTransfer createTransferRecord(String userId, String recipientAddress,
                                             Double amount, String currency, String paymentIntentId) {
        FiatTransfer transfer = new FiatTransfer();
        transfer.setUserId(userId);
        transfer.setRecipientAddress(recipientAddress);
        transfer.setAmount(amount);
        transfer.setCurrency(currency);
        transfer.setPaymentIntentId(paymentIntentId);
        transfer.setStatus("PENDING");
        transfer.setCreatedAt(LocalDateTime.now());

        return fiatTransferRepository.save(transfer);
    }

    @Transactional
    public void updateTransferStatus(String paymentIntentId, String status) {
        FiatTransfer transfer = fiatTransferRepository.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));

        transfer.setStatus(status);
        transfer.setUpdatedAt(LocalDateTime.now());

        fiatTransferRepository.save(transfer);
    }

    @Transactional
    public String getOrCreateStripeCustomer(String userId) throws StripeException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStripeCustomerId() != null) {
            return user.getStripeCustomerId();
        }

        Customer customer = stripeService.createCustomer(
                user.getEmail(),
                user.getName(),
                userId
        );

        user.setStripeCustomerId(customer.getId());
        userRepository.save(user);

        return customer.getId();
    }

    public void processFiatTransfer(String paymentIntentId, String userId,
                                    String recipientAddress, Long amountInCents, String currency) {
        // TODO: Implement actual fiat transfer logic

        log.info("Processing fiat transfer: {} {} to {}",
                amountInCents / 100.0, currency, recipientAddress);
    }
}
