package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.dto.PurchaseHistoryDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.ForbiddenDurationException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPurchaseIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {
    public final IPurchaseFinder purchaseFinder;
    public final IPurchaseStats statisticMaker;
    public final IPerksFinder perkFinder;

    @Autowired
    public MonitoringController(IPurchaseFinder purchaseFinder, IPurchaseStats statisticMaker, IPerksFinder perksFinder) {
        this.purchaseFinder = purchaseFinder;
        this.statisticMaker = statisticMaker;
        this.perkFinder = perksFinder;
    }

    /**
     * Found a purchase by its id
     *
     * @param purchaseId purchase id
     * @return purchase
     * @throws UnknownPartnerIdException if partner id is unknown
     * @throws UnknownPurchaseIdException if purchase id is unknown
     */
    @GetMapping("/purchase/{purchaseId}")
    public ResponseEntity<PurchaseHistoryDTO> getPurchase(@PathVariable long purchaseId) throws UnknownPartnerIdException, UnknownPurchaseIdException {
        return ResponseEntity.ok().body(purchaseFinder.findPurchaseById(purchaseId));
    }

    /**
     * Found all purchases of a customer
     *
     * @param customerEmail customer email
     * @param limit         limit of purchases
     * @return list of purchases
     * @throws UnknownCustomerEmailException if customer email is unknown
     */
    @GetMapping("/customer/{customerEmail}/history")
    public ResponseEntity<List<PurchaseHistoryDTO>> customerHistory(@PathVariable String customerEmail, @RequestParam Optional<Integer> limit) throws UnknownCustomerEmailException {
        List<PurchaseHistoryDTO> purchases;
        if (limit.isPresent()) {
            purchases = this.purchaseFinder.findPurchasesByCustomerEmail(customerEmail, limit.get());
        } else {
            purchases = this.purchaseFinder.findPurchasesByCustomerEmail(customerEmail);
        }
        return ResponseEntity.ok().body(purchases);
    }

    /**
     * Found all purchases of a partner
     *
     * @param partnerId partner id
     * @param limit     limit of purchases
     * @return list of purchases
     * @throws UnknownPartnerIdException if partner id is unknown
     */
    @GetMapping("/partner/{partnerId}/history")
    public ResponseEntity<List<PurchaseHistoryDTO>> partnerHistory(@PathVariable long partnerId, @RequestParam Optional<Integer> limit) throws UnknownPartnerIdException {
        List<PurchaseHistoryDTO> purchases;
        if (limit.isPresent()) {
            purchases = this.purchaseFinder.findPurchasesByPartnerId(partnerId, limit.get());
        } else {
            purchases = purchaseFinder.findPurchasesByPartnerId(partnerId);
        }
        return ResponseEntity.ok().body(purchases);
    }

    /**
     * Found all purchases of a customer for a partner
     *
     * @param customerEmail customer email
     * @param partnerId     partner id
     * @return list of purchases
     * @throws UnknownCustomerEmailException if customer email is unknown
     * @throws UnknownPartnerIdException     if partner id is unknown
     */
    @GetMapping("/purchase")
    public ResponseEntity<List<PurchaseHistoryDTO>> getByCustomerAndPartner(@RequestParam String customerEmail, @RequestParam long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        return ResponseEntity.ok().body(purchaseFinder.findPurchasesByCustomerAndPartner(customerEmail, partnerId));
    }

    public record TwoDaysAggregation(Map<LocalTime, Integer> day1Aggregation, Map<LocalTime, Integer> day2Aggregation) {
    }

    @GetMapping("/stats/{partnerId}/comparePurchases")
    public ResponseEntity<TwoDaysAggregation> comparePurchases(@PathVariable long partnerId, @RequestParam LocalDate day1, @RequestParam LocalDate day2, @RequestParam Optional<Integer> duration) throws UnknownPartnerIdException, ForbiddenDurationException {
        Duration dur = Duration.ofHours(duration.orElse(1));
        if (dur.compareTo(Duration.ofDays(1)) > 0)
            throw new ForbiddenDurationException(dur);
        Map<LocalTime, Integer> day1Aggregation = this.statisticMaker.aggregatePurchasesByDayAndDuration(partnerId, day1, dur);
        Map<LocalTime, Integer> day2Aggregation = this.statisticMaker.aggregatePurchasesByDayAndDuration(partnerId, day2, dur);
        return ResponseEntity.ok(new TwoDaysAggregation(day1Aggregation, day2Aggregation));
    }

    @GetMapping("/stats/{partnerId}/nb-perks-by-type")
    public ResponseEntity<Map<String, Long>> aggregatePartnerPerksUsageByType(
            @PathVariable long partnerId
    ) throws UnknownPartnerIdException {
        Map<String, Long> map = this.perkFinder.aggregatePartnerPerksUsageByType(partnerId);
        return ResponseEntity.ok(map);
    }
}
