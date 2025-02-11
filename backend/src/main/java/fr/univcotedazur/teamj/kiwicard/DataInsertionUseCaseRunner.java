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
    private final ICartRepository cartRepository;
    private final IPerkRepository perkRepository;
    private final IPurchaseRepository purchaseRepository;


    private final boolean deleteAllData = true;

    public DataInsertionUseCaseRunner(ICustomerRepository customerRepository, IPartnerRepository partnerRepository, ICartRepository cartRepository, IPerkRepository perkRepository, IPurchaseRepository purchaseRepository) {
        this.customerRepository = customerRepository;
        this.partnerRepository = partnerRepository;
        this.cartRepository = cartRepository;
        this.perkRepository = perkRepository;
        this.purchaseRepository = purchaseRepository;
    }

    private void deleteAllData() {
        purchaseRepository.deleteAll();
        cartRepository.deleteAll();
        perkRepository.deleteAll();
        partnerRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Override
    public void run(String... args) {
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
        cartRepository.save(cart);
        customer.setCart(cart);

        // Item
        Item item = new Item();
        item.setLabel("ItemLabel");
        item.setPrice(10.0);

        // CartItem with cart and item
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);

        // Perk (Vfp discount in %)
        VfpDiscountInPercentPerk perk = new VfpDiscountInPercentPerk(LocalDateTime.now(), 5);
        perkRepository.save(perk);
        cart.addPerk(perk);

        // Payment
        Payment payment = new Payment(40, LocalDateTime.now());

        // Purchase
        Purchase purchase = new Purchase(payment, cart);
        purchaseRepository.save(purchase);
        customer.addPurchase(purchase);
    }
}
