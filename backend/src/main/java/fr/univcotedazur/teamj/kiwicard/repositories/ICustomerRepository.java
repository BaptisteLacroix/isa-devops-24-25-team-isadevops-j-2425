package fr.univcotedazur.teamj.kiwicard.repositories;

import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String customerEmail);

    Optional<Customer> findByCardNumber(String cardNumber);

    @Query("SELECT COUNT(c) FROM Customer c" +
            " WHERE (" +
            "   SELECT COUNT(p) " +
            "   FROM c.purchaseList p " +
            "   WHERE p.payment.timestamp > :lastWeekDate" +
            " ) >= :nbPurchaseRequired"
    )
    int refreshVfpStatus(@Param("nbPurchaseRequired") int nbPurchaseRequired, @Param("lastWeekDate") LocalDateTime lastWeekDate);
}
