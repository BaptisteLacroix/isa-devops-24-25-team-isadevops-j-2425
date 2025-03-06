package fr.univcotedazur.teamj.kiwicard.repositories;


import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
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

    @Query(
    """
        SELECT p FROM Purchase p, Customer c
            where c.email = :customerEmail
            and p member of c.purchaseList
            AND p.cart.partner.partnerId = :partnerId
            ORDER BY p.payment.timestamp DESC
            LIMIT :nbPurchasesToConsume
    """
    )
    List<Purchase> findPurchasesToConsume(@Param("customerEmail") String customerEmail,
                                          @Param("partnerId") long partnerId,
                                          @Param("nbPurchasesToConsume") int nbPurchasesToConsume);

    @Query(
            """
                SELECT p FROM Purchase p, Customer c
                    where c.email = :customerEmail
                    and p member of c.purchaseList
                    ORDER BY p.payment.timestamp DESC
                    LIMIT :nbPurchasesToConsume
            """
    )
    List<Purchase> findPurchasesToConsume(@Param("customerEmail") String customerEmail,
                                          @Param("nbPurchasesToConsume") int nbPurchasesToConsume);

    Object queryPurchaseByPurchaseId(Long purchaseId);
}

