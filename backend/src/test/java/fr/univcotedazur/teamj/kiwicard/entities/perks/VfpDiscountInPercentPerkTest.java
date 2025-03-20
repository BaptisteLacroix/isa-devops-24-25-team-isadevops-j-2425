package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VfpDiscountInPercentPerkTest {

    private VfpDiscountInPercentPerk perk;
    private Customer customer;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        cart = mock(Cart.class);
        customer = mock(Customer.class);
        when(customer.getCart()).thenReturn(cart);
    }

    @Test
    void testIsConsumableForWhenBookingInRange() {
        // Le client est VFP et son panier contient un HKItem avec une heure de réservation dans la plage du perk
        when(customer.isVfp()).thenReturn(true);
        cartItem = mock(CartItem.class);
        when(cartItem.getStartTime()).thenReturn(LocalDateTime.of(2025, 1, 1, 11, 0));
        when(cart.getHKItems()).thenReturn(List.of(cartItem));

        LocalTime startHour = LocalTime.of(10, 0);
        LocalTime endHour = LocalTime.of(12, 0);
        perk = new VfpDiscountInPercentPerk(0.2, startHour, endHour);

        boolean result = perk.isConsumableFor(customer);
        assertTrue(result);
    }

    @Test
    void testIsConsumableForWhenBookingNotInRange() {
        when(customer.isVfp()).thenReturn(true);
        cartItem = mock(CartItem.class);
        LocalTime bookingTime = LocalTime.now();
        when(cartItem.getStartTime()).thenReturn(LocalDateTime.now());
        when(cart.getHKItems()).thenReturn(List.of(cartItem));

        // Plage horaire ne contenant pas l'heure de réservation
        LocalTime startHour = bookingTime.plusHours(1);
        LocalTime endHour = bookingTime.plusHours(2);
        perk = new VfpDiscountInPercentPerk(0.2, startHour, endHour);

        boolean result = perk.isConsumableFor(customer);
        assertFalse(result);
    }

    @Test
    void testIsConsumableForWhenCartIsNull() {
        when(customer.getCart()).thenReturn(null);
        LocalTime now = LocalTime.now();
        perk = new VfpDiscountInPercentPerk(0.2, now.minusMinutes(10), now.plusMinutes(10));

        boolean result = perk.isConsumableFor(customer);
        assertFalse(result);
    }

    @Test
    void testIsConsumableForWhenCartItemStartTimeNull() {
        when(customer.isVfp()).thenReturn(true);
        cartItem = mock(CartItem.class);
        when(cartItem.getStartTime()).thenReturn(null);
        when(cart.getHKItems()).thenReturn(List.of(cartItem));

        LocalTime now = LocalTime.now();
        perk = new VfpDiscountInPercentPerk(0.2, now.minusMinutes(10), now.plusMinutes(10));

        boolean result = perk.isConsumableFor(customer);
        assertFalse(result);
    }
}
