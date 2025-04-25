package webapp.exchangerates.service;

import org.springframework.stereotype.Service;
import webapp.exchangerates.model.ExchangeRate;
import webapp.exchangerates.repository.ExchangeRateRepository;

@Service
public class ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
        initializeExchangeRate();
    }

    private void initializeExchangeRate() {
        if (exchangeRateRepository.count() == 0) {
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setEurUsd(1.0);
            exchangeRate.setLastUpdated(System.currentTimeMillis());
            exchangeRateRepository.save(exchangeRate);
        }
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRateRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Exchange rate not found"));
    }

    public void updateExchangeRate(double eurUsd) {
        ExchangeRate exchangeRate = getExchangeRate();
        exchangeRate.setEurUsd(eurUsd);
        exchangeRate.setLastUpdated(System.currentTimeMillis());
        exchangeRateRepository.save(exchangeRate);
    }
}