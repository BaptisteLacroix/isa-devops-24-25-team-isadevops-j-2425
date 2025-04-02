package fr.univcotedazur.teamj.kiwicard;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.entities.*;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import jakarta.persistence.EntityManager;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PurchaseCreationUtils {
    private EntityManager entityManager;
    private int totalNbPurchases;
    private Customer customer;

    public PurchaseCreationUtils(EntityManager entityManager, int totalNbPurchases) {
        this.entityManager = entityManager;
        this.totalNbPurchases = totalNbPurchases;
    }

    public Customer getCustomer() {
        return customer;
    }

    @Transactional
    public Map<LocalTime, Integer> createDummyPurchasesForDate(LocalDate baseDate, List<LocalDateTime> timestamps,
                                                               Partner partner) {
        return this.createDummyPurchasesForDate(
                baseDate,
                timestamps,
                partner,
                new HashMap<>()
        );
    }

    @Transactional
    public Map<LocalTime, Integer> createDummyPurchasesForDate(LocalDate baseDate, List<LocalDateTime> timestamps,
                                                               Partner partner, Map<AbstractPerk, Integer> perksToApply) {
        CustomerSubscribeDTO customerDTExample = new CustomerSubscribeDTO(
                "anyMail",
                "anyFirstname",
                "anySurname",
                "anyAddress"
        );
        this.customer = new Customer(customerDTExample, "1234567893");
        // create a list of not equal intervals to enforce non-linear results
        Map<AbstractMap.SimpleEntry<LocalTime, LocalTime>, Integer> intervalToNb = getIntervalToNb();
        Map<LocalTime, Integer> aggregMap = new LinkedHashMap<>();
        timestamps.forEach(t -> aggregMap.put(t.toLocalTime(), 0));
        perksToApply = new HashMap<>(perksToApply); // copy list to avoid modifying reference
        for (Map.Entry<AbstractMap.SimpleEntry<LocalTime, LocalTime>, Integer> entry : intervalToNb.entrySet()) {
            AbstractMap.SimpleEntry<LocalTime, LocalTime> interval = entry.getKey();
            int nb = entry.getValue();
            Duration intervalDuration = Duration.between(interval.getKey(), interval.getValue());
            Duration purchaseStep = intervalDuration.dividedBy(nb);
            createPurchases(nb, LocalDateTime.of(baseDate, interval.getKey()), purchaseStep, aggregMap, customer, partner, perksToApply);
        }
        return aggregMap;
    }

    @Transactional
    public void createPurchases(int nb, LocalDateTime baseDateTime, Duration purchaseStep, Map<LocalTime, Integer> aggregMap, Customer customer,
                                Partner partner, Map<AbstractPerk, Integer> perksToApply) {
        for (int i = 0; i < nb; i++) {
            LocalTime purchaseRelativeTime = LocalTime.ofSecondOfDay(purchaseStep.multipliedBy(i).getSeconds());
            LocalTime nextHour = baseDateTime.plusSeconds(purchaseRelativeTime.toSecondOfDay()).plusHours(1).truncatedTo(ChronoUnit.HOURS).toLocalTime();
            aggregMap.put(nextHour, aggregMap.get(nextHour) + 1);
            Payment payment = new Payment(40, baseDateTime.plusSeconds(purchaseRelativeTime.toSecondOfDay()));
            entityManager.persist(payment);

            Cart purchaseCart = new Cart();
            var nextPerk = getNextPerkToApply(perksToApply);

            if (nextPerk != null) {
                partner.addPerk(nextPerk);
                purchaseCart.addPerkUsed(nextPerk);
            }

            for (int i1 = 0; i1 < 3; i1++) {
                CartItem ci = new CartItem();
                entityManager.persist(ci);
                Item item1 = new Item("croissant " + i1, 10.0 + i1);
                entityManager.persist(item1);
                ci.setItem(item1);
                purchaseCart.addItem(ci);
            }

            purchaseCart.setPartner(partner);
            entityManager.persist(purchaseCart);
            Purchase purchase = new Purchase(payment, purchaseCart);
            purchase.setAlreadyConsumedInAPerk(false);
            customer.addPurchase(purchase);
            entityManager.persist(purchase);
        }
    }

    @Nullable
    private static AbstractPerk getNextPerkToApply(Map<AbstractPerk, Integer> perksToApply) {
        for (var entry: perksToApply.entrySet()) {
            if (entry.getValue() > 0) {
                perksToApply.put(entry.getKey(), entry.getValue() - 1);
                return entry.getKey();
            }
        }
        return null;
    }

    private Map<AbstractMap.SimpleEntry<LocalTime, LocalTime>, Integer> getIntervalToNb() {
        List<AbstractMap.SimpleEntry<LocalTime, LocalTime>> intervals = new ArrayList<>() {{
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

        for (int i = 1; i < totalNbPurchases; i++) {
            var interval = intervals.get((int) (intervals.size() * ((float) i / (float) totalNbPurchases))); // get by percentage
            intervalToNb.put(interval, intervalToNb.getOrDefault(interval, 0) + 1);
        }
        return intervalToNb;
    }

}
