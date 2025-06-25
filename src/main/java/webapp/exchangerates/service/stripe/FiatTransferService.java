package webapp.exchangerates.service.stripe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.exchangerates.domain.model.FiatTransfer;
import webapp.exchangerates.repository.FiatTransferRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class FiatTransferService {

    private final FiatTransferRepository fiatTransferRepository;

    @Transactional
    public FiatTransfer createTransferRecord(String recipientAddress,
                                             Double amount, String currency, String paymentIntentId) {
        FiatTransfer transfer = new FiatTransfer();
        transfer.setRecipientAddress(recipientAddress);
        transfer.setAmount(amount);
        transfer.setCurrency(currency);
        transfer.setPaymentIntentId(paymentIntentId);
        transfer.setStatus("PENDING");
        transfer.setCreatedAt(LocalDateTime.now());

        return fiatTransferRepository.save(transfer);
    }
}
