package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.entities.*;
import fr.univcotedazur.teamj.kiwicard.exceptions.*;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseConsumer;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseCreator;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseFinder;
import fr.univcotedazur.teamj.kiwicard.repositories.IPurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class PurchaseCatalog implements IPurchaseConsumer, IPurchaseCreator, IPurchaseFinder {
    private final IPurchaseRepository purchaseRepository;
    private final CustomerCatalog customerCatalog;
    private final IPartnerManager partnerManager;

    @Autowired
    public PurchaseCatalog(IPurchaseRepository purchaseRepository, CustomerCatalog customerCatalog, IPartnerManager partnerManager) {
        this.purchaseRepository = purchaseRepository;
        this.customerCatalog = customerCatalog;
        this.partnerManager = partnerManager;
    }

    @Override
    @Transactional
    public void consumeNLastPurchaseOfCustomer(CustomerDTO customer, int nbPurchasesToConsume) {
        this.purchaseRepository.findAllByCustomer(customer.email(), nbPurchasesToConsume)
                .forEach(p -> p.setAlreadyConsumedInAPerk(true));
    }

    @Override
    @Transactional
    public void consumeNLastPurchaseOfCustomerInPartner(String customerEmail, Long partnerId, int nbPurchasesToConsume) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        this.customerCatalog.findCustomerByEmail(customerEmail);
        this.partnerManager.findPartnerById(partnerId);
        var res = this.purchaseRepository.findAllByCustomerAndPartner(customerEmail, partnerId, nbPurchasesToConsume);
        res.forEach(p -> p.setAlreadyConsumedInAPerk(true));
    }


    @Transactional
    @Override
    public void consumeNLastItemsOfCustomerInPartner(int nbItemsConsumed, String customerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        this.purchaseRepository.findAllByCustomerAndPartner(customerEmail, partnerId).stream()
                .map(p -> p.getCart().getItems())
                .flatMap(Collection::stream)
                .limit(nbItemsConsumed)
                .forEach(i -> i.setConsumed(true));
    }

    @Transactional
    @Override
    public Purchase createPurchase(String customerEmail, Long amount) throws UnknownCustomerEmailException {
        Customer customer = this.customerCatalog.findCustomerByEmail(customerEmail);
        Cart cart = customer.getCart();
        Payment payment = new Payment(amount, LocalDateTime.now());
        var purchase = new Purchase(
                payment,
                cart
        );
        this.purchaseRepository.save(purchase);
        return purchase;
    }

    @Override
    public Purchase findPurchaseById(long purchaseId) throws UnknownPurchaseIdException {
        return this.purchaseRepository.findById(purchaseId).orElseThrow(() -> new UnknownPurchaseIdException(purchaseId));
    }

    @Override
    public List<Purchase> findPurchasesByCustomerAndPartner(String customerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        this.customerCatalog.findCustomerByEmail(customerEmail);
        this.partnerManager.findPartnerById(partnerId);
        return this.purchaseRepository.findAllByCustomerAndPartner(customerEmail, partnerId);
    }

    @Override
    public List<Purchase> findPurchasesByPartnerId(long partnerId) throws UnknownPartnerIdException {
        this.partnerManager.findPartnerById(partnerId);
        return this.purchaseRepository.findAllByPartner(partnerId);
    }

    @Override
    public List<Purchase> findPurchasesByCutomerEmail(String customerEmail) throws UnknownCustomerEmailException {
        return this.purchaseRepository.findAllByCustomer(customerEmail);
    }

    @Override
    public List<Purchase> findPurchasesByCutomerEmail(String customerEmail, int limit) throws UnknownCustomerEmailException {
        this.customerCatalog.findCustomerByEmail(customerEmail);
        return this.purchaseRepository.findAllByCustomer(customerEmail, limit);
    }

    @Override
    public List<Purchase> findPurchasesByPartnerId(long partnerId, int limit) throws UnknownPartnerIdException {
        this.partnerManager.findPartnerById(partnerId);
        return this.purchaseRepository.findAllByPartner(partnerId, limit);
    }
}

