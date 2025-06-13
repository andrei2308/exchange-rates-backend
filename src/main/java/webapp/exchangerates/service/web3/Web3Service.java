package webapp.exchangerates.service.web3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

import java.io.IOException;
import java.math.BigInteger;

@Service
public class Web3Service {

    private final Web3j web3j;

    @Autowired
    public Web3Service(Web3j web3j) {
        this.web3j = web3j;
    }

    public String getClientVersion() throws IOException {
        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
        return web3ClientVersion.getWeb3ClientVersion();
    }

    public BigInteger getBlockNumber() throws IOException {
        EthBlockNumber blockNumber = web3j.ethBlockNumber().send();
        return blockNumber.getBlockNumber();
    }

    public BigInteger getGasPrice() throws IOException {
        EthGasPrice gasPrice = web3j.ethGasPrice().send();
        return gasPrice.getGasPrice();
    }

    public BigInteger getBalance(String address) throws IOException {
        return web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .send()
                .getBalance();
    }
}
