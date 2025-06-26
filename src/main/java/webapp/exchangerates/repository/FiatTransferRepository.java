package webapp.exchangerates.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webapp.exchangerates.domain.model.FiatTransfer;

import java.util.Optional;

@Repository
public interface FiatTransferRepository extends JpaRepository<FiatTransfer, Long> {

    Optional<FiatTransfer> findByPaymentIntentId(String paymentIntentId);
}
