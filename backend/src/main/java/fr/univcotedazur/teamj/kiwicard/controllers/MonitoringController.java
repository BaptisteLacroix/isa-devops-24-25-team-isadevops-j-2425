package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseHistoryDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.ForbiddenDurationException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPurchaseIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPerkManager;
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
    public final IPerkManager perkManager;

    @Autowired
    public MonitoringController(IPurchaseFinder purchaseFinder, IPurchaseStats statisticMaker, IPerkManager perkManager) {
        this.purchaseFinder = purchaseFinder;
        this.statisticMaker = statisticMaker;
        this.perkManager = perkManager;
    }

    @GetMapping("/purchase/{purchaseId}")
    public ResponseEntity<PurchaseHistoryDTO> getPurchase(@PathVariable long purchaseId) throws UnknownPartnerIdException, UnknownPurchaseIdException {
        return ResponseEntity.ok().body(purchaseFinder.findPurchaseById(purchaseId));
    }

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

    @GetMapping("/purchase")
    public ResponseEntity<List<PurchaseHistoryDTO>> getByCustomerAndPartner(@RequestParam String customerEmail, @RequestParam long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        return ResponseEntity.ok().body(purchaseFinder.findPurchasesByCustomerAndPartner(customerEmail, partnerId));
    }

    public record TwoDaysAggregation(Map<LocalTime, Integer> day1Aggregation, Map<LocalTime, Integer> day2Aggregation) {
    }

    @GetMapping("/stats/{partnerId}/compare-purchases")
    public ResponseEntity<TwoDaysAggregation> comparePurchases(@PathVariable long partnerId, @RequestParam LocalDate day1, @RequestParam LocalDate day2, @RequestParam Optional<Duration> duration) throws UnknownPartnerIdException, ForbiddenDurationException {
        Duration dur = duration.orElse(Duration.ofHours(1));
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
        return ResponseEntity.ok(this.perkManager.aggregatePartnerPerksUsageByType(partnerId));
    }
}
