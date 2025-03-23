package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.entities.*;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseStats;
import jakarta.persistence.EntityManager;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static fr.univcotedazur.teamj.kiwicard.DateUtils.getLocalDateTimes;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PurchaseStatsTest {
    @Autowired
    EntityManager entityManager;

    @Autowired
    IPurchaseStats purchaseStats;

    private Customer customer;
    private Partner partner;

    private int nbPurchases = 0;
    private Map<LocalTime, Integer> expectedDay1Aggregation = new HashMap<>();


    @Transactional
    @BeforeEach
    void setUp() {
        entityManager.clear();

        customer = new Customer(new CustomerSubscribeDTO("test@example.com", "Alice", "Bob", "14 rue du trottoir, Draguignan"), "51");
        entityManager.persist(customer);

        partner = new Partner("Boulange", "14 rue du trottoir, Draguignan");
        entityManager.persist(partner);

        var baseDate = LocalDate.of(2025, 3, 16);

        this.expectedDay1Aggregation = createPurchasesForDate(baseDate, getLocalDateTimes(baseDate, Duration.ofHours(1)));
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional
    public Map<LocalTime, Integer> createPurchasesForDate(LocalDate baseDate, List<LocalDateTime> timestamps) {
        // create a list of not equal intervals to enforce non-linear results
        Map<AbstractMap.SimpleEntry<LocalTime, LocalTime>, Integer> intervalToNb = getIntervalToNb();
        Map<LocalTime, Integer> aggregMap = new HashMap<>();
        timestamps.forEach(t-> aggregMap.put(t.toLocalTime(), 0));
        for (var entry : intervalToNb.entrySet()) {
            var interval = entry.getKey();
            int nb = entry.getValue();
            Duration intervalDuration = Duration.between(interval.getKey(), interval.getValue());
            Duration purchaseStep = intervalDuration.dividedBy(nb);
            this.nbPurchases += createPurchases(nb, LocalDateTime.of(baseDate, interval.getKey()), purchaseStep, aggregMap);
        }
        return aggregMap;
    }

    @Transactional
    public int createPurchases(int nb, LocalDateTime baseDateTime, Duration purchaseStep, Map<LocalTime, Integer> aggregMap) {
        int count = 0;
        for (int i = 0; i < nb; i++) {
            LocalTime purchaseRelativeTime = LocalTime.ofSecondOfDay(purchaseStep.multipliedBy(i).getSeconds());
            LocalTime nextHour = baseDateTime.plusSeconds(purchaseRelativeTime.toSecondOfDay()).plusHours(1).truncatedTo(ChronoUnit.HOURS).toLocalTime();
            aggregMap.put(nextHour, aggregMap.get(nextHour) + 1);
            Payment payment = new Payment(40, baseDateTime.plusSeconds(purchaseRelativeTime.toSecondOfDay()));
            entityManager.persist(payment);

            Cart purchaseCart = new Cart();
            for (int i1 = 0; i1 < 3; i1++) {
                CartItem ci = new CartItem();
                entityManager.persist(ci);
                Item item1 = new Item("croissant " + i1, 10.0 + i1);
                entityManager.persist(item1);
                ci.setItem(item1);
                purchaseCart.addItem(ci);
            }

            Partner currentPartner = partner;
            purchaseCart.setPartner(currentPartner);
            entityManager.persist(purchaseCart);
            Purchase purchase = new Purchase(payment, purchaseCart);
            purchase.setAlreadyConsumedInAPerk(false);
            customer.addPurchase(purchase);
            entityManager.persist(purchase);
            count++;
        }
        return count;
    }

    @Transactional
    @Test
    void testAggregateOneDay() {
        Cart cart3 = new Cart();
        cart3.setPartner(partner);
        customer.setCart(cart3);
        entityManager.persist(cart3);
        Payment payment3 = new Payment(2, LocalDate.of(2025, 3, 15).atStartOfDay());
        entityManager.persist(payment3);
        Purchase purchase3 = new Purchase(payment3, cart3);
        entityManager.persist(purchase3);
        customer.addPurchase(purchase3);
        Map<LocalTime, Integer> aggregation = purchaseStats.aggregateByDayAndDuration(partner.getPartnerId(), LocalDate.of(2025, 3, 16), Duration.ofHours(1));
        assertEquals(aggregation.values().stream().reduce(Integer::sum).orElseThrow(), this.nbPurchases);
        assertEquals(aggregation, this.expectedDay1Aggregation);
    }

    @Transactional
    @Test
    void testAggregateTwoDays() {
        Map<LocalTime, Integer> aggregationDay1 = purchaseStats.aggregateByDayAndDuration(partner.getPartnerId(), LocalDate.of(2025, 3, 16), Duration.ofHours(1));
        assertEquals(aggregationDay1.values().stream().reduce(Integer::sum).orElseThrow(), this.nbPurchases);
        assertEquals(aggregationDay1, this.expectedDay1Aggregation);
    }


    @NotNull
    private static Map<AbstractMap.SimpleEntry<LocalTime, LocalTime>, Integer> getIntervalToNb() {
        List<AbstractMap.SimpleEntry<LocalTime, LocalTime>> intervals = new ArrayList<>(){{
            add(new AbstractMap.SimpleEntry<>(
                        LocalTime.of(0, 0),
                        LocalTime.of(6, 30)) {
                }
            );
            add(new AbstractMap.SimpleEntry<>(
                        LocalTime.of(6, 30),
                        LocalTime.of(8, 0)) {
                }
            );
            add(new AbstractMap.SimpleEntry<>(
                        LocalTime.of(8, 0),
                        LocalTime.of(10, 0)) {
                }
            );
            add(new AbstractMap.SimpleEntry<>(
                        LocalTime.of(10, 0),
                        LocalTime.of(13, 30)) {
                }
            );
            add(new AbstractMap.SimpleEntry<>(
                        LocalTime.of(13, 30),
                        LocalTime.of(15, 30)) {
                }
            );
            add(new AbstractMap.SimpleEntry<>(
                        LocalTime.of(15, 30),
                        LocalTime.of(16, 30)) {
                }
            );
            add(new AbstractMap.SimpleEntry<>(
                        LocalTime.of(16, 30),
                        LocalTime.of(17, 0)) {
                }
            );
            add(new AbstractMap.SimpleEntry<>(
                        LocalTime.of(17, 0),
                        LocalTime.of(17, 15)) {
                }
            );
            add(new AbstractMap.SimpleEntry<>(
                        LocalTime.of(17, 15),
                        LocalTime.of(18, 0)) {
                }
            );
            add(new AbstractMap.SimpleEntry<>(
                        LocalTime.of(18, 0),
                        LocalTime.of(19, 0)) {
                }
            );
        }};
        Map<AbstractMap.SimpleEntry<LocalTime, LocalTime>, Integer> intervalToNb = new HashMap<>();

        int total = 150;
        for (int i = 1; i < total; i++) {
            var interval = intervals.get((int)(intervals.size()*((float)i/(float)total))); // get by percentage
            intervalToNb.put(interval, intervalToNb.getOrDefault(interval, 0) + 1);
        }
        return intervalToNb;
    }
}
