package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.connectors.BankProxy;
import fr.univcotedazur.teamj.kiwicard.connectors.HappyKidsProxy;
import fr.univcotedazur.teamj.kiwicard.connectors.externaldto.PaymentRequestDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemAddDTO;
import fr.univcotedazur.teamj.kiwicard.dto.HappyKidsDiscountDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentResponseDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.perks.NPurchasedMGiftedPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.TimedDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.VfpDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.BookingTimeNotSetException;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static fr.univcotedazur.teamj.kiwicard.configurations.Constants.HAPPY_KIDS_ITEM_NAME;
import static fr.univcotedazur.teamj.kiwicard.entities.Item.createTestItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CashierTest extends BaseUnitTest {

    @MockitoBean
    private HappyKidsProxy happyKidsProxy;

    @InjectMocks
    private Cashier cashier;

    @Mock
    private BankProxy bankProxy = mock(BankProxy.class);

    @Mock
    private Customer customer = mock(Customer.class);

    @BeforeEach
    void setUp() {
        cashier = new Cashier(bankProxy, happyKidsProxy);
    }

    @Test
    void testMakePay_WhenPaymentIsSuccessful_ShouldReturnAuthorizedPayment() throws UnreachableExternalServiceException, ClosedTimeException, BookingTimeNotSetException {
        // Arrange
        Item item1 = new Item("Item 1", 50.0);
        Item item2 = new Item("Item 2", 30.0);
        CartItem cartItem1 = new CartItem(item1, new CartItemAddDTO(2, null, 1)); // 2 x Item 1 = 100.0
        CartItem cartItem2 = new CartItem(item2, new CartItemAddDTO(1, null, 2)); // 1 x Item 2 = 30.0
        Cart cart = new Cart();
        cart.addItem(cartItem1);
        cart.addItem(cartItem2);
        cart.addToTotalPercentageReduction(0.1); // 10% discount

        when(customer.getCart()).thenReturn(cart);
        when(bankProxy.askPayment(Mockito.any(PaymentRequestDTO.class))).thenReturn(new PaymentDTO("1234567890123456", 117.0, true));

        // Act
        PaymentDTO paymentDTO = cashier.makePay(customer);

        // Assert
        verify(bankProxy, times(1)).askPayment(any());
        assertNotNull(paymentDTO);
        assertEquals(117.0, paymentDTO.amount()); // (100 + 30) - 10% = 117
        assertTrue(paymentDTO.authorized());
    }

    @Test
    void testMakePay_WhenExternalServiceIsUnreachable_ShouldThrowException() throws UnreachableExternalServiceException {
        // Arrange
        Item item1 = new Item("Item 1", 50.0);
        Item item2 = new Item("Item 2", 30.0);
        CartItem cartItem1 = new CartItem(item1, new CartItemAddDTO(2, null, 2)); // 2 x Item 1 = 100.0
        CartItem cartItem2 = new CartItem(item2, new CartItemAddDTO(1, null, 2)); // 1 x Item 2 = 30.0
        Cart cart = new Cart();
        cart.addItem(cartItem1);
        cart.addItem(cartItem2);
        cart.addToTotalPercentageReduction(0.1); // 10% discount

        when(customer.getCart()).thenReturn(cart);
        when(bankProxy.askPayment(any())).thenThrow(UnreachableExternalServiceException.class);

        // Act & Assert
        assertThrows(UnreachableExternalServiceException.class, () -> cashier.makePay(customer));
    }

    @Test
    void testComputePrice_WhenCartHasReduction_ShouldReturnReducedPrice() throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        // Arrange
        Item item1 = new Item("Item 1", 200.0);
        Item item2 = new Item("Item 2", 150.0);
        CartItem cartItem1 = new CartItem(item1, new CartItemAddDTO(1, null, 2)); // 1 x Item 1 = 200.0
        CartItem cartItem2 = new CartItem(item2, new CartItemAddDTO(1, null, 2)); // 1 x Item 2 = 150.0
        Cart cart = new Cart();
        cart.addItem(cartItem1);
        cart.addItem(cartItem2);
        cart.addToTotalPercentageReduction(0.2); // 20% discount

        when(customer.getCart()).thenReturn(cart);

        // Act
        PaymentResponseDTO response = cashier.computePrice(customer);

        // Assert
        assertNotNull(response);
        assertEquals(280.0, response.totalPrice());  // (200 + 150) - 20% = 280
    }

    @Test
    void testComputePrice_WhenCartHasNoReduction_ShouldReturnFullPrice() throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        // Arrange
        Item item1 = new Item("Item 1", 100.0);
        Item item2 = new Item("Item 2", 50.0);
        CartItem cartItem1 = new CartItem(item1, new CartItemAddDTO(1, null, 2)); // 1 x Item 1 = 100.0
        CartItem cartItem2 = new CartItem(item2, new CartItemAddDTO(1, null, 2)); // 1 x Item 2 = 50.0
        Cart cart = new Cart();
        cart.addItem(cartItem1);
        cart.addItem(cartItem2);
        cart.addToTotalPercentageReduction(0.0); // No discount

        when(customer.getCart()).thenReturn(cart);

        // Act
        PaymentResponseDTO response = cashier.computePrice(customer);

        // Assert
        assertNotNull(response);
        assertEquals(150.0, response.totalPrice());  // (100 + 50) - 0% = 150
    }

    @Test
    void testComputePrice_WhenCartIsEmpty_ShouldReturnZeroPrice() throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        // Arrange
        Cart cart = new Cart();
        cart.addToTotalPercentageReduction(0.0); // No discount

        when(customer.getCart()).thenReturn(cart);

        // Act
        PaymentResponseDTO response = cashier.computePrice(customer);

        // Assert
        assertNotNull(response);
        assertEquals(0.0, response.totalPrice());  // No items, no discount
    }

    @Test
    void testComputePrice_WhenNPurchasedMGiftedPerkIsApplied_ShouldReturnCorrectPriceAndQuantity() throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        // Arrange
        Item item = createTestItem(1, "Item 1", 100.0);
        CartItem cartItem = new CartItem(item, new CartItemAddDTO(3, null, item.getItemId())); // 3 x Item 1 = 300.0
        Cart cart = new Cart();
        cart.addItem(cartItem);

        NPurchasedMGiftedPerk perk = new NPurchasedMGiftedPerk(3, 1, item); // Buy 3, get 1 free
        cart.addPerkToUse(perk);

        when(customer.getCart()).thenReturn(cart);

        // Act
        PaymentResponseDTO response = cashier.computePrice(customer);

        // Assert
        assertNotNull(response);
        assertEquals(300.0, response.totalPrice());  // 3 x 100.0 = 300.0 (1 free item added)
        assertEquals(4, cartItem.getQuantity()); // 3 purchased + 1 gifted
    }

    @Test
    void testComputePrice_WhenNPurchasedMGiftedPerkIsNotEligible_ShouldReturnCorrectPriceAndQuantity() throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        // Arrange
        Item item = createTestItem(1, "Item 1", 100.0);
        CartItem cartItem = new CartItem(item, new CartItemAddDTO(2, null, item.getItemId())); // 2 x Item 1 = 200.0
        Cart cart = new Cart();
        cart.addItem(cartItem);

        NPurchasedMGiftedPerk perk = new NPurchasedMGiftedPerk(3, 1, item); // Buy 3 get 1 free
        cart.addPerkToUse(perk);

        when(customer.getCart()).thenReturn(cart);

        // Act
        PaymentResponseDTO response = cashier.computePrice(customer);

        // Assert
        assertNotNull(response);
        assertEquals(200.0, response.totalPrice());  // 2 x 100.0 = 200.0 (not eligible for perk)
        assertEquals(2, cartItem.getQuantity()); // 2 purchased, no gifted item
    }

    @Test
    void testComputePrice_WhenMultiplePerksAreApplied_ShouldReturnCorrectPriceAndQuantity() throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        // Arrange
        Item item1 = createTestItem(1, "Item 1", 100.0);
        Item item2 = createTestItem(2, "Item 2", 50.0);
        CartItem cartItem1 = new CartItem(item1, new CartItemAddDTO(3, null, item1.getItemId())); // 3 x Item 1 = 300.0
        CartItem cartItem2 = new CartItem(item2, new CartItemAddDTO(2, null, item2.getItemId())); // 2 x Item 2 = 100.0
        Cart cart = new Cart();
        cart.addItem(cartItem1);
        cart.addItem(cartItem2);

        NPurchasedMGiftedPerk nPurchasedMGiftedPerk = new NPurchasedMGiftedPerk(3, 1, item1); // Buy 3, get 1 free
        TimedDiscountInPercentPerk timedDiscountInPercentPerk = new TimedDiscountInPercentPerk(LocalTime.now().minusMinutes(15), 0.2); // 20% discount after 12:00

        cart.addPerkToUse(nPurchasedMGiftedPerk);
        cart.addPerkToUse(timedDiscountInPercentPerk);

        when(customer.getCart()).thenReturn(cart);
        when(customer.isVfp()).thenReturn(true);

        // Act
        PaymentResponseDTO response = cashier.computePrice(customer);

        // Assert
        assertNotNull(response);
        assertEquals(320.0, response.totalPrice());  // (300 + 100) - 20% = 320
        assertEquals(4, cartItem1.getQuantity()); // 3 purchased + 1 gifted
    }

    @Test
    void testComputePrice_WhenVFPDiscountInPercentPerkIsApplied_ShouldReturnDiscountedPrice() throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        // Arrange
        Item item1 = createTestItem(1, HAPPY_KIDS_ITEM_NAME, 100.0);
        CartItem cartItem1 = new CartItem(item1, new CartItemAddDTO(3, LocalDateTime.now().plusHours(1), item1.getItemId())); // 3 x Item 1 = 300.0
        Cart cart = new Cart();
        cart.addItem(cartItem1);

        // Simulating a VFP discount (10% discount for VFP members)
        VfpDiscountInPercentPerk vfpDiscountInPercentPerk = new VfpDiscountInPercentPerk(0.1, LocalTime.now().minusHours(1), LocalTime.now().plusHours(10));
        cart.addPerkToUse(vfpDiscountInPercentPerk);

        // Set up customer and HappyKidsProxy mock behavior
        when(customer.getCart()).thenReturn(cart);
        when(customer.isVfp()).thenReturn(true);
        when(happyKidsProxy.computeDiscount(anyDouble(), anyDouble())).thenReturn(new HappyKidsDiscountDTO(270.0));

        // Act
        PaymentResponseDTO response = cashier.computePrice(customer);

        // Assert
        assertNotNull(response);
        assertEquals(270.0, response.totalPrice());  // (300) - 10% = 270.0 after VFP discount
    }

    @Test
    void testComputePrice_WhenVFPDiscountPerkNotEligibleDueToTime_ShouldReturnFullPrice() throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        // Arrange
        Item item1 = createTestItem(1, HAPPY_KIDS_ITEM_NAME, 100.0);
        CartItem cartItem1 = new CartItem(item1, new CartItemAddDTO(3, LocalDateTime.now().minusHours(5), item1.getItemId())); // 3 x Item 1 = 300.0
        Cart cart = new Cart();
        cart.addItem(cartItem1);

        // Simulating a VFP discount (10% discount for VFP members)
        VfpDiscountInPercentPerk vfpDiscountInPercentPerk = new VfpDiscountInPercentPerk(0.1, LocalTime.now().plusHours(1), LocalTime.now().plusHours(10));
        cart.addPerkToUse(vfpDiscountInPercentPerk);

        // Set up customer and HappyKidsProxy mock behavior
        when(customer.getCart()).thenReturn(cart);
        when(customer.isVfp()).thenReturn(true);
        when(happyKidsProxy.computeDiscount(anyDouble(), anyDouble())).thenReturn(new HappyKidsDiscountDTO(270.0));

        // Act
        PaymentResponseDTO response = cashier.computePrice(customer);

        // Assert
        assertNotNull(response);
        assertEquals(300.0, response.totalPrice());
    }

    @Test
    void testComputePrice_WhenDifferentItemsHaveDifferentHours_ShouldReturnCorrectPrice() throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        // Arrange
        Item item1 = createTestItem(1, HAPPY_KIDS_ITEM_NAME, 100.0);
        Item item2 = createTestItem(2, HAPPY_KIDS_ITEM_NAME, 100.0);
        CartItem cartItem1 = new CartItem(item1, new CartItemAddDTO(3, LocalDateTime.now().plusHours(1), item1.getItemId())); // 3 x Item 1 = 300.0
        CartItem cartItem2 = new CartItem(item2, new CartItemAddDTO(3, LocalDateTime.now().minusHours(5), item2.getItemId())); // 3 x Item 2 = 300.0
        Cart cart = new Cart();
        cart.addItem(cartItem1);
        cart.addItem(cartItem2);

        // Simulating a VFP discount (10% discount for VFP members)
        VfpDiscountInPercentPerk vfpDiscountInPercentPerk = new VfpDiscountInPercentPerk(0.1, LocalTime.now().minusHours(1), LocalTime.now().plusHours(10));
        cart.addPerkToUse(vfpDiscountInPercentPerk);

        // Set up customer and HappyKidsProxy mock behavior
        when(customer.getCart()).thenReturn(cart);
        when(customer.isVfp()).thenReturn(true);
        when(happyKidsProxy.computeDiscount(anyDouble(), anyDouble())).thenReturn(new HappyKidsDiscountDTO(270.0));

        // Act
        PaymentResponseDTO response = cashier.computePrice(customer);

        // Assert
        assertNotNull(response);
        assertEquals(570.0, response.totalPrice());  // ((300 - 10%) + 300) = 270 + 300 = 570.0 after VFP discount
    }

    @Test
    void testComputePrice_WhenHappyKidsItemWithVFPDiscountInPercentPerk_ShouldReturnDiscountedPrice() throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        // Arrange
        Item item1 = createTestItem(1, HAPPY_KIDS_ITEM_NAME, 100.0);
        CartItem cartItem1 = new CartItem(item1, new CartItemAddDTO(3, LocalDateTime.of(2023, 10, 10, 1, 0), item1.getItemId())); // 3 x Item 1 = 300.0
        Cart cart = new Cart();
        cart.addItem(cartItem1);

        // Simulating a VFP discount (10% discount for VFP members) between 21:00 and 02:00
        VfpDiscountInPercentPerk vfpDiscountInPercentPerk = new VfpDiscountInPercentPerk(0.1, LocalTime.of(21, 0), LocalTime.of(2, 0));
        cart.addPerkToUse(vfpDiscountInPercentPerk);

        // Set up customer and HappyKidsProxy mock behavior
        when(customer.getCart()).thenReturn(cart);
        when(customer.isVfp()).thenReturn(true);
        when(happyKidsProxy.computeDiscount(anyDouble(), anyDouble())).thenReturn(new HappyKidsDiscountDTO(290.0));

        // Act
        PaymentResponseDTO response = cashier.computePrice(customer);

        // Assert
        assertNotNull(response);
        assertEquals(290.0, response.totalPrice());  // (300) - (100 * 2 * 0.1) = 280.0 after VFP discount
        verify(happyKidsProxy).computeDiscount(100.0, 0.1);
    }

    @Test
    void testComputePrice_WhenVFPDiscountPerkNotEligibleDueToItem_ShouldReturnFullPrice() throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        // Arrange
        Item item1 = createTestItem(1, "Item 1", 100.0);
        Item item2 = createTestItem(2, "Item 2", 50.0);
        CartItem cartItem1 = new CartItem(item1, new CartItemAddDTO(3, null, item1.getItemId())); // 3 x Item 1 = 300.0
        CartItem cartItem2 = new CartItem(item2, new CartItemAddDTO(2, null, item2.getItemId())); // 2 x Item 2 = 100.0
        Cart cart = new Cart();
        cart.addItem(cartItem1);
        cart.addItem(cartItem2);

        // Simulating a VFP discount (10% discount for VFP members)
        VfpDiscountInPercentPerk vfpDiscountInPercentPerk = new VfpDiscountInPercentPerk(0.1, LocalTime.of(21, 0), LocalTime.of(22, 0));
        cart.addPerkToUse(vfpDiscountInPercentPerk);

        // Set up customer and HappyKidsProxy mock behavior
        when(customer.getCart()).thenReturn(cart);
        when(customer.isVfp()).thenReturn(true);

        // Act
        PaymentResponseDTO response = cashier.computePrice(customer);

        // Assert
        assertNotNull(response);
        assertEquals(400.0, response.totalPrice());  // No discount applied because HappyKids eligibility fails
    }

    @Test
    void testComputePrice_WhenMultiplePerksAreApplied_ShouldReturnCorrectPriceAndQuantityForHappyKids() throws ClosedTimeException, UnreachableExternalServiceException, BookingTimeNotSetException {
        // Arrange
        Item item1 = createTestItem(1, HAPPY_KIDS_ITEM_NAME, 100.0);
        CartItem cartItem1 = new CartItem(item1, new CartItemAddDTO(3, LocalDateTime.now().plusHours(1), item1.getItemId())); // 3 x Item 1 = 300.0
        Cart cart = new Cart();
        cart.addItem(cartItem1);

        NPurchasedMGiftedPerk nPurchasedMGiftedPerk = new NPurchasedMGiftedPerk(3, 1, item1); // Buy 3, get 1 free
        TimedDiscountInPercentPerk timedDiscountInPercentPerk = new TimedDiscountInPercentPerk(LocalTime.now().minusMinutes(15), 0.2); // 20% discount activated just before the purchase
        VfpDiscountInPercentPerk vfpDiscountInPercentPerk = new VfpDiscountInPercentPerk(0.1, LocalTime.now().minusHours(1), LocalTime.now().plusHours(10));

        cart.addPerkToUse(vfpDiscountInPercentPerk);
        cart.addPerkToUse(nPurchasedMGiftedPerk);
        cart.addPerkToUse(timedDiscountInPercentPerk);

        when(customer.getCart()).thenReturn(cart);
        when(customer.isVfp()).thenReturn(true);
        when(happyKidsProxy.computeDiscount(anyDouble(), anyDouble())).thenReturn(new HappyKidsDiscountDTO(270.0));

        // Act
        PaymentResponseDTO response = cashier.computePrice(customer);

        // Assert
        assertNotNull(response);
        assertEquals(216.0, response.totalPrice());  // (300) - 10% = 270 - 20% = 216
        assertEquals(4, cartItem1.getQuantity()); // 3 purchased + 1 gifted
    }
}
