package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.entities.*;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCartIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPaymentIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseConsumer;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseCreator;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseFinder;
import fr.univcotedazur.teamj.kiwicard.repositories.ICustomerRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPartnerRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPurchaseRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class PurchaseCatalog implements IPurchaseConsumer, IPurchaseCreator, IPurchaseFinder {
    IPurchaseRepository purchaseRepository;
    ICustomerRepository customerRepository;
    IPartnerRepository partnerRepository;

    public PurchaseCatalog(IPurchaseRepository purchaseRepository, ICustomerRepository customerRepository, IPartnerRepository partnerRepository) {
        this.purchaseRepository = purchaseRepository;
        this.customerRepository = customerRepository;
        this.partnerRepository = partnerRepository;
    }

    @Override
    public void consumeNLastPurchaseoOfCustomer(int nbPurchasesToConsume, String customerEmail) throws UnknownCustomerEmailException {
        Optional<Customer> customer;
        if ((customer = this.customerRepository.findByEmail(customerEmail)).isEmpty())
            throw new UnknownCustomerEmailException();
        customer.orElseThrow().getPurchases().stream()
                .filter(Purchase::isAlreadyConsumedInAPerk)
                .sorted(Comparator.comparing(e -> e.getPayment().getTimestamp()))
                .limit(nbPurchasesToConsume)
                .forEach(p -> p.setAlreadyConsumedInAPerk(true));
    }

    @Override
    public void consumeNLastPurchaseOfCustomerInPartner(int nbPurchasesToConsume, String customerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        Optional<Customer> customer;
        Optional<Partner> partner;
        if ((partner = this.partnerRepository.findById(partnerId)).isEmpty()) throw new UnknownPartnerIdException();
        if ((customer = this.customerRepository.findByEmail(customerEmail)).isEmpty())
            throw new UnknownCustomerEmailException();

        customer.orElseThrow().getPurchases().stream()
                .filter(Purchase::isAlreadyConsumedInAPerk)
                .filter(p -> p.getCart().getPartner().equals(partner.orElseThrow()))
                .sorted(Comparator.comparing(e -> e.getPayment().getTimestamp()))
                .limit(nbPurchasesToConsume)
                .forEach(p -> p.setAlreadyConsumedInAPerk(true));
    }


    @Override
    public void consumeNLastItemsOfCustomerInPartner(long itemId, int nbItemsConsumed, String customerEmail) throws UnknownCustomerEmailException {
        Optional<Customer> customer;
        if ((customer = this.customerRepository.findByEmail(customerEmail)).isEmpty())
            throw new UnknownCustomerEmailException();
        customer.orElseThrow().getPurchases().stream()
                .filter(Purchase::isAlreadyConsumedInAPerk)
                .sorted((e1, e2) -> e2.getPayment().getTimestamp().compareTo(e1.getPayment().getTimestamp()))
                .map(p -> p.getCart().getItemList())
                .flatMap(Collection::stream)
                .limit(nbItemsConsumed)
                .forEach((i)-> i.setConsumed(true));
    }

    @Override
    public PurchaseDTO createPurchase(String customerEmail, PaymentDTO paymentDTO) throws UnknownCustomerEmailException, UnknownPaymentIdException {
        Optional<Customer> customer;
        if ((customer = this.customerRepository.findByEmail(customerEmail)).isEmpty())
            throw new UnknownCustomerEmailException();
        Cart cart = customer.orElseThrow().getCart();
        Payment payment = new Payment(paymentDTO.getAmount(), LocalDateTime.now());
        return new PurchaseDTO(new Purchase(
                payment,
                cart
        ));
    }

    @Override
    public PurchaseDTO findPurchaseById(long purchaseId) throws UnknownPartnerIdException {
        Optional<Purchase> res;
        if ((res = this.purchaseRepository.findById(purchaseId)).isPresent()) {
            return new PurchaseDTO(res.orElseThrow());
        }
        throw new UnknownPartnerIdException();
    }

    @Override
    public List<PurchaseDTO> findPurchasesByCustomerAndPartner(String customerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException {
        Optional<Partner> partner;
        Optional<Customer> customer;

        if ((partner = this.partnerRepository.findById(partnerId)).isEmpty()) throw new UnknownPartnerIdException();
        if ((customer = this.customerRepository.findByEmail(customerEmail)).isEmpty())
            throw new UnknownCustomerEmailException();

        Partner p = partner.orElseThrow();
        Customer c = customer.orElseThrow();

        List<Purchase> result = new ArrayList<>(c.getPurchases());
        result.retainAll(p.getPurchases());
        return result.stream().map(PurchaseDTO::new).collect(Collectors.toList());
    }
}

