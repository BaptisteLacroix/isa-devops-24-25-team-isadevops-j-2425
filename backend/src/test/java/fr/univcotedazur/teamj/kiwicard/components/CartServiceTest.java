package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.configurations.Constants;
import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemAddDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyBookedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.EmptyCartException;
import fr.univcotedazur.teamj.kiwicard.exceptions.NoCartException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.IPayment;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.repositories.IItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class CartServiceTest extends BaseUnitTest {

    @Mock
    private IItemRepository itemRepository;

    @Mock
    private IPartnerManager partnerManager;

    @Mock
    private IPayment payment;

    @Mock
    private CustomerCatalog customerCatalog;

    @InjectMocks
    private CartService cartService;
    @Mock
    private Customer customer;
    @Mock
    private Partner partner;
    @Mock
    private Item item;
    private CartItemDTO cartItemDTO;
    private CartItemAddDTO cartItemAddDTO;
    @Mock
    private CartDTO existingCartDTO;
    @Mock
    private CustomerDTO customerDTO;
    private PartnerDTO partnerDTO;
    @Mock
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking the Item entity
        item = mock(Item.class);
        when(item.getItemId()).thenReturn(1L);
        when(item.getLabel()).thenReturn("Item");
        when(item.getPrice()).thenReturn(10.0);
        when(item.getPartner()).thenReturn(partner);
        cartItemDTO = new CartItemDTO(2, null, new ItemDTO(item));
        cartItemAddDTO = new CartItemAddDTO(2, null, item.getItemId());

        cartItem = mock(CartItem.class);
        when(cartItem.getCartItemId()).thenReturn(1L);
        when(cartItem.getQuantity()).thenReturn(2);
        when(cartItem.getItem()).thenReturn(item);
        when(cartItem.getPrice()).thenReturn(20.0);

        // Mocking the Cart entity, including cartId
        Cart cart = mock(Cart.class);
        when(cart.getCartId()).thenReturn(1L);
        when(cart.getPartner()).thenReturn(partner);
        when(cart.getItems()).thenReturn(new HashSet<>(List.of(cartItem)));
        when(cart.getPerksToUse()).thenReturn(new ArrayList<>());

        when(customer.getEmail()).thenReturn("customer@email.com");
        when(customer.getFirstName()).thenReturn("John");
        when(customer.getSurname()).thenReturn("Doe");
        when(customer.getCart()).thenReturn(cart);
        when(customer.isVfp()).thenReturn(false);
        when(customer.getCardNumber()).thenReturn("1234567890");
        when(customer.getAddress()).thenReturn("Address");
        when(customer.getCart()).thenReturn(cart);

        when(partner.getPartnerId()).thenReturn(1L);
        when(partner.getName()).thenReturn("Partner");
        when(partner.getAddress()).thenReturn("Address");

        partnerDTO = new PartnerDTO(partner);

        when(existingCartDTO.cartId()).thenReturn(1L);
        when(existingCartDTO.partner()).thenReturn(partnerDTO);
        when(existingCartDTO.items()).thenReturn(new HashSet<>(List.of(cartItemDTO)));
        when(existingCartDTO.perksList()).thenReturn(new ArrayList<>());

        when(customerDTO.cart()).thenReturn(existingCartDTO);
        when(customerDTO.vfp()).thenReturn(false);
        when(customerDTO.creditCard()).thenReturn("1234567890");
        when(customerDTO.email()).thenReturn("customer@email.com");
        when(customerDTO.firstName()).thenReturn("John");
        when(customerDTO.surname()).thenReturn("Doe");
    }

    @Test
    void createCart_shouldThrowUnknownCustomerEmailException_whenCustomerNotFound() throws UnknownCustomerEmailException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenThrow(UnknownCustomerEmailException.class);

        // When & Then
        assertThrows(UnknownCustomerEmailException.class, () -> cartService.addItemToCart("nonexistent@example.com", cartItemAddDTO, null));
    }

    @Test
    void createCart_shouldThrowUnknownItemIdException_whenItemNotFound() throws UnknownPartnerIdException, UnknownCustomerEmailException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(customer);
        when(partnerManager.findAllPartnerItems(anyLong())).thenReturn(List.of(item));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UnknownItemIdException.class, () -> cartService.addItemToCart("customer@example.com", cartItemAddDTO, null));
    }

    @Test
    void createCart_shouldCreateCartSuccessfully() throws UnknownCustomerEmailException, UnknownPartnerIdException, UnknownItemIdException, NoCartException, AlreadyBookedTimeException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(customer);
        when(partnerManager.findAllPartnerItems(anyLong())).thenReturn(List.of(item));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(partnerManager.findPartnerById(anyLong())).thenReturn(partner);
        when(customerCatalog.setCart(anyString(), any())).thenReturn(customer);

        // When
        CartDTO result = cartService.addItemToCart("customer@example.com", cartItemAddDTO, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.items().size());
    }

    @Test
    void addItemToCart_shouldAddClassicItemSuccessfully() throws UnknownCustomerEmailException, UnknownItemIdException, UnknownPartnerIdException, NoCartException, AlreadyBookedTimeException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(customer);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(partnerManager.findAllPartnerItems(anyLong())).thenReturn(List.of(item));
        when(customerCatalog.setCart(anyString(), any())).thenReturn(customer);

        // When
        CartDTO result = cartService.addItemToCart("customer@example.com", cartItemAddDTO, existingCartDTO);

        // Then
        assertNotNull(result);
        assertTrue(result.items().contains(cartItemDTO));
    }

    @Test
    void addItemToCart_shouldAddHappyKidsItemSuccessfully() throws UnknownCustomerEmailException, UnknownPartnerIdException, NoCartException, AlreadyBookedTimeException, UnknownItemIdException {
        // Given
        Item happyKidsItem = mock(Item.class);
        when(happyKidsItem.getItemId()).thenReturn(2L);
        when(happyKidsItem.getLabel()).thenReturn(Constants.HAPPY_KIDS_ITEM_NAME);
        when(happyKidsItem.getPrice()).thenReturn(10.0);
        when(happyKidsItem.getPartner()).thenReturn(partner);
        CartItemAddDTO cartItemHKDTO = new CartItemAddDTO(1, LocalDateTime.of(2025, 3, 15, 10, 0), happyKidsItem.getItemId());
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(customer);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(happyKidsItem));
        when(partnerManager.findAllPartnerItems(anyLong())).thenReturn(List.of(happyKidsItem));
        when(customerCatalog.setCart(anyString(), any())).thenReturn(customer);

        // When
        CartDTO result = cartService.addItemToCart("customer@example.com", cartItemHKDTO, existingCartDTO);

        // Then
        assertNotNull(result);
        assertEquals(2, result.items().size());
        assertTrue(result.items().contains(new CartItemDTO(1, LocalDateTime.of(2025, 3, 15, 10, 0), new ItemDTO(happyKidsItem))));
    }

    @Test
    void addItemToCart_shouldThrowIfHappyKidsItemWithSameDateAlreadyIsInCart() throws UnknownCustomerEmailException, UnknownPartnerIdException, NoCartException, AlreadyBookedTimeException, UnknownItemIdException {
        Item happyKidsItem = mock(Item.class);
        when(happyKidsItem.getItemId()).thenReturn(2L);
        when(happyKidsItem.getLabel()).thenReturn(Constants.HAPPY_KIDS_ITEM_NAME);
        when(happyKidsItem.getPrice()).thenReturn(10.0);
        when(happyKidsItem.getPartner()).thenReturn(partner);
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(customer);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(happyKidsItem));
        when(partnerManager.findAllPartnerItems(anyLong())).thenReturn(List.of(happyKidsItem));
        when(customerCatalog.setCart(anyString(), any())).thenReturn(customer);
        CartItem hkCartItem = mock(CartItem.class);
        when(hkCartItem.getCartItemId()).thenReturn(2L);
        when(hkCartItem.getQuantity()).thenReturn(1);
        when(hkCartItem.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 15, 10, 0));
        when(hkCartItem.getItem()).thenReturn(happyKidsItem);
        when(hkCartItem.getPrice()).thenReturn(10.0);
        Cart cartHKCartItem = spy(new Cart(partner, new HashSet<>(List.of(hkCartItem)), new ArrayList<>()));
        when(cartHKCartItem.getCartId()).thenReturn(2L);
        when(customer.getCart()).thenReturn(cartHKCartItem);

        // When & Then
        CartItemAddDTO cartItemHKDTO = new CartItemAddDTO(1, LocalDateTime.of(2025, 3, 15, 10, 0), happyKidsItem.getItemId());
        assertThrows(AlreadyBookedTimeException.class, () -> cartService.addItemToCart("customer@example.com", cartItemHKDTO, existingCartDTO));
    }

    @Test
    void addItemToCart_shouldThrowIfHappyKidsItemWithJustEarlierDateAlreadyIsInCart() throws UnknownCustomerEmailException, UnknownPartnerIdException, NoCartException, AlreadyBookedTimeException, UnknownItemIdException {
        Item happyKidsItem = mock(Item.class);
        when(happyKidsItem.getItemId()).thenReturn(2L);
        when(happyKidsItem.getLabel()).thenReturn(Constants.HAPPY_KIDS_ITEM_NAME);
        when(happyKidsItem.getPrice()).thenReturn(10.0);
        when(happyKidsItem.getPartner()).thenReturn(partner);
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(customer);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(happyKidsItem));
        when(partnerManager.findAllPartnerItems(anyLong())).thenReturn(List.of(happyKidsItem));
        when(customerCatalog.setCart(anyString(), any())).thenReturn(customer);
        CartItem hkCartItem = mock(CartItem.class);
        when(hkCartItem.getCartItemId()).thenReturn(2L);
        when(hkCartItem.getQuantity()).thenReturn(2);
        when(hkCartItem.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 15, 10, 0));
        when(hkCartItem.getItem()).thenReturn(happyKidsItem);
        when(hkCartItem.getPrice()).thenReturn(10.0);
        Cart cartHKCartItem = spy(new Cart(partner, new HashSet<>(List.of(hkCartItem)), new ArrayList<>()));
        when(cartHKCartItem.getCartId()).thenReturn(2L);
        when(customer.getCart()).thenReturn(cartHKCartItem);

        // When & Then
        CartItemAddDTO cartItemHKDTO = new CartItemAddDTO(1, LocalDateTime.of(2025, 3, 15, 11, 0), happyKidsItem.getItemId());
        assertThrows(AlreadyBookedTimeException.class, () -> cartService.addItemToCart("customer@example.com", cartItemHKDTO, existingCartDTO));
    }

    @Test
    void addItemToCart_shouldThrowIfHappyKidsItemWithJustLaterDateAlreadyIsInCart() throws UnknownCustomerEmailException, UnknownPartnerIdException, NoCartException, AlreadyBookedTimeException, UnknownItemIdException {
        Item happyKidsItem = mock(Item.class);
        when(happyKidsItem.getItemId()).thenReturn(2L);
        when(happyKidsItem.getLabel()).thenReturn(Constants.HAPPY_KIDS_ITEM_NAME);
        when(happyKidsItem.getPrice()).thenReturn(10.0);
        when(happyKidsItem.getPartner()).thenReturn(partner);
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(customer);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(happyKidsItem));
        when(partnerManager.findAllPartnerItems(anyLong())).thenReturn(List.of(happyKidsItem));
        when(customerCatalog.setCart(anyString(), any())).thenReturn(customer);
        CartItem hkCartItem = mock(CartItem.class);
        when(hkCartItem.getCartItemId()).thenReturn(2L);
        when(hkCartItem.getQuantity()).thenReturn(2);
        when(hkCartItem.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 15, 10, 0));
        when(hkCartItem.getItem()).thenReturn(happyKidsItem);
        when(hkCartItem.getPrice()).thenReturn(10.0);
        Cart cartHKCartItem = spy(new Cart(partner, new HashSet<>(List.of(hkCartItem)), new ArrayList<>()));
        when(cartHKCartItem.getCartId()).thenReturn(2L);
        when(customer.getCart()).thenReturn(cartHKCartItem);

        // When & Then
        CartItemAddDTO cartItemHKDTO = new CartItemAddDTO(2, LocalDateTime.of(2025, 3, 15, 9, 0), happyKidsItem.getItemId());
        assertThrows(AlreadyBookedTimeException.class, () -> cartService.addItemToCart("customer@example.com", cartItemHKDTO, existingCartDTO));
    }

    @Test
    void addItemToCart_shouldAddHappyKidsItemWithDifferentDateAlreadyIsInCart() throws UnknownCustomerEmailException, UnknownPartnerIdException, NoCartException, AlreadyBookedTimeException, UnknownItemIdException {
        Item happyKidsItem = mock(Item.class);
        when(happyKidsItem.getItemId()).thenReturn(2L);
        when(happyKidsItem.getLabel()).thenReturn(Constants.HAPPY_KIDS_ITEM_NAME);
        when(happyKidsItem.getPrice()).thenReturn(10.0);
        when(happyKidsItem.getPartner()).thenReturn(partner);
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(customer);
        when(itemRepository.findById(happyKidsItem.getItemId())).thenReturn(Optional.of(happyKidsItem));
        when(partnerManager.findAllPartnerItems(anyLong())).thenReturn(List.of(happyKidsItem));
        when(customerCatalog.setCart(anyString(), any())).thenReturn(customer);
        CartItem hkCartItem = mock(CartItem.class);
        when(hkCartItem.getCartItemId()).thenReturn(2L);
        when(hkCartItem.getQuantity()).thenReturn(1);
        when(hkCartItem.getStartTime()).thenReturn(LocalDateTime.of(2025, 3, 15, 10, 0));
        when(hkCartItem.getItem()).thenReturn(happyKidsItem);
        when(hkCartItem.getPrice()).thenReturn(10.0);
        Cart cartHKCartItem = spy(new Cart(partner, new HashSet<>(List.of(hkCartItem)), new ArrayList<>()));
        when(cartHKCartItem.getCartId()).thenReturn(2L);
        when(customer.getCart()).thenReturn(cartHKCartItem);

        // When
        CartItemAddDTO cartItemHKDTO = new CartItemAddDTO(1, LocalDateTime.of(2025, 3, 15, 11, 0), happyKidsItem.getItemId());
        CartDTO result = cartService.addItemToCart("customer@example.com", cartItemHKDTO, existingCartDTO);

        // Then
        assertNotNull(result);
        assertEquals(2, result.items().size());
        assertTrue(result.items().contains(new CartItemDTO(1, LocalDateTime.of(2025, 3, 15, 10, 0), new ItemDTO(happyKidsItem))));
    }

    @Test
    void removeItemFromCart_shouldThrowUnknownCustomerEmailException_whenCustomerNotFound() throws UnknownCustomerEmailException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenThrow(UnknownCustomerEmailException.class);

        // When & Then
        assertThrows(UnknownCustomerEmailException.class, () -> cartService.removeItemFromCart("nonexistent@example.com", cartItemDTO));
    }

    @Test
    void removeItemFromCart_shouldRemoveItemSuccessfully() throws UnknownCustomerEmailException, EmptyCartException, NoCartException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(customer);
        when(customerCatalog.setCart(anyString(), any())).thenReturn(customer);

        // When
        CartDTO result = cartService.removeItemFromCart("customer@example.com", cartItemDTO);

        // Then
        assertNotNull(result);
        assertTrue(result.items().isEmpty());
    }

    @Test
    void validateCart_shouldThrowUnknownCustomerEmailException_whenCustomerNotFound() throws UnknownCustomerEmailException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenThrow(UnknownCustomerEmailException.class);

        // When & Then
        assertThrows(UnknownCustomerEmailException.class, () -> cartService.validateCart("nonexistent@example.com"));
    }

    @Test
    void validateCart_shouldThrowUnreachableExternalServiceException_whenPaymentServiceFails() throws UnknownCustomerEmailException, UnreachableExternalServiceException, ClosedTimeException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(customer);
        when(payment.makePay(any(Customer.class))).thenThrow(UnreachableExternalServiceException.class);

        // When & Then
        assertThrows(UnreachableExternalServiceException.class, () -> cartService.validateCart("customer@email.com"));
    }

    @Test
    void validateCart_shouldReturnPurchaseDTO_whenCartIsValid() throws UnknownCustomerEmailException, UnreachableExternalServiceException, EmptyCartException, NoCartException, ClosedTimeException {
        // Given
        PaymentDTO paymentDTO = mock(PaymentDTO.class);
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(customer);
        when(payment.makePay(any(Customer.class))).thenReturn(paymentDTO);

        // When
        PurchaseDTO result = cartService.validateCart("customer@email.com");

        // Then
        assertNotNull(result);
        assertEquals("customer@email.com", result.email());
        assertNotNull(result.cartDTO());
        assertNotNull(result.paymentDTO());
    }

    @Test
    void validateCart_shouldThrowNoCartException_whenCartDoesNotExist() throws UnknownCustomerEmailException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(customer);
        when(customer.getCart()).thenReturn(null);

        // When & Then
        assertThrows(NoCartException.class, () -> cartService.validateCart("customer@email.com"));
    }

    @Test
    void validateCart_shouldThrowEmptyCartException_whenCartIsEmpty() throws UnknownCustomerEmailException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(customer);
        when(customer.getCart()).thenReturn(new Cart());

        // When & Then
        assertThrows(EmptyCartException.class, () -> cartService.validateCart("customer@email.com"));

    }

}
