package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TimedDiscountInPercentPerkTest {

    private TimedDiscountInPercentPerk perk;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // Création d'un mock de Cart et Customer
        Cart cart = mock(Cart.class);
        customer = mock(Customer.class);
        when(customer.getCart()).thenReturn(cart);
    }

    @Test
    void testIsConsumableForWhenTimePassed() {
        LocalTime discountTime = LocalTime.now().minusMinutes(5);
        double discountRate = 15.0;
        perk = new TimedDiscountInPercentPerk(discountTime, discountRate);

        assertTrue(perk.isConsumableFor(customer));
    }

    @Test
    void testIsConsumableForWhenTimeNotPassed() {
        LocalTime discountTime = LocalTime.now().plusMinutes(5);
        double discountRate = 15.0;
        perk = new TimedDiscountInPercentPerk(discountTime, discountRate);

        assertFalse(perk.isConsumableFor(customer));
    }
}
