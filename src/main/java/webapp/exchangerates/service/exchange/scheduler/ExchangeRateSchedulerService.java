package webapp.exchangerates.service.exchange.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import webapp.exchangerates.domain.model.ExchangeRateApiResponse;
import webapp.exchangerates.service.exchange.contract.ExchangeContractService;
import webapp.exchangerates.service.exchange.rate.ExchangeRateService;

import java.math.BigInteger;

@Service
public class ExchangeRateSchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateSchedulerService.class);

    private final RestTemplate restTemplate;
    private final ExchangeContractService exchangeContractService;
    private final ExchangeRateService exchangeRateService;

    @Value("${exchange.api.url}")
    private String apiUrl;

    public ExchangeRateSchedulerService(ExchangeContractService exchangeContractService, ExchangeRateService exchangeRateService, RestTemplate restTemplate) {
        this.exchangeContractService = exchangeContractService;
        this.restTemplate = restTemplate;
        this.exchangeRateService = exchangeRateService;
    }

    @Scheduled(fixedRate = 15000000)
    public void updateExchangeRates() {
        logger.info("Fetching latest exchange rates");
        try {
            String url = apiUrl;

            ExchangeRateApiResponse response = restTemplate.getForObject(url, ExchangeRateApiResponse.class);

            if (response != null) {
                double eurUsd = response.getEurUsd();
                BigInteger rateForBlockchain = convertRateToBlockchainFormat(eurUsd);
                try {
                    exchangeContractService.setExchangeRate(rateForBlockchain);
                    logger.info("Successfully updated blockchain exchange rate: EUR/USD = {}", eurUsd);
                    exchangeRateService.updateExchangeRate(eurUsd);
                    logger.info("Successfully updated exchange rate: EUR/USD = {}", eurUsd);
                } catch (Exception e) {
                    logger.error("Failed to update blockchain exchange rate: {}", e.getMessage());
                }
            } else {
                logger.error("Failed to fetch exchange rates: empty response");
            }
        } catch (Exception e) {
            logger.error("Error updating exchange rates", e);
        }
    }
    /**
     * Converts a double exchange rate to the format expected by the blockchain
     * You may need to adjust this based on your contract's requirements
     */
    private BigInteger convertRateToBlockchainFormat(double rate) {
        long scaleFactor = 100000000L; // 10^8
        long scaledRate = Math.round(rate * scaleFactor);
        return BigInteger.valueOf(scaledRate);
    }
}