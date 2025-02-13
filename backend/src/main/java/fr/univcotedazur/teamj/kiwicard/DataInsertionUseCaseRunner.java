package fr.univcotedazur.teamj.kiwicard;

import fr.univcotedazur.teamj.kiwicard.entities.*;
import fr.univcotedazur.teamj.kiwicard.entities.perks.VfpDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInsertionUseCaseRunner implements CommandLineRunner {

    private final ICustomerRepository customerRepository;
    private final IPartnerRepository partnerRepository;
    private final IPerkRepository perkRepository;
    private final IPurchaseRepository purchaseRepository;


    private final boolean deleteAllData = true;
    private long customerId;

    public DataInsertionUseCaseRunner(ICustomerRepository customerRepository, IPartnerRepository partnerRepository, IPerkRepository perkRepository, IPurchaseRepository purchaseRepository) {
        this.customerRepository = customerRepository;
        this.partnerRepository = partnerRepository;
        this.perkRepository = perkRepository;
        this.purchaseRepository = purchaseRepository;
    }

    private void deleteAllData() {
        customerRepository.deleteAll();
        purchaseRepository.deleteAll();
        perkRepository.deleteAll();
        partnerRepository.deleteAll();
    }

    @Override
    public void run(String... args) {
        tryToInsert();
        tryToRetrieve();
    }

    private void tryToRetrieve() {
        Customer customer = customerRepository.findById(customerId).get();
        System.out.println("Customer name: " + customer.getFirstName());
        Cart cart = customer.getCart();
        System.out.println("Cart partner: " + cart.getPartner().getName());
        System.out.println("Cart items: ");
        for (CartItem item : cart.getItemList()) {
            System.out.println("Item: " + item.getItem().getLabel() + " - Quantity: " + item.getQuantity());
        }
    }

    private void tryToInsert() {
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
                "Antoine",
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
        Item item = new Item();
        item.setLabel("croissant");
        item.setPrice(10.0);

        // CartItem with cart and item
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(2);
        cart.addItem(cartItem);
        customerRepository.save(customer);

        // Perk (Vfp discount in %)
        VfpDiscountInPercentPerk perk = new VfpDiscountInPercentPerk(LocalDateTime.now(), 5);
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
