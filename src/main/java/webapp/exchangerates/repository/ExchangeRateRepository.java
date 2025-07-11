package webapp.exchangerates.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webapp.exchangerates.domain.model.ExchangeRate;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
}
