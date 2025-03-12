package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.components.PurchaseCatalog;
import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    private PurchaseCatalog purchaseCatalog;

    @Autowired
    public MonitoringController(PurchaseCatalog purchaseCatalog) {
        this.purchaseCatalog = purchaseCatalog;
    }

    @GetMapping("/partnerHistory/{partnerId}}")
    public List<Purchase> partnerHistory(@PathVariable long partnerId) throws UnknownPartnerIdException {
        return purchaseCatalog.findPurchaseByPartnerId(partnerId);
    }
}
