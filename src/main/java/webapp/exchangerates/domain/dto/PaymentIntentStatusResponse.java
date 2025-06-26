package webapp.exchangerates.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentIntentStatusResponse {
    private String paymentIntentId;
    private String status;
    private Long amount;
    private String currency;
}
