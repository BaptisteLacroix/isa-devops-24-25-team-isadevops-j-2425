package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseHistoryDTO;
import fr.univcotedazur.teamj.kiwicard.entities.*;
import fr.univcotedazur.teamj.kiwicard.exceptions.*;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseConsumer;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseCreator;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseFinder;
import fr.univcotedazur.teamj.kiwicard.repositories.IPurchaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Component
public class PurchaseCatalog implements IPurchaseConsumer, IPurchaseCreator, IPurchaseFinder {
    private final IPurchaseRepository purchaseRepository;
    private final CustomerCatalog customerCatalog;
    private final IPartnerManager partnerManager;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public PurchaseCatalog(IPurchaseRepository purchaseRepository, CustomerCatalog customerCatalog, IPartnerManager partnerManager, EntityManager entityManager) {
        this.purchaseRepository = purchaseRepository;
        this.customerCatalog = customerCatalog;
        this.partnerManager = partnerManager;
        this.entityManager = entityManager;
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


    @Override
    @Transactional
    public void consumeNLastItemsOfCustomerInPartner(int nbItemsConsumed, String customerEmail, long partnerId) {
        this.purchaseRepository.findAllByCustomerAndPartner(customerEmail, partnerId).stream()
                .map(p -> p.getCart().getItems())
                .flatMap(Collection::stream)
                .limit(nbItemsConsumed)
                .forEach(i -> i.setConsumed(true));
    }

    @Override
    @Transactional
    public Purchase createPurchase(Customer customer, double amount) {
        Cart cart = customer.getCart();
        cart = entityManager.merge(cart);
        Payment payment = new Payment(amount, LocalDateTime.now());
        Purchase purchase = new Purchase(
                payment,
                cart
        );
        this.purchaseRepository.save(purchase);
        customer.addPurchase(purchase);
        return purchase;
    }

    @Override
    public PurchaseHistoryDTO findPurchaseById(long purchaseId) throws UnknownPurchaseIdException {
        return this.purchaseRepository.findById(purchaseId).map(PurchaseHistoryDTO::new).orElseThrow(()->new UnknownPurchaseIdException(purchaseId));
    }

    @Override
    public List<PurchaseHistoryDTO> findPurchasesByCustomerAndPartner(String customerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        this.customerCatalog.findCustomerByEmail(customerEmail);
        this.partnerManager.findPartnerById(partnerId);
        return this.purchaseRepository.findAllByCustomerAndPartner(customerEmail, partnerId).stream().map(PurchaseHistoryDTO::new).toList();
    }

    @Override
    public List<PurchaseHistoryDTO> findPurchasesByPartnerId(long partnerId) throws UnknownPartnerIdException {
        this.partnerManager.findPartnerById(partnerId);
        return this.purchaseRepository.findAllByPartner(partnerId).stream().map(PurchaseHistoryDTO::new).toList();
    }

    @Override
    @Transactional
    public List<PurchaseHistoryDTO> findPurchasesByCustomerEmail(String customerEmail) {
        return this.purchaseRepository.findAllByCustomer(customerEmail).stream().map(PurchaseHistoryDTO::new).toList();
    }

    @Override
    @Transactional
    public List<PurchaseHistoryDTO> findPurchasesByCustomerEmail(String customerEmail, int limit) throws UnknownCustomerEmailException {
        this.customerCatalog.findCustomerByEmail(customerEmail);
        return this.purchaseRepository.findAllByCustomer(customerEmail, limit).stream()
                .map(PurchaseHistoryDTO::new)
                .toList();
    }

    @Override
    public List<PurchaseHistoryDTO> findPurchasesByPartnerId(long partnerId, int limit) throws UnknownPartnerIdException {
        this.partnerManager.findPartnerById(partnerId);
        return this.purchaseRepository.findAllByPartner(partnerId, limit).stream().map(PurchaseHistoryDTO::new).toList();
    }
}

