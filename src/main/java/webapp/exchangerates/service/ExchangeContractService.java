package webapp.exchangerates.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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
                BigInteger.valueOf(20_000_000_000L),
                BigInteger.valueOf(200_000L)
        );
    }

    /**
     * Get the current exchange rate from the contract
     *
     * @return The exchange rate as a BigInteger
     */
    public BigInteger getExchangeRate() throws IOException {
        Function function = new Function(
                "getExchangeRate",
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Int256>() {
                })
        );

        String encodedFunction = FunctionEncoder.encode(function);

        EthCall response = web3j.ethCall(
                        Transaction.createEthCallTransaction(
                                credentials.getAddress(),
                                contractAddress,
                                encodedFunction),
                        DefaultBlockParameterName.LATEST)
                .send();

        List<Type> decodedResponse = FunctionReturnDecoder.decode(
                response.getValue(), function.getOutputParameters());

        if (decodedResponse.isEmpty()) {
            return BigInteger.ZERO;
        } else {
            return ((Int256) decodedResponse.get(0)).getValue();
        }
    }

    /**
     * Set a new exchange rate in the contract
     *
     * @param newExchangeRate The new exchange rate to set
     * @return The transaction receipt
     */
    public TransactionReceipt setExchangeRate(BigInteger newExchangeRate)
            throws IOException, InterruptedException {
        Function function = new Function(
                "setExchangeRate",
                Collections.singletonList(new Int256(newExchangeRate)),
                Collections.emptyList()
        );

        String encodedFunction = FunctionEncoder.encode(function);

        TransactionManager transactionManager = new RawTransactionManager(
                web3j,
                credentials,
                web3j.ethChainId().send().getChainId().longValue()
        );

        String transactionHash = transactionManager.sendTransaction(
                gasProvider.getGasPrice(),
                gasProvider.getGasLimit(),
                contractAddress,
                encodedFunction,
                BigInteger.ZERO).getTransactionHash();

        return waitForTransactionReceipt(transactionHash);
    }

    /**
     * Wait for transaction receipt with polling
     *
     * @param transactionHash The transaction hash to check
     * @return The transaction receipt
     */
    private TransactionReceipt waitForTransactionReceipt(String transactionHash)
            throws IOException, InterruptedException {
        long pollingInterval = 1000;
        long timeout = 120000;
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() < startTime + timeout) {
            Optional<TransactionReceipt> receiptOptional =
                    web3j.ethGetTransactionReceipt(transactionHash).send().getTransactionReceipt();

            if (receiptOptional.isPresent()) {
                return receiptOptional.get();
            }

            Thread.sleep(pollingInterval);
        }

        throw new RuntimeException("Transaction receipt not found after timeout of " + timeout / 1000 + " seconds. Transaction hash: " + transactionHash);
    }
}