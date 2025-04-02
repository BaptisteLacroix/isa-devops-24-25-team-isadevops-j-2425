package fr.univcotedazur.teamj.kiwicard.repositories;


import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Query(
            """
                SELECT p FROM Purchase p, Customer c
                    where c.email = :customerEmail
                    and p member of c.purchaseList
                    ORDER BY p.payment.timestamp DESC
            """
    )
    List<Purchase> findAllByCustomer(@Param("customerEmail") String customerEmail);

    @Query(
            """
                SELECT p FROM Purchase p
                    where p.cart.partner.partnerId = :partnerId
                    ORDER BY p.payment.timestamp DESC
            """
    )
    List<Purchase> findAllByPartner(@Param("partnerId") long partnerId);

    @Query(
            """
                SELECT p FROM Purchase p
                    where p.cart.partner.partnerId = :partnerId
                    ORDER BY p.payment.timestamp DESC
                    LIMIT :limit
            """
    )
    List<Purchase> findAllByPartner(long partnerId, int limit);

    @Query(
            value = """
            select * from Purchase as p
            inner join Payment pay on pay.payment_id = p.payment_payment_id
            and pay.timestamp >= :startOfDay
            and pay.timestamp < :endOfDay
            """
    ,nativeQuery = true)
    List<Purchase> findAllByPartnerAndDay(
            @Param("partnerId") long partnerId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}

