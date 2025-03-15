package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPaymentIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPurchaseIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseCreator;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseFinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {
    private final IPurchaseFinder purchaseFinder;

    public MonitoringController(IPurchaseFinder purchaseFinder) {
        this.purchaseFinder = purchaseFinder;
    }

    @GetMapping("/purchase/{purchaseId}")
    public Purchase getPurchase(@PathVariable long purchaseId) throws UnknownPartnerIdException, UnknownPurchaseIdException {
        return purchaseFinder.findPurchaseById(purchaseId);
    }

    @GetMapping("/customerHistory/{customerEmail}")
    public List<Purchase> customerHistory(@PathVariable String customerEmail, @RequestParam Optional<Integer> limit) throws UnknownPartnerIdException, UnknownCustomerEmailException {
        if (limit.isPresent()) return this.purchaseFinder.findPurchasesByCutomerEmail(customerEmail, limit.get());
        return this.purchaseFinder.findPurchasesByCutomerEmail(customerEmail);
    }

    @GetMapping("/partnerHistory/{partnerId}")
    public List<Purchase> partnerHistory(@PathVariable long partnerId, @RequestParam Optional<Integer> limit) throws UnknownPartnerIdException {
        if (limit.isPresent()) return this.purchaseFinder.findPurchasesByPartnerId(partnerId, limit.get());
        return purchaseFinder.findPurchasesByPartnerId(partnerId);
    }

    @GetMapping("/purchase")
    public List<Purchase> getByCustomerAndPartner(@RequestParam String customerEmail, @RequestParam long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        return purchaseFinder.findPurchasesByCustomerAndPartner(customerEmail, partnerId);
    }
}
