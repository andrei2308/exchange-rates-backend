package webapp.exchangerates.configuration.blockchain.credentials;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;

@Configuration
public class EthereumCredentialsConfiguration {

    @Value("${ethereum.private-key}")
    private String privateKey;

    @Bean
    public Credentials credentials() {
        return Credentials.create(privateKey);
    }
}