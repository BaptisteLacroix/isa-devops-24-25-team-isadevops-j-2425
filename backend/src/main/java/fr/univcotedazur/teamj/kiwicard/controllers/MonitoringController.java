package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPurchaseIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    private final IPurchaseFinder purchaseCatalog;

    public MonitoringController(IPurchaseFinder purchaseCatalog) {
        this.purchaseCatalog = purchaseCatalog;
    }

    @GetMapping("/partnerHistory/{partnerId}")
    public List<Purchase> partnerHistory(@PathVariable long partnerId) throws UnknownPartnerIdException {
        return purchaseCatalog.findPurchaseByPartnerId(partnerId);
    }

    @GetMapping("purchase/{purchaseId}")
    public Purchase getPurchase(@PathVariable long purchaseId) throws UnknownPartnerIdException, UnknownPurchaseIdException {
        return purchaseCatalog.findPurchaseById(purchaseId);
    }

    @GetMapping("purchase")
    public List<Purchase> getByCustomerAndPartner(@RequestParam String customerEmail, @RequestParam long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        return purchaseCatalog.findPurchasesByCustomerAndPartner(customerEmail, partnerId);
    }
}
