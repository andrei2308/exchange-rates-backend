package webapp.exchangerates.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import webapp.exchangerates.model.ExchangeRateApiResponse;

@Service
public class ExchangeRateSchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateSchedulerService.class);

    private final RestTemplate restTemplate;
    private final ExchangeRateService exchangeRateService;

    //TODO: add revolut api instead of mock
    @Value("${exchange.api.url}")
    private String apiUrl;

    public ExchangeRateSchedulerService(ExchangeRateService exchangeRateService) {
        this.restTemplate = new RestTemplate();
        this.exchangeRateService = exchangeRateService;
    }

    @Scheduled(fixedRate = 5000)
    public void updateExchangeRates() {
        logger.info("Fetching latest exchange rates");
        try {
            String url = apiUrl;

            ExchangeRateApiResponse response = restTemplate.getForObject(url, ExchangeRateApiResponse.class);

            if (response != null) {
                double eurUsd = response.getEurUsd();
                exchangeRateService.updateExchangeRate(eurUsd);
                logger.info("Successfully updated exchange rate: EUR/USD = {}", eurUsd);
            } else {
                logger.error("Failed to fetch exchange rates: empty response");
            }
        } catch (Exception e) {
            logger.error("Error updating exchange rates", e);
        }
    }
}