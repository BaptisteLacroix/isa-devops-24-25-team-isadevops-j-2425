package fr.univcotedazur.teamj.kiwicard.repositories;


import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query(
    """
        SELECT p FROM Purchase p, Customer c
            where c.email = :customerEmail
            and p member of c.purchaseList
            AND p.cart.partner.partnerId = :partnerId
            ORDER BY p.payment.timestamp DESC
            LIMIT :nbPurchases
    """
    )
    List<Purchase> findAllByCustomerAndPartner(@Param("customerEmail") String customerEmail,
                                               @Param("partnerId") long partnerId,
                                               @Param("nbPurchases") int nbPurchases);

    @Query(
            """
                SELECT p FROM Purchase p, Customer c
                    where c.email = :customerEmail
                    and p member of c.purchaseList
                    AND p.cart.partner.partnerId = :partnerId
                    ORDER BY p.payment.timestamp DESC
            """
    )
    List<Purchase> findAllByCustomerAndPartner(@Param("customerEmail") String customerEmail,
                                               @Param("partnerId") long partnerId);

    @Query(
            """
                SELECT p FROM Purchase p, Customer c
                    where c.email = :customerEmail
                    and p member of c.purchaseList
                    ORDER BY p.payment.timestamp DESC
                    LIMIT :nbPurchases
            """
    )
    List<Purchase> findAllByCustomer(@Param("customerEmail") String customerEmail, @Param("nbPurchases") int nbPurchases);


//    @Query(
//            """
//                SELECT ci FROM Purchase p, Customer c
//                join CartItem ci on ci member of p.cart.itemList
//                where c.email = :customerEmail
//                and p member of c.purchaseList
//                AND p.cart.partner.partnerId = :partnerId
//                ORDER BY p.payment.timestamp DESC
//                limit :nbItems
//            """)
//    List<CartItem> findLastItemIdsByCustomerAndPartner(
//            @Param("customerEmail") String customerEmail,
//            @Param("partnerId") long partnerId,
//            @Param("nbItemsConsumed") int nbItems);
}

