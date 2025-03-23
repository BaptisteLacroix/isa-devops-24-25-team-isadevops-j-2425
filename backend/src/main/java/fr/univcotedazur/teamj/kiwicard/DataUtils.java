package fr.univcotedazur.teamj.kiwicard;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.entities.*;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DataUtils {
    private EntityManager entityManager;

    public DataUtils(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public Map<LocalTime, Integer> createDummyPurchasesForDate(LocalDate baseDate, List<LocalDateTime> timestamps,
                                                               Partner partner) {
        CustomerSubscribeDTO customerDTExample = new CustomerSubscribeDTO(
                "anyMail",
                "anyFirstname",
                "anySurname",
                "anyAddress"
        );
        var customer = new Customer(customerDTExample, "1234567893");
        // create a list of not equal intervals to enforce non-linear results
        Map<AbstractMap.SimpleEntry<LocalTime, LocalTime>, Integer> intervalToNb = getIntervalToNb();
        Map<LocalTime, Integer> aggregMap = new LinkedHashMap<>();
        timestamps.forEach(t -> aggregMap.put(t.toLocalTime(), 0));
        for (var entry : intervalToNb.entrySet()) {
            var interval = entry.getKey();
            int nb = entry.getValue();
            Duration intervalDuration = Duration.between(interval.getKey(), interval.getValue());
            Duration purchaseStep = intervalDuration.dividedBy(nb);
            createPurchases(nb, LocalDateTime.of(baseDate, interval.getKey()), purchaseStep, aggregMap, customer, partner);
        }
        return aggregMap;
    }

    @Transactional
    public int createPurchases(int nb, LocalDateTime baseDateTime, Duration purchaseStep, Map<LocalTime, Integer> aggregMap, Customer customer,
                               Partner partner) {
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

            purchaseCart.setPartner(partner);
            entityManager.persist(purchaseCart);
            Purchase purchase = new Purchase(payment, purchaseCart);
            purchase.setAlreadyConsumedInAPerk(false);
            customer.addPurchase(purchase);
            entityManager.persist(purchase);
            count++;
        }
        return count;
    }

    private static Map<AbstractMap.SimpleEntry<LocalTime, LocalTime>, Integer> getIntervalToNb() {
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

        int total = 150;
        for (int i = 1; i < total; i++) {
            var interval = intervals.get((int) (intervals.size() * ((float) i / (float) total))); // get by percentage
            intervalToNb.put(interval, intervalToNb.getOrDefault(interval, 0) + 1);
        }
        return intervalToNb;
    }

}
