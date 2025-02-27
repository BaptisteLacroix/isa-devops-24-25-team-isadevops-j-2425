package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.entities.*;
import fr.univcotedazur.teamj.kiwicard.exceptions.*;
import fr.univcotedazur.teamj.kiwicard.repositories.ICustomerRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPartnerRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PurchaseCatalogTest extends BaseUnitTest {
    private final String customerEmail = "test@example.com";

    @Mock
    private IPurchaseRepository purchaseRepository;

    @Mock
    private ICustomerRepository customerRepository;

    @Mock
    private IPartnerRepository partnerRepository;

    @InjectMocks
    private PurchaseCatalog purchaseCatalog;

    @Mock
    private Customer customer;

    private List<Purchase> purchases = new ArrayList<>();
    private final int nbPurchase = 4;

    @Mock
    private Partner partner;

    @Mock
    private Partner partner2;

    private final long partnerId = 9;
    private final long partnerId2 = 10;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = mock(Customer.class);
        when(customerRepository.findByEmail(customerEmail)).thenReturn(Optional.of(customer));
        doAnswer(invocation -> {
            long id = invocation.getArgument(0);
            if(id == partnerId) return Optional.of(partner);
            if(id == partnerId2) return Optional.of(partner2);
            return Optional.empty();
        }).when(partnerRepository).findById(anyLong());

        for (int i = 0; i < nbPurchase; i++) {
            Purchase purchase = mock(Purchase.class);
            Payment payment = mock(Payment.class);
            when(payment.getTimestamp()).thenReturn(LocalDateTime.now());
            when(purchase.getPayment()).thenReturn(payment);
            when(purchase.isAlreadyConsumedInAPerk()).thenReturn(false);
            Partner p = i < nbPurchase - 1 ? partner : mock(Partner.class);
            Cart cart = mock(Cart.class);
            when(cart.getPartner()).thenReturn(p);
            when(purchase.getCart()).thenReturn(cart);
            purchases.add(purchase);
        }
        when(customer.getPurchases()).thenReturn(purchases);
        Cart cart = mock(Cart.class);
        when(customer.getCart()).thenReturn(cart);
        when(cart.getItems()).thenReturn(new HashSet<>());
    }

    @Test
    void consumeNLastPurchaseOfCustomer() throws UnknownCustomerEmailException, UnknownPartnerIdException {
        purchaseCatalog.consumeNLastPurchaseOfCustomer(nbPurchase -1, customerEmail);
        for (int i = 0; i < nbPurchase; i++) {
            if (i < nbPurchase - 1) {
                verify(purchases.get(i)).setAlreadyConsumedInAPerk(true);
                continue;
            }
            verify(purchases.get(i), never()).setAlreadyConsumedInAPerk(anyBoolean());
        }
    }

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

    @Test
    void consumeNLastItemsOfCustomerInPartner() throws UnknownCustomerEmailException, UnknownPartnerIdException {
        int totalItems = 9;
        int nbPurchases = 3;
        int itemsToConsume = 5;
        List<Purchase> purchases = new ArrayList<>();
        List<Cart> allItems = new ArrayList<>();

        List<CartItem> itemsConsumed = new ArrayList<>();


        for (int i = 0; i < nbPurchases; i++) {
            Purchase purchase = mock(Purchase.class);
            Payment payment = mock(Payment.class);
            when(payment.getTimestamp()).thenReturn(LocalDateTime.now().minusDays(i));
            when(purchase.getPayment()).thenReturn(payment);
            when(purchase.isAlreadyConsumedInAPerk()).thenReturn(false);

            Cart cart = mock(Cart.class);
            when(purchase.getCart()).thenReturn(cart);
            when(cart.getPartner()).thenReturn(i == 0 ? partner2 : partner); // partner 2  for the most recent, partner 1 for the rest
            List<CartItem> items = new ArrayList<>();
            when(cart.getItems()).thenReturn(new HashSet<>(items));
            allItems.add(cart);
            for (int j = 0; j < totalItems /nbPurchases; j++) {
                CartItem item = mock(CartItem.class);
                doAnswer(invocation -> {
                    itemsConsumed.add(item);
                    return null;
                }).when(item).setConsumed(anyBoolean());

                when(item.isConsumed()).thenReturn(false);
                items.add(item);
            }
            when(cart.getItems()).thenReturn(new HashSet<>(items));
            when(purchase.getCart()).thenReturn(cart);
            purchases.add(purchase);
        }

        when(customer.getPurchases()).thenReturn(purchases);

        purchaseCatalog.consumeNLastItemsOfCustomerInPartner(itemsToConsume, customerEmail, partnerId);

        for (Cart cart : allItems) {
            int count = 0;
            for (CartItem item : cart.getItems()) {
                if (itemsConsumed.contains(item)) {
                    count++;
                }
            }
            assertEquals(count, Math.min(cart.getPartner() == partner ? cart.getItems().size() : 0, itemsToConsume));
            itemsToConsume -= count;
        }
    }

    @Test
    void createPurchase() throws UnknownCustomerEmailException, UnknownPaymentIdException {

    }

    @Test
    void findPurchaseById() throws UnknownPurchaseIdException {

    }

    @Test
    void findPurchasesByCustomerAndPartner() throws UnknownCustomerEmailException, UnknownPartnerIdException {

    }
}

