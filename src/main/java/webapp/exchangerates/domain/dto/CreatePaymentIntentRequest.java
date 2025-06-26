package webapp.exchangerates.domain.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class CreatePaymentIntentRequest {
    @NotNull
    private Double amount;

    @NotNull
    private String currency;

    @NotNull
    private String recipientAddress;
}
