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

    /**
     * Set a new exchange rate
     * @param newRate The new exchange rate to set
     * @return Transaction receipt details
     */
    @PostMapping("/rate")
    public ResponseEntity<Map<String, Object>> setExchangeRate(
            @RequestParam BigInteger newRate) {
        try {
            TransactionReceipt receipt = exchangeContractService.setExchangeRate(newRate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionHash", receipt.getTransactionHash());
            response.put("blockNumber", receipt.getBlockNumber());
            response.put("gasUsed", receipt.getGasUsed());
            response.put("status", receipt.getStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("newRateRequested", newRate.toString());

            return ResponseEntity.accepted().body(errorResponse);
        }
    }
}