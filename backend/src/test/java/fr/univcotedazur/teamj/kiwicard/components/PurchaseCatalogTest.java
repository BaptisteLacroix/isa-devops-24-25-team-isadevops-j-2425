package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.entities.*;
import fr.univcotedazur.teamj.kiwicard.exceptions.*;
import fr.univcotedazur.teamj.kiwicard.repositories.ICustomerRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPartnerRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPurchaseRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
class PurchaseCatalogTest extends BaseUnitTest {
    private final String customerEmail = "test@example.com";

    @Autowired
    private IPurchaseRepository purchaseRepository;

    @Autowired
    private TestEntityManager entityManager;
//
//    @Mock
//    private ICustomerRepository customerRepository;
//
//    @Mock
//    private IPartnerRepository partnerRepository;
//
//    @InjectMocks
//    private PurchaseCatalog purchaseCatalog;

    private Customer customer;

//    private List<Purchase> purchases = new ArrayList<>();
    private final int nbPurchase = 4;

//    @Mock
    private Partner partner;

//    @Mock
    private Partner partner2;

    private final long partnerId = 9;
    private final long partnerId2 = 10;
    private PurchaseCatalog purchaseCatalog;


    @BeforeEach
    public void setUp() {
        // On s'assure que nbPurchase est un entier positif (ici, par exemple, 5)
        int nbPurchase = 5;

        // Création et persistance d'un client réel avec tous les champs requis
        customer = new Customer(
                "Alice",                      // firstName
                "Bob",                        // surname
                "14 rue du trottoir, Draguignan", // address
                customerEmail,                // email
                true                          // actif (par exemple)
        );
        entityManager.persist(customer);

        // Création et persistance des partenaires
        partner = new Partner("Boulange", "14 rue du trottoir, Draguignan");
        entityManager.persist(partner);

        partner2 = new Partner("Café du coin", "5 place centrale, Nice");
        entityManager.persist(partner2);

        // Création d'un panier principal et association avec le client et un partenaire
        Cart cart = new Cart();
        cart.setPartner(partner);
        customer.setCart(cart);
        entityManager.persist(cart);
        // On met à jour le client si besoin (cela dépend de la gestion de la relation dans Customer)
        entityManager.persist(customer);

        // Insertion d'un item dans le catalogue du partenaire
        Item item = new Item("croissant", 10.0);
        entityManager.persist(item);

        // Création d'un item de panier et ajout dans le panier
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(2);
        cart.addItem(cartItem);
        entityManager.persist(cartItem);

        // Création de nbPurchase achats
        for (int i = 0; i < nbPurchase; i++) {
            // Création et persistance d'un paiement avec une date variant selon i
            Payment payment = new Payment(40, LocalDateTime.now().minusDays(i));
            entityManager.persist(payment);

            // Pour chaque achat, création d'un panier spécifique et affectation d'un partenaire
            Cart purchaseCart = new Cart();
            Partner currentPartner = (i < nbPurchase - 1) ? partner : partner2;
            purchaseCart.setPartner(currentPartner);
            entityManager.persist(purchaseCart);

            // Création d'un achat associé au paiement et au panier de l'achat
            Purchase purchase = new Purchase(payment, purchaseCart);
            purchase.setAlreadyConsumedInAPerk(false);
            // Etablissement de la relation bidirectionnelle (si Purchase possède une référence vers Customer)
            customer.addPurchase(purchase);

            entityManager.persist(purchase);
        }

        // On flush pour forcer l'écriture en base, et clear pour simuler un nouveau contexte
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void temp() {
        assertNotEquals(0, purchaseRepository.findAll().size());
    }
    //        List<Purchase> purchases = purchaseRepository.findPurchasesToConsume(
//                customerEmail, partnerId, nbPurchase
//        );
    //    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        customer = mock(Customer.class);
//        when(customerRepository.findByEmail(customerEmail)).thenReturn(Optional.of(customer));
//        doAnswer(invocation -> {
//            long id = invocation.getArgument(0);
//            if(id == partnerId) return Optional.of(partner);
//            if(id == partnerId2) return Optional.of(partner2);
//            return Optional.empty();
//        }).when(partnerRepository).findById(anyLong());
//
//        for (int i = 0; i < nbPurchase; i++) {
//            Purchase purchase = mock(Purchase.class);
//            Payment payment = mock(Payment.class);
//            when(payment.getTimestamp()).thenReturn(LocalDateTime.now());
//            when(purchase.getPayment()).thenReturn(payment);
//            when(purchase.isAlreadyConsumedInAPerk()).thenReturn(false);
//            Partner p = i < nbPurchase - 1 ? partner : mock(Partner.class);
//            Cart cart = mock(Cart.class);
//            when(cart.getPartner()).thenReturn(p);
//            when(purchase.getCart()).thenReturn(cart);
//            purchases.add(purchase);
//        }
//        when(customer.getPurchases()).thenReturn(purchases);
//        Cart cart = mock(Cart.class);
//        when(customer.getCart()).thenReturn(cart);
//        when(cart.getItems()).thenReturn(new HashSet<>());
//    }
//    @Test
//    void consumeNLastPurchaseOfCustomer() throws UnknownCustomerEmailException, UnknownPartnerIdException {
//        purchaseCatalog.consumeNLastPurchaseOfCustomer(nbPurchase -1, customerEmail);
//        for (int i = 0; i < nbPurchase; i++) {
//            if (i < nbPurchase - 1) {
//                verify(purchases.get(i)).setAlreadyConsumedInAPerk(true);
//                continue;
//            }
//            verify(purchases.get(i), never()).setAlreadyConsumedInAPerk(anyBoolean());
//        }
//    }
//
    @Test
    void consumeNLastPurchaseOfCustomerInPartner() throws UnknownCustomerEmailException, UnknownPartnerIdException {
        purchaseCatalog.consumeNLastPurchaseOfCustomerInPartner(nbPurchase, customerEmail, partnerId);
        for (int i = 0; i < nbPurchase; i++) {
            if (i < nbPurchase - 1) {
                verify(purchases.get(i)).setAlreadyConsumedInAPerk(true);
                continue;
            }
            verify(purchases.get(i), never()).setAlreadyConsumedInAPerk(anyBoolean());
        }
    }
//
//    @Test
//    void consumeNLastItemsOfCustomerInPartner() throws UnknownCustomerEmailException, UnknownPartnerIdException {
//        int totalItems = 9;
//        int nbPurchases = 3;
//        int itemsToConsume = 5;
//        List<Purchase> purchases = new ArrayList<>();
//        List<Cart> allItems = new ArrayList<>();
//
//        List<CartItem> itemsConsumed = new ArrayList<>();
//
//
//        for (int i = 0; i < nbPurchases; i++) {
//            Purchase purchase = mock(Purchase.class);
//            Payment payment = mock(Payment.class);
//            when(payment.getTimestamp()).thenReturn(LocalDateTime.now().minusDays(i));
//            when(purchase.getPayment()).thenReturn(payment);
//            when(purchase.isAlreadyConsumedInAPerk()).thenReturn(false);
//
//            Cart cart = mock(Cart.class);
//            when(purchase.getCart()).thenReturn(cart);
//            when(cart.getPartner()).thenReturn(i == 0 ? partner2 : partner); // partner 2  for the most recent, partner 1 for the rest
//            List<CartItem> items = new ArrayList<>();
//            when(cart.getItems()).thenReturn(new HashSet<>(items));
//            allItems.add(cart);
//            for (int j = 0; j < totalItems /nbPurchases; j++) {
//                CartItem item = mock(CartItem.class);
//                doAnswer(invocation -> {
//                    itemsConsumed.add(item);
//                    return null;
//                }).when(item).setConsumed(anyBoolean());
//
//                when(item.isConsumed()).thenReturn(false);
//                items.add(item);
//            }
//            when(cart.getItems()).thenReturn(new HashSet<>(items));
//            when(purchase.getCart()).thenReturn(cart);
//            purchases.add(purchase);
//        }
//
//        when(customer.getPurchases()).thenReturn(purchases);
//
//        purchaseCatalog.consumeNLastItemsOfCustomerInPartner(itemsToConsume, customerEmail, partnerId);
//
//        for (Cart cart : allItems) {
//            int count = 0;
//            for (CartItem item : cart.getItems()) {
//                if (itemsConsumed.contains(item)) {
//                    count++;
//                }
//            }
//            assertEquals(count, Math.min(cart.getPartner() == partner ? cart.getItems().size() : 0, itemsToConsume));
//            itemsToConsume -= count;
//        }
//    }
//
//    @Test
//    void createPurchase() throws UnknownCustomerEmailException, UnknownPaymentIdException {
//
//    }
//
//    @Test
//    void findPurchaseById() throws UnknownPurchaseIdException {
//
//    }
//
//    @Test
//    void findPurchasesByCustomerAndPartner() throws UnknownCustomerEmailException, UnknownPartnerIdException {
//
//    }
}

