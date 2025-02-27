package fr.univcotedazur.teamj.kiwicard;

import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.entities.Payment;
import fr.univcotedazur.teamj.kiwicard.entities.perks.VfpDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.repositories.ICustomerRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPartnerRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPerkRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPurchaseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Profile("!test")
@Component
public class DataInsertionUseCaseRunner implements CommandLineRunner {

    private final ICustomerRepository customerRepository;
    private final IPartnerRepository partnerRepository;
    private final IPerkRepository perkRepository;
    private final IPurchaseRepository purchaseRepository;
    private final IPartnerManager partnerManager;
    private final boolean deleteAllData = true;
    private long customerId;

    public DataInsertionUseCaseRunner(ICustomerRepository customerRepository, IPartnerRepository partnerRepository, IPerkRepository perkRepository, IPurchaseRepository purchaseRepository, IPartnerManager partnerManager) {
        this.customerRepository = customerRepository;
        this.partnerRepository = partnerRepository;
        this.perkRepository = perkRepository;
        this.purchaseRepository = purchaseRepository;
        this.partnerManager = partnerManager;
    }

    private void deleteAllData() {
        customerRepository.deleteAll();
        purchaseRepository.deleteAll();
        perkRepository.deleteAll();
        partnerRepository.deleteAll();
    }

    @Override
    @Transactional
    public void run(String... args) throws UnknownPartnerIdException {
        tryToInsert();
        tryToRetrieve();
    }

    private void tryToRetrieve() {
        Customer customer = customerRepository.findById(customerId).get();
        System.out.println("Customer name: " + customer.getFirstName());
        Cart cart = customer.getCart();
        System.out.println("Cart partner: " + cart.getPartner().getName());
        System.out.println("Cart items: ");
        for (CartItem item : cart.getItems()) {
            System.out.println("Item: " + item.getItem().getLabel() + " - Quantity: " + item.getQuantity());
        }
    }

    private void tryToInsert() throws UnknownPartnerIdException {
        if (deleteAllData) {
            this.deleteAllData();
        }
        // Customer
        Customer customer = new Customer(
                "Alice",
                "bob",
                "blabliblou",
                "alice.bob@gmail.com",
                true
        );
        customerRepository.save(customer);

        // Partner
        Partner partner = new Partner(
                "Boulange",
                "14 rue du trottoir, Draguignan"
        );

        partnerRepository.save(partner);
        // Cart with Partner and Customer
        Cart cart = new Cart();
        cart.setPartner(partner);
        customer.setCart(cart);
        customerRepository.save(customer);
        customerId = customer.getCustomerId();
        cart = customer.getCart();

        // Item
        ItemDTO itemDTO = new ItemDTO("croissant", 10.0);
        partnerManager.addItemToPartnerCatalog(partner.getPartnerId(), itemDTO);
        Item item = partnerManager.findAllPartnerItems(partner.getPartnerId()).getFirst();

        // CartItem with cart and item
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(2);
        cart.addItem(cartItem);
        customerRepository.save(customer);

        // Perk (Vfp discount in %)
        VfpDiscountInPercentPerk perk = new VfpDiscountInPercentPerk(0.05);
        perkRepository.save(perk);
        cart.addPerk(perk);

        // Payment
        Payment payment = new Payment(40, LocalDateTime.now());

//        // Purchase
//        Purchase purchase = new Purchase(payment, cart);
//        purchaseRepository.save(purchase);
//        customer.addPurchase(purchase);
//        customer.removeCart();
//        customerRepository.save(customer);
    }
}
