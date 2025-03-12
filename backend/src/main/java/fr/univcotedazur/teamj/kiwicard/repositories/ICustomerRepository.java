package fr.univcotedazur.teamj.kiwicard.repositories;

import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String customerEmail);

    Optional<Customer> findByCardNumber(String cardNumber);

@Modifying
@Query("UPDATE Customer c " +
       "SET c.vfp = CASE WHEN (" +
       "   SELECT COUNT(p) " +
       "   FROM c.purchaseList p " +
       "   WHERE p.payment.timestamp >= :startDate" +
       "   AND p.payment.timestamp < :endDate" +
       " ) >= :nbPurchaseRequired THEN true ELSE false END"
)
    void refreshVfpStatus(@Param("nbPurchaseRequired") int nbPurchaseRequired, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
