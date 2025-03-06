package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.entities.*;
import fr.univcotedazur.teamj.kiwicard.exceptions.*;
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
    @Autowired
    public PurchaseCatalog(IPurchaseRepository purchaseRepository, CustomerCatalog customerCatalog) {
        this.purchaseRepository = purchaseRepository;
        this.customerCatalog = customerCatalog;
    }

    @Override
    public void consumeNLastPurchaseOfCustomer(CustomerDTO customer, int nbPurchasesToConsume) {
        this.purchaseRepository.findPurchasesToConsume(customer.email(), nbPurchasesToConsume).forEach(p->p.setAlreadyConsumedInAPerk(true));
    }

    @Override
    @Transactional
    public void consumeNLastPurchaseOfCustomerInPartner(CustomerDTO customer, PartnerDTO partner, int nbPurchasesToConsume) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        var res  = this.purchaseRepository.findPurchasesToConsume(customer.email(), partner.id(), nbPurchasesToConsume);
        res.forEach(p -> p.setAlreadyConsumedInAPerk(true));
    }


    @Override
    public void consumeNLastItemsOfCustomerInPartner(int nbItemsConsumed, String customerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
//        Customer customer =  this.customerRepository.findByEmail(customerEmail).orElseThrow(UnknownCustomerEmailException::new);
//        Partner partner = this.partnerRepository.findById(partnerId).orElseThrow(()-> new UnknownPartnerIdException(partnerId));
//        customer.getPurchases().stream()
//                .filter(p-> !p.isAlreadyConsumedInAPerk() && p.getCart().getPartner().equals(partner))
//                .sorted((e1, e2) -> e2.getPayment().getTimestamp().compareTo(e1.getPayment().getTimestamp()))
//                .map(p -> p.getCart().getItems())
//                .flatMap(Collection::stream)
//                .limit(nbItemsConsumed)
//                .forEach((i)->i.setConsumed(true));
    }

    @Transactional
    @Override
    public PurchaseDTO createPurchase(String customerEmail, Long amount) throws UnknownCustomerEmailException {
        Customer customer = this.customerCatalog.findCustomerByEmail(customerEmail);
        Cart cart = customer.getCart();
        Payment payment = new Payment(amount, LocalDateTime.now());
        var purchase = new Purchase(
                payment,
                cart
        );
        this.purchaseRepository.save(purchase);
        return new PurchaseDTO(purchase);

    }

    @Override
    public PurchaseDTO findPurchaseById(long purchaseId) throws UnknownPurchaseIdException {
        Optional<Purchase> res;
        if ((res = this.purchaseRepository.findById(purchaseId)).isPresent()) {
            return new PurchaseDTO(res.orElseThrow());
        }
        throw new UnknownPurchaseIdException(purchaseId);
    }

    @Override
    public List<PurchaseDTO> findPurchasesByCustomerAndPartner(String customerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
//        Optional<Partner> partner;
//        Optional<Customer> customer;
//
//        if ((partner = this.partnerRepository.findById(partnerId)).isEmpty()) throw new UnknownPartnerIdException(partnerId);
//        if ((customer = this.customerRepository.findByEmail(customerEmail)).isEmpty())
//            throw new UnknownCustomerEmailException();
//
//        Partner p = partner.orElseThrow();
//        Customer c = customer.orElseThrow();
//
//        List<Purchase> result = new ArrayList<>(c.getPurchases());
//        result.retainAll(p.getPurchaseList());
//        return result.stream().map(PurchaseDTO::new).collect(Collectors.toList());
        return null;
    }
}

