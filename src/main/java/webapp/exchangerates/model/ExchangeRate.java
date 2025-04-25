package webapp.exchangerates.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class ExchangeRate {
    @Id
    private Long id = 1L;

    private double eurUsd;

    private Long lastUpdated;
}
