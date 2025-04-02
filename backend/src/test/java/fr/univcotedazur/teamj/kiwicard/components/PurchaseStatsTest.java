package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.PurchaseCreationUtils;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.entities.*;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseStats;
import fr.univcotedazur.teamj.kiwicard.repositories.IPurchaseRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static fr.univcotedazur.teamj.kiwicard.DateUtils.getLocalDateTimes;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class PurchaseStatsTest {
    @Autowired
    EntityManager entityManager;

    @Autowired
    IPurchaseStats purchaseStats;

    @Autowired
    IPurchaseRepository purchaseRepository;

    private Customer customer;
    private Partner partner;

    private Map<LocalTime, Integer> expectedDay1Aggregation = new LinkedHashMap<>();
    private LocalDate baseDate;


    @Transactional
    @BeforeEach
    void setUp() {
        entityManager.clear();
        assertNotNull(purchaseRepository);

        customer = new Customer(new CustomerSubscribeDTO("test@example.com", "Alice", "Bob", "14 rue du trottoir, Draguignan"), "51");
        entityManager.persist(customer);

        partner = new Partner("Boulange", "14 rue du trottoir, Draguignan");
        entityManager.persist(partner);
        baseDate = LocalDate.of(2025, 3, 16);
        this.expectedDay1Aggregation = new PurchaseCreationUtils(entityManager, 150).createDummyPurchasesForDate(baseDate, getLocalDateTimes(baseDate, Duration.ofHours(1)), partner);
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional
    @Test
    void testAggregateOneDay() throws UnknownPartnerIdException {
        Cart cart3 = new Cart();
        cart3.setPartner(partner);
        customer.setCart(cart3);
        entityManager.persist(cart3);
        Payment payment3 = new Payment(2, LocalDate.of(2025, 3, 15).atStartOfDay());
        entityManager.persist(payment3);
        Purchase purchase3 = new Purchase(payment3, cart3);
        entityManager.persist(purchase3);
        customer.addPurchase(purchase3);
        Map<LocalTime, Integer> aggregation = purchaseStats.aggregatePurchasesByDayAndDuration(partner.getPartnerId(), baseDate, Duration.ofHours(1));
        assertEquals(aggregation, this.expectedDay1Aggregation);
    }
}
