package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.dto.HappyKidsDiscountDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.interfaces.IHappyKids;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PerkApplicationVisitorImplTest {

    @Mock
    private IHappyKids happyKidsProxy;
    private Customer customer;
    private Cart cart;
    private PerkApplicationVisitorImpl visitor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cart = mock(Cart.class);
        customer = mock(Customer.class);
        when(customer.getCart()).thenReturn(cart);
        visitor = new PerkApplicationVisitorImpl(happyKidsProxy);
    }

    @Test
    void testVisitVfpDiscountInPercentPerk_success() throws Exception {
        // Création d'un CartItem avec une heure de réservation dans l'intervalle
        CartItem cartItem = mock(CartItem.class);
        LocalTime bookingTime = LocalTime.now();
        when(cartItem.getStartTime()).thenReturn(LocalDateTime.now());
        when(cart.getHKItems()).thenReturn(List.of(cartItem));

        // Création d'un perk avec une plage horaire couvrant bookingTime
        LocalTime startHour = bookingTime.minusMinutes(10);
        LocalTime endHour = bookingTime.plusMinutes(10);
        double discountRate = 0.2; // 20%
        VfpDiscountInPercentPerk perk = new VfpDiscountInPercentPerk(discountRate, startHour, endHour);

        // Préparation d'un HappyKidsDiscountDTO fictif
        HappyKidsDiscountDTO discountDTO = mock(HappyKidsDiscountDTO.class);
        when(discountDTO.price()).thenReturn(8.0);
        when(happyKidsProxy.computeDiscount(cartItem, discountRate)).thenReturn(discountDTO);

        boolean result = visitor.visit(perk, customer);

        verify(cartItem).setPrice(8.0);
        assertTrue(result);
    }

    @Test
    void testVisitVfpDiscountInPercentPerk_bookingTimeNotInRange() throws Exception {
        CartItem cartItem = mock(CartItem.class);
        LocalTime bookingTime = LocalTime.now();
        when(cartItem.getStartTime()).thenReturn(LocalDateTime.now());
        when(cart.getHKItems()).thenReturn(List.of(cartItem));

        // Plage horaire ne contenant pas bookingTime
        LocalTime startHour = bookingTime.plusHours(1);
        LocalTime endHour = bookingTime.plusHours(2);
        double discountRate = 0.2;
        VfpDiscountInPercentPerk perk = new VfpDiscountInPercentPerk(discountRate, startHour, endHour);

        boolean result = visitor.visit(perk, customer);

        verify(happyKidsProxy, never()).computeDiscount(any(), anyDouble());
        verify(cartItem, never()).setPrice(anyDouble());
        assertTrue(result);
    }

    @Test
    void testVisitVfpDiscountInPercentPerk_bookingTimeNotSet() {
        CartItem cartItem = mock(CartItem.class);
        when(cartItem.getStartTime()).thenReturn(null);
        when(cart.getHKItems()).thenReturn(List.of(cartItem));

        LocalTime startHour = LocalTime.now().minusMinutes(10);
        LocalTime endHour = LocalTime.now().plusMinutes(10);
        double discountRate = 0.2;
        VfpDiscountInPercentPerk perk = new VfpDiscountInPercentPerk(discountRate, startHour, endHour);

        assertThrows(IllegalStateException.class, () -> visitor.visit(perk, customer));
    }

    @Test
    void testVisitTimedDiscountInPercentPerk_success() {
        // Créer un perk avec une heure passée pour déclencher la réduction
        LocalTime discountTime = LocalTime.now().minusMinutes(5);
        double discountRate = 15.0;
        TimedDiscountInPercentPerk perk = new TimedDiscountInPercentPerk(discountTime, discountRate);

        when(cart.addToTotalPercentageReduction(discountRate)).thenReturn(0.15);

        boolean result = visitor.visit(perk, customer);

        verify(cart).addToTotalPercentageReduction(discountRate);
        assertTrue(result);
    }

    @Test
    void testVisitTimedDiscountInPercentPerk_notApplicable() {
        // Créer un perk avec une heure future : la réduction ne doit pas être appliquée
        LocalTime discountTime = LocalTime.now().plusMinutes(5);
        double discountRate = 15.0;
        TimedDiscountInPercentPerk perk = new TimedDiscountInPercentPerk(discountTime, discountRate);

        boolean result = visitor.visit(perk, customer);

        verify(cart, never()).addToTotalPercentageReduction(anyDouble());
        assertFalse(result);
    }

    @Test
    void testVisitNPurchasedMGiftedPerk_success() {
        // Préparer un item et un CartItem correspondant
        Item item = Item.createTestItem(1, "TestItem", 10);
        CartItem cartItem = mock(CartItem.class);
        when(cartItem.getQuantity()).thenReturn(5);
        when(cart.getItemById(item.getItemId())).thenReturn(cartItem);

        // Créer un perk pour "Achetez 3, recevez 1 gratuit"
        NPurchasedMGiftedPerk perk = new NPurchasedMGiftedPerk(3, 1, item);

        boolean result = visitor.visit(perk, customer);

        verify(cartItem).addFreeItem(1);
        assertTrue(result);
    }

    @Test
    void testVisitNPurchasedMGiftedPerk_notEligible() {
        Item item = Item.createTestItem(1, "TestItem", 10);
        CartItem cartItem = mock(CartItem.class);
        when(cartItem.getQuantity()).thenReturn(2); // quantité insuffisante
        when(cart.getItemById(item.getItemId())).thenReturn(cartItem);

        NPurchasedMGiftedPerk perk = new NPurchasedMGiftedPerk(3, 1, item);

        boolean result = visitor.visit(perk, customer);

        verify(cartItem, never()).addFreeItem(anyInt());
        assertFalse(result);
    }
}

