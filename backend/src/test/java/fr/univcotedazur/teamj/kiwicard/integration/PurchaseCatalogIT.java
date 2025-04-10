package fr.univcotedazur.teamj.kiwicard.integration;

import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.components.CustomerCatalog;
import fr.univcotedazur.teamj.kiwicard.components.PartnerCatalog;
import fr.univcotedazur.teamj.kiwicard.components.PurchaseCatalog;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.entities.Payment;
import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPurchaseIdException;
import fr.univcotedazur.teamj.kiwicard.repositories.IPurchaseRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PurchaseCatalogIT extends BaseUnitTest {

    @Autowired
    private IPurchaseRepository purchaseRepository;

    @Autowired
    private CustomerCatalog customerCatalog;

    @Autowired
    private EntityManager entityManager;

    private Customer customer;

    private Partner partner;

    private Partner partner2;

    private PurchaseCatalog purchaseCatalog;
    private Customer customer2;
    private List<Purchase> allGoodPurchases;
    @Autowired
    private PartnerCatalog partnerCatalog;

    @BeforeEach
    void setUp() {
        entityManager.clear();
        assertNotNull(purchaseRepository);
        purchaseCatalog = new PurchaseCatalog(purchaseRepository, customerCatalog, partnerCatalog, entityManager);
        allGoodPurchases = new ArrayList<>();

        customer = new Customer(new CustomerSubscribeDTO("test@example.com", "Alice", "Bob", "14 rue du trottoir, Draguignan"), "51");
        customer2 = new Customer(new CustomerSubscribeDTO("test2@example.com", "Alic2e", "Bob2", "14 rue du trottoir, Draguignan2"), "52");

        entityManager.persist(customer);
        entityManager.persist(customer2);

        partner = new Partner("Boulange", "14 rue du trottoir, Draguignan");
        entityManager.persist(partner);

        partner2 = new Partner("Café du coin", "5 place centrale, Nice");
        entityManager.persist(partner2);

        Cart cart = new Cart();
        cart.setPartner(partner);
        customer.setCart(cart);
        entityManager.persist(cart);
        entityManager.persist(customer);

        Item item = new Item("croissant", 10.0);
        entityManager.persist(item);

        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(2);
        cart.addItem(cartItem);
        entityManager.persist(cartItem);


        int nbGoodPurchase = 4;
        for (int i = 0; i < nbGoodPurchase; i++) {
            Payment payment = new Payment(40, LocalDateTime.now().minusDays(i));
            entityManager.persist(payment);

            Cart purchaseCart = new Cart();
            for (int i1 = 0; i1 < 3; i1++) {
                CartItem ci = new CartItem();
                entityManager.persist(ci);
                Item item1 = new Item("croissant " + i1, 10.0 + i1);
                entityManager.persist(item1);
                ci.setItem(item1);
                purchaseCart.addItem(ci);
            }

            Partner currentPartner = partner;

            purchaseCart.setPartner(currentPartner);
            entityManager.persist(purchaseCart);

            Purchase purchase = new Purchase(payment, purchaseCart);
            purchase.setAlreadyConsumedInAPerk(false);
            customer.addPurchase(purchase);
            entityManager.persist(purchase);
            allGoodPurchases.add(purchase);
        }

        entityManager.flush();
        entityManager.clear();
    }


    @Test
    @Transactional
    void consumeNLastPurchaseOfCustomerWithBadPurchase() {
        // good partner, bad customer
        Cart cart3 = new Cart();
        cart3.setPartner(partner);
        customer2.setCart(cart3);
        entityManager.persist(cart3);
        Payment payment3 = new Payment(2, LocalDateTime.now());
        entityManager.persist(payment3);
        Purchase purchase3 = new Purchase(payment3, cart3);
        entityManager.persist(purchase3);
        purchase3.setAlreadyConsumedInAPerk(false);
        customer2.addPurchase(purchase3);

        purchaseCatalog.consumeNLastPurchaseOfCustomer(new CustomerDTO(customer), allGoodPurchases.size() + 1);
        allGoodPurchases.stream().map(this::refreshPurchase).forEach(purchase -> assertTrue(purchase.isAlreadyConsumedInAPerk()));
        assertFalse(this.refreshPurchase(purchase3).isAlreadyConsumedInAPerk());
    }

    @Test
    @Transactional
    void consumeNLastPurchaseOfCustomerWithSmallerN() {
        purchaseCatalog.consumeNLastPurchaseOfCustomer(new CustomerDTO(customer), allGoodPurchases.size() - 1);
        for (int i = 0; i < allGoodPurchases.size() - 1; i++) {
            assertTrue(refreshPurchase(this.allGoodPurchases.get(i)).isAlreadyConsumedInAPerk());
        }
        assertFalse(refreshPurchase(this.allGoodPurchases.getLast()).isAlreadyConsumedInAPerk());
    }


    @Transactional
    @Test
    void consumeNLastPurchaseOfCustomerInPartner() throws UnknownCustomerEmailException, UnknownPartnerIdException {
        // persist the bad cases :
        // good customer, bad partner
        Cart cart2 = new Cart();
        cart2.setPartner(partner2);
        customer.setCart(cart2);
        entityManager.persist(cart2);
        Payment payment2 = new Payment(2, LocalDateTime.now());
        entityManager.persist(payment2);
        Purchase purchase2 = new Purchase(payment2, cart2);
        entityManager.persist(purchase2);
        purchase2.setAlreadyConsumedInAPerk(false);
        customer.addPurchase(purchase2);

        // good partner, bad customer
        Cart cart3 = new Cart();
        cart3.setPartner(partner);
        customer2.setCart(cart3);
        entityManager.persist(cart3);
        Payment payment3 = new Payment(2, LocalDateTime.now());
        entityManager.persist(payment3);
        Purchase purchase3 = new Purchase(payment3, cart3);
        entityManager.persist(purchase3);
        purchase3.setAlreadyConsumedInAPerk(false);
        customer2.addPurchase(purchase3);

        purchaseCatalog.consumeNLastPurchaseOfCustomerInPartner(customer.getEmail(), partner.getPartnerId(), 100);

        // these two purchases should not be consumed
        Stream.of(purchase2, purchase3)
                .map(this::refreshPurchase)
                .forEach(p -> assertFalse(p.isAlreadyConsumedInAPerk()));

        // The other ones set up in the before each should be consumed
        this.allGoodPurchases
                .stream()
                .map(this::refreshPurchase)
                .forEach(p -> assertTrue(p.isAlreadyConsumedInAPerk()));
    }

    @Transactional
    @Test
    void testCreatePurchase() {
        assertTrue(this.purchaseRepository.findById(this.purchaseCatalog.createPurchase(customer, 5L).getPurchaseId()).isPresent());
    }

    @Transactional
    @Test
    void testFindById() {
        assertDoesNotThrow(() -> this.purchaseCatalog.findPurchaseById(allGoodPurchases.getFirst().getPurchaseId()));
        assertThrows(UnknownPurchaseIdException.class, () -> this.purchaseCatalog.findPurchaseById(54511));
    }

    @Transactional
    @Test
    void consumeNLastItemsOfCustomerInPartner() {
        int itemsToConsume = allGoodPurchases.stream().map(p -> p.getCart().getItems().size()).reduce(Integer::sum).orElseThrow() - 1;
        purchaseCatalog.consumeNLastItemsOfCustomerInPartner(itemsToConsume, customer.getEmail(), partner.getPartnerId());

        for (Purchase purchase : allGoodPurchases.stream().map(this::refreshPurchase).toList()) {
            int nbConsumed = (int) purchase.getCart().getItems().stream().filter(CartItem::isConsumed).count();
            assertEquals(nbConsumed, Math.min(purchase.getCart().getItems().size(), itemsToConsume));
            itemsToConsume -= nbConsumed;
        }
    }

    private Purchase refreshPurchase(Purchase p) {
        return purchaseRepository.findById(p.getPurchaseId()).orElseThrow();
    }
}

