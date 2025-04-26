package webapp.exchangerates.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webapp.exchangerates.service.Web3Service;

import java.io.IOException;
import java.math.BigInteger;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {

    private final Web3Service web3Service;

    @Autowired
    public BlockchainController(Web3Service web3Service) {
        this.web3Service = web3Service;
    }

    @GetMapping("/version")
    public String getClientVersion() throws IOException {
        return web3Service.getClientVersion();
    }

    @GetMapping("/block-number")
    public BigInteger getBlockNumber() throws IOException {
        return web3Service.getBlockNumber();
    }

    @GetMapping("/gas-price")
    public BigInteger getGasPrice() throws IOException {
        return web3Service.getGasPrice();
    }

    @GetMapping("/balance/{address}")
    public BigInteger getBalance(@PathVariable String address) throws IOException {
        return web3Service.getBalance(address);
    }
}