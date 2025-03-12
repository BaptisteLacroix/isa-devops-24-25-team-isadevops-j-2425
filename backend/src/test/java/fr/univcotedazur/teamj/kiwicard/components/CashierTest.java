package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.connectors.BankProxy;
import fr.univcotedazur.teamj.kiwicard.connectors.externaldto.PaymentRequestDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CashierTest extends BaseUnitTest {

    @Mock
    private BankProxy bankProxy;

    @InjectMocks
    private Cashier cashier;

    @Mock
    private Cart cart;

    private Customer customer;
    private CartItem cartItem;
    private AbstractPerk perk;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up a mock cart item
        Item item = Item.createTestItem(1, "Item1", 10);
        cartItem = new CartItem(item, 1);

        // Set up a mock perk
        perk = mock(AbstractPerk.class);

        cart = mock(Cart.class);
        when(cart.getItemList()).thenReturn(Set.of(cartItem));
        when(cart.getPerksToUse()).thenReturn(Collections.singletonList(perk));

        // Set up a mock customer
        customer = mock(Customer.class);
        when(customer.getCart()).thenReturn(cart);
        when(customer.getCart().getItemList()).thenReturn(Set.of(cartItem));
        when(customer.getCart().getPerksToUse()).thenReturn(Collections.singletonList(perk));
        when(customer.getCardNumber()).thenReturn("1234567890");

        // Mock methods for cart calculations
        when(customer.getCart().getTotalPercentageReduction()).thenReturn(0.1);
        when(customer.getCart().addToTotalPercentageReduction(any(Double.class))).thenReturn(0.2);
    }

    @Test
    void makePaySuccess() throws UnreachableExternalServiceException, ClosedTimeException {
        // Mock the bank response
        PaymentDTO paymentDTO = mock(PaymentDTO.class);
        when(bankProxy.askPayment(any(PaymentRequestDTO.class))).thenReturn(paymentDTO);

        // Call the method to test
        PaymentDTO result = cashier.makePay(customer);

        // Verify interactions and the result
        verify(bankProxy).askPayment(any(PaymentRequestDTO.class));
        assertNotNull(result);
    }

    @Test
    void makePayUnreachableExternalServiceException() throws UnreachableExternalServiceException {
        // Mock the bank to throw an exception
        when(bankProxy.askPayment(any(PaymentRequestDTO.class)))
                .thenThrow(new UnreachableExternalServiceException());

        // Call the method and assert that the exception is thrown
        assertThrows(UnreachableExternalServiceException.class, () -> cashier.makePay(customer));
    }

    @Test
    void makePayWithZeroPercentageDiscount() throws UnreachableExternalServiceException, ClosedTimeException {
        // Set up customer with no discount
        when(customer.getCart().getTotalPercentageReduction()).thenReturn(0.0);

        // Mock the bank response
        PaymentDTO paymentDTO = mock(PaymentDTO.class);
        when(bankProxy.askPayment(any(PaymentRequestDTO.class))).thenReturn(paymentDTO);

        // Call the method
        PaymentDTO result = cashier.makePay(customer);

        // Verify the payment request with no discount applied
        verify(bankProxy).askPayment(any(PaymentRequestDTO.class));
        assertNotNull(result);
    }

    @Test
    void makePayWithMultiplePerksApplied() throws UnreachableExternalServiceException, ClosedTimeException {
        // Create a second perk and mock behavior
        AbstractPerk perk2 = mock(AbstractPerk.class);
        when(customer.getCart().getPerksToUse()).thenReturn(Arrays.asList(perk, perk2));

        // Mock the bank response
        PaymentDTO paymentDTO = mock(PaymentDTO.class);
        when(bankProxy.askPayment(any(PaymentRequestDTO.class))).thenReturn(paymentDTO);

        // Call the method
        PaymentDTO result = cashier.makePay(customer);

        // Verify perks were applied
        verify(perk).apply(any());
        verify(perk2).apply(any());
        verify(bankProxy).askPayment(any(PaymentRequestDTO.class));
        assertNotNull(result);
    }
}
