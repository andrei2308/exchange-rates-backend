package webapp.exchangerates.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webapp.exchangerates.domain.model.ExchangeRate;
import webapp.exchangerates.service.exchange.rate.ExchangeRateService;

@RestController
@RequestMapping("/api/exchange-rate")
public class RatesController {
    private final ExchangeRateService exchangeRateService;

    public RatesController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping()
    public ResponseEntity<ExchangeRate> getRates(){
        return ResponseEntity.ok(exchangeRateService.getExchangeRate());
    }
}
