package fr.univcotedazur.teamj.kiwicard.entities.perks;

import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NPurchasedMGiftedPerkTest {

    private NPurchasedMGiftedPerk perk;
    private Customer customer;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        Item item = Item.createTestItem(1, "TestItem", 10);
        perk = new NPurchasedMGiftedPerk(3, 1, item);

        cartItem = mock(CartItem.class);
        Cart cart = mock(Cart.class);
        when(cart.getItemById(item.getItemId())).thenReturn(cartItem);

        customer = mock(Customer.class);
        when(customer.getCart()).thenReturn(cart);
    }

    @Test
    void testIsConsumableForEligible() {
        // Vérification de isConsumableFor : quantité suffisante
        when(cartItem.getQuantity()).thenReturn(3);
        assertTrue(perk.isConsumableFor(customer));
    }

    @Test
    void testIsConsumableForNotEligible() {
        // Vérification de isConsumableFor : quantité insuffisante
        when(cartItem.getQuantity()).thenReturn(2);
        assertFalse(perk.isConsumableFor(customer));
    }
}
