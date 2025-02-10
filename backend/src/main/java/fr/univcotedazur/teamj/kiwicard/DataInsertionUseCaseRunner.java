package fr.univcotedazur.teamj.kiwicard;

import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInsertionUseCaseRunner implements CommandLineRunner {

    private final ICustomerRepository customerRepository;
    private final IPartnerRepository partnerRepository;
    private final ICartRepository cartRepository;

    private final boolean deleteAllData = true;

    public DataInsertionUseCaseRunner(ICustomerRepository customerRepository, IPartnerRepository partnerRepository, ICartRepository cartRepository) {
        this.customerRepository = customerRepository;
        this.partnerRepository = partnerRepository;
        this.cartRepository = cartRepository;
    }

    private void deleteAllData() {
        cartRepository.deleteAll();
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
                false
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
        cartRepository.save(cart);

        // Item
        Item item = new Item();
        item.setLabel("ItemLabel");
        item.setPrice(10.0);

        // CartItem with cart and item
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);

        partnerRepository.save(partner);
        customerRepository.save(customer);
    }
}
