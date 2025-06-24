package webapp.exchangerates.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class ExchangeRateApiResponse {
    private Long id;
    private Double eurUsd;
    private Long lastUpdated;
}