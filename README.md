# Exchange Rates Backend

An automated Java Spring Boot service that fetches exchange rates from external APIs, stores them in an H2 database, and updates rates in a smart contract. The entire process is automated using a scheduler.

## Features

- Automated exchange rate fetching from external API sources
- H2 database storage of exchange rate data
- Smart contract integration for on-chain rate updates
- Scheduled execution using Spring's task scheduling

## Architecture

The system consists of the following components:

1. **Rate Fetcher**: Spring service that calls an external API to get the latest exchange rates
2. **Database Service**: Persists the fetched rates using Spring Data JPA and H2
3. **Smart Contract Updater**: Updates the exchange rates in the blockchain smart contract
4. **Scheduler**: Spring's @Scheduled tasks that manage the timing of rate updates

## Prerequisites

- Java JDK 11 or higher
- Maven
- Ethereum wallet with funds for gas fees
- Access to an Ethereum node (Infura, Alchemy, or your own)

## Installation

1. Clone the repository:
   ```
   git clone https://github.com/andrei2308/exchange-rates-backend.git
   cd exchange-rates-backend
   ```

2. Build the project:
   ```
   mvn clean install
   ```

3. Create an `application.properties` or `application.yml` file in the `src/main/resources` directory with the following variables:
   ```properties
   # API Configuration
   exchange.rate.api.key=your_api_key
   exchange.rate.api.url=https://api.example.com/v1/rates

   # Database Configuration
   spring.datasource.url=jdbc:h2:file:./data/exchange_rates
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=password
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   spring.h2.console.enabled=true

   # Blockchain Configuration
   ethereum.provider.url=https://mainnet.infura.io/v3/your_project_id
   ethereum.wallet.private.key=your_wallet_private_key
   smart.contract.address=0x1234567890abcdef1234567890abcdef12345678

   # Scheduler Configuration
   exchange.rate.update.interval=3600000  # in milliseconds (1 hour)
   ```

## Usage

You can run the application in several ways:

### Using Maven
```
mvn spring-boot:run
```

### Using the JAR file
```
java -jar target/exchange-rates-backend-0.0.1-SNAPSHOT.jar
```

The service will automatically:
1. Start when the application boots
2. Fetch exchange rates at the configured interval
3. Store the rates in the H2 database
4. Update the rates in the smart contract

## Configuration Options

### Rate Sources

You can configure rate sources in the application properties:

```properties
# Primary source
exchange.rate.sources[0].name=Primary
exchange.rate.sources[0].url=${exchange.rate.api.url}
exchange.rate.sources[0].apiKey=${exchange.rate.api.key}
exchange.rate.sources[0].currencies=USD,EUR,GBP,JPY

# Additional source example
exchange.rate.sources[1].name=Secondary
exchange.rate.sources[1].url=https://another-api.com/rates
exchange.rate.sources[1].apiKey=another_api_key
exchange.rate.sources[1].currencies=USD,EUR,GBP
```

### Scheduler

Adjust the update frequency in the application properties by changing the `exchange.rate.update.interval` value. The Spring scheduler is configured in the code using annotations like:

```java
@Scheduled(fixedRateString = "${exchange.rate.update.interval}")
public void updateExchangeRates() {
    // Rate updating logic
}
```

## Smart Contract Integration

The service is designed to work with a smart contract that has an update function with the following signature:

```solidity
function updateRate(string memory currencyPair, uint256 rate) external;
```

The Web3j library is likely used to interact with the Ethereum blockchain.

## Database Schema

Exchange rates are stored in the H2 database with the following entity structure:

```java
@Entity
@Table(name = "exchange_rates")
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String currencyPair;
    private BigDecimal rate;
    private String source;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    
    // Getters and setters
}
```

## Error Handling

The service includes comprehensive error handling for:
- API failures
- Database connection issues
- Smart contract interaction errors

All errors are logged using SLF4J/Logback.

## Monitoring

The service outputs logs using Spring Boot's default logging configuration. You can access the H2 console at `http://localhost:8080/h2-console` when `spring.h2.console.enabled=true`.

## Development

### Running Tests

```
mvn test
```

### Running in Development Mode

```
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## API Documentation

If the application includes REST endpoints, they might be documented with Swagger/OpenAPI:

```
http://localhost:8080/swagger-ui.html
```

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

Andrei ([@andrei2308](https://github.com/andrei2308))
