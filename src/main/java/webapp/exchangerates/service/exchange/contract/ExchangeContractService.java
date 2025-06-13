package webapp.exchangerates.service.exchange.contract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Optional;

@Service
public class ExchangeContractService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final String contractAddress;
    private final ContractGasProvider gasProvider;

    @Autowired
    public ExchangeContractService(
            Web3j web3j,
            Credentials credentials,
            @Value("${ethereum.exchange-contract-address}") String contractAddress) {
        this.web3j = web3j;
        this.credentials = credentials;
        this.contractAddress = contractAddress;
        this.gasProvider = new StaticGasProvider(
                BigInteger.valueOf(50_000_000_000L), // 50 Gwei
                BigInteger.valueOf(500_000L) // Higher gas limit
        );
    }

    /**
     * Set a new exchange rate in the contract
     *
     * @param newExchangeRate The new exchange rate to set
     */
    public void setExchangeRate(BigInteger newExchangeRate)
            throws Exception {
        Function function = new Function(
                "setExchangeRate",
                Collections.singletonList(new Int256(newExchangeRate)),
                Collections.emptyList()
        );

        String encodedFunction = FunctionEncoder.encode(function);

        BigInteger nonce = web3j.ethGetTransactionCount(
                credentials.getAddress(),
                DefaultBlockParameterName.LATEST
        ).sendAsync().get().getTransactionCount();

        BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();

        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasProvider.getGasLimit(),
                contractAddress,
                encodedFunction
        );

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();

        if(ethSendTransaction.hasError()){
            throw new Exception("Error updating exchange rate " + ethSendTransaction.getError().getMessage());
        }

        waitForTransactionReceipt(ethSendTransaction.getTransactionHash());
    }

    /**
     * Wait for transaction receipt with polling
     *
     * @param transactionHash The transaction hash to check
     */
    private void waitForTransactionReceipt(String transactionHash)
            throws IOException, InterruptedException {
        long pollingInterval = 1000;
        long timeout = 120000;
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() < startTime + timeout) {
            Optional<TransactionReceipt> receiptOptional =
                    web3j.ethGetTransactionReceipt(transactionHash).send().getTransactionReceipt();

            if (receiptOptional.isPresent()) {
                receiptOptional.get();
                return;
            }

            Thread.sleep(pollingInterval);
        }

        throw new RuntimeException("Transaction receipt not found after timeout of " + timeout / 1000 + " seconds. Transaction hash: " + transactionHash);
    }
}