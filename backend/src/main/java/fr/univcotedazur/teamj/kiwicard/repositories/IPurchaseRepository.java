package fr.univcotedazur.teamj.kiwicard.repositories;


import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPurchaseRepository extends JpaRepository<Purchase, Long> {
    @Query(value =
            "SELECT p FROM Purchase p " //+
//            "JOIN cart c ON p.cart_id = c.id " +
//            "JOIN customer cust ON c.customer_id = cust.id " +
//            "JOIN partner par ON c.partner_id = par.id " +
//            "WHERE cust.email = :customerEmail " +
//            "AND par.id = :partnerId " +
//            "AND p.already_consumed_in_a_perk = false " +
//            "ORDER BY p.payment_timestamp ASC " +
//            "LIMIT :nbPurchasesToConsume"
            )
    List<Purchase> findPurchasesToConsume(@Param("customerId") long customerId,
                                          @Param("partnerId") long partnerId,
                                          @Param("nbPurchasesToConsume") int nbPurchasesToConsume);
}
