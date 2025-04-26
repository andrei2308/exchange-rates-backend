package webapp.exchangerates.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import webapp.exchangerates.service.ExchangeContractService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeController {

    private final ExchangeContractService exchangeContractService;

    @Autowired
    public ExchangeController(ExchangeContractService exchangeContractService) {
        this.exchangeContractService = exchangeContractService;
    }

    /**
     * Get the current exchange rate
     * @return The exchange rate as a BigInteger and its decimal representation
     */
    @GetMapping("/rate")
    public ResponseEntity<Map<String, Object>> getExchangeRate() throws IOException {
        BigInteger rate = exchangeContractService.getExchangeRate();

        Map<String, Object> response = new HashMap<>();
        response.put("rateRaw", rate);
        response.put("rateDecimal", rate.toString());
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }
}