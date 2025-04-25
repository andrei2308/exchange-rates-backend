package webapp.exchangerates.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import webapp.exchangerates.model.ExchangeRate;
import webapp.exchangerates.service.ExchangeRateService;

@RestController
public class RatesController {
    private final ExchangeRateService exchangeRateService;

    public RatesController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/rate")
    public ResponseEntity<ExchangeRate> getRates(){
        return ResponseEntity.ok(exchangeRateService.getExchangeRate());
    }
}
