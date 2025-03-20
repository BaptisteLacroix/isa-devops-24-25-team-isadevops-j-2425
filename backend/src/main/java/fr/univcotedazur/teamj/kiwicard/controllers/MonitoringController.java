package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.dto.PurchaseHistoryDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPurchaseIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseFinder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        }else{
            purchases = purchaseFinder.findPurchasesByPartnerId(partnerId);
        }
        return ResponseEntity.ok().body(purchases);
    }

    @GetMapping("/purchase")
    public ResponseEntity<List<PurchaseHistoryDTO>> getByCustomerAndPartner(@RequestParam String customerEmail, @RequestParam long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        return ResponseEntity.ok().body(purchaseFinder.findPurchasesByCustomerAndPartner(customerEmail, partnerId));
    }
}
