package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPurchaseIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.monitoring.IStatisticsExplorator;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseStats;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {
    private final IPurchaseFinder purchaseFinder;
    private final IPurchaseStats statisticMaker;


    @Autowired
    public MonitoringController(IPurchaseFinder purchaseFinder, IPurchaseStats statisticMaker) {
        this.purchaseFinder = purchaseFinder;
        this.statisticMaker = statisticMaker;
    }

    @GetMapping("/purchase/{purchaseId}")
    public Purchase getPurchase(@PathVariable long purchaseId) throws UnknownPartnerIdException, UnknownPurchaseIdException {
        return purchaseFinder.findPurchaseById(purchaseId);
    }

    @GetMapping("/customer/{customerEmail}/history")
    public List<Purchase> customerHistory(@PathVariable String customerEmail, @RequestParam Optional<Integer> limit) throws UnknownPartnerIdException, UnknownCustomerEmailException {
        if (limit.isPresent()) return this.purchaseFinder.findPurchasesByCutomerEmail(customerEmail, limit.get());
        return this.purchaseFinder.findPurchasesByCutomerEmail(customerEmail);
    }

    @GetMapping("/partner/{partnerId}/history")
    public List<Purchase> partnerHistory(@PathVariable long partnerId, @RequestParam Optional<Integer> limit) throws UnknownPartnerIdException {
        if (limit.isPresent()) return this.purchaseFinder.findPurchasesByPartnerId(partnerId, limit.get());
        return purchaseFinder.findPurchasesByPartnerId(partnerId);
    }

    @GetMapping("/purchase")
    public List<Purchase> getByCustomerAndPartner(@RequestParam String customerEmail, @RequestParam long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        return purchaseFinder.findPurchasesByCustomerAndPartner(customerEmail, partnerId);
    }

    public record TwoDaysAggregation(Map<LocalTime, Integer> day1Aggregation, Map<LocalTime, Integer> day2Aggregation){}
    @GetMapping("/stats/{partnerId}/comparePurchases")
    public ResponseEntity<?> comparePurchases (@PathVariable long partnerId, @RequestParam LocalDate day1, @RequestParam LocalDate day2, @RequestParam Optional<Duration> duration) {
        Duration dur = duration.orElse(Duration.ofHours(1));
        if (dur.compareTo(Duration.ofDays(1))>0) return ResponseEntity.badRequest().body("Duration is expected to be less than a day, got" + dur);
        Map<LocalTime, Integer> day1Aggregation = this.statisticMaker.aggregateByDayAndDuration(partnerId, day1, dur);
        Map<LocalTime, Integer> day2Aggregation = this.statisticMaker.aggregateByDayAndDuration(partnerId, day2, dur);
        return ResponseEntity.ok(new TwoDaysAggregation(day1Aggregation, day2Aggregation));
    }
}
