package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.IPayment;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.repositories.IItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
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
    private Item item;
    private CartItemDTO cartItemDTO;
    @Mock
    private CartDTO cartDTO;
    @Mock
    private CustomerDTO customerDTO;
    private PartnerDTO partnerDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        item = Item.createTestItem(1, "Item1", 10);
        cartItemDTO = new CartItemDTO(1L, 2, null, null, 1L);
        CartItem cartItem = new CartItem(cartItemDTO);
        Cart cart = new Cart(partner, new HashSet<>(List.of(cartItem)), new ArrayList<>());

        when(customer.getEmail()).thenReturn("customer@email.com");
        when(customer.getFirstName()).thenReturn("John");
        when(customer.getSurname()).thenReturn("Doe");
        when(customer.getCart()).thenReturn(cart);
        when(customer.isVfp()).thenReturn(false);
        when(customer.getCardNumber()).thenReturn("1234567890");
        when(customer.getAddress()).thenReturn("Address");

        when(partner.getPartnerId()).thenReturn(1L);
        when(partner.getName()).thenReturn("Partner");
        when(partner.getAddress()).thenReturn("Address");

        partnerDTO = new PartnerDTO(partner);

        when(cartDTO.cartId()).thenReturn(1L);
        when(cartDTO.partner()).thenReturn(partnerDTO);
        when(cartDTO.items()).thenReturn(new HashSet<>(List.of(cartItemDTO)));
        when(cartDTO.perksList()).thenReturn(new ArrayList<>());

        when(customerDTO.cartDTO()).thenReturn(cartDTO);
        when(customerDTO.vfp()).thenReturn(false);
        when(customerDTO.creditCard()).thenReturn("1234567890");
        when(customerDTO.email()).thenReturn("customer@email.com");
        when(customerDTO.firstName()).thenReturn("John");
        when(customerDTO.surname()).thenReturn("Doe");
    }

    @Test
    void createCart_shouldThrowUnknownCustomerEmailException_whenCustomerNotFound() throws UnknownCustomerEmailException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UnknownCustomerEmailException.class, () -> cartService.createCart("nonexistent@example.com", 1L, new ArrayList<>()));
    }

    @Test
    void createCart_shouldThrowUnknownPartnerIdException_whenPartnerNotFound() throws UnknownPartnerIdException, UnknownCustomerEmailException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(Optional.of(customerDTO));
        when(partnerManager.findAllPartnerItems(anyLong())).thenReturn(Collections.emptyList());
        when(partnerManager.findPartnerById(anyLong())).thenThrow(UnknownPartnerIdException.class);

        // When & Then
        assertThrows(UnknownPartnerIdException.class, () -> cartService.createCart("customer@example.com", 1L, new ArrayList<>()));
    }

    @Test
    void createCart_shouldThrowUnknownItemIdException_whenItemNotFound() throws UnknownPartnerIdException, UnknownCustomerEmailException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(Optional.of(customerDTO));
        when(partnerManager.findAllPartnerItems(anyLong())).thenReturn(List.of(item));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UnknownItemIdException.class, () -> cartService.createCart("customer@example.com", 1L, List.of(cartItemDTO)));
    }

    @Test
    void createCart_shouldCreateCartSuccessfully() throws UnknownCustomerEmailException, UnknownPartnerIdException, UnknownItemIdException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(Optional.of(customerDTO));
        when(partnerManager.findAllPartnerItems(anyLong())).thenReturn(List.of(item));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(partnerManager.findPartnerById(anyLong())).thenReturn(partnerDTO);

        // When
        CartDTO result = cartService.createCart("customer@example.com", 1L, List.of(cartItemDTO));

        // Then
        assertNotNull(result);
        assertEquals(1, result.items().size());
    }

    @Test
    void addItemToCart_shouldThrowUnknownCustomerEmailException_whenCustomerNotFound() throws UnknownCustomerEmailException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UnknownCustomerEmailException.class, () -> cartService.addItemToCart("nonexistent@example.com", cartItemDTO));
    }

    @Test
    void addItemToCart_shouldThrowUnknownItemIdException_whenItemNotFound() throws UnknownCustomerEmailException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(Optional.of(customerDTO));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UnknownItemIdException.class, () -> cartService.addItemToCart("customer@example.com", cartItemDTO));
    }

    @Test
    void addItemToCart_shouldAddItemSuccessfully() throws UnknownCustomerEmailException, UnknownItemIdException, UnknownPartnerIdException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(Optional.of(customerDTO));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(partnerManager.findAllPartnerItems(anyLong())).thenReturn(List.of(item));

        // When
        CartDTO result = cartService.addItemToCart("customer@example.com", cartItemDTO);

        // Then
        assertNotNull(result);
        assertEquals(2, result.items().size());
    }

    @Test
    void removeItemFromCart_shouldThrowUnknownCustomerEmailException_whenCustomerNotFound() throws UnknownCustomerEmailException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UnknownCustomerEmailException.class, () -> cartService.removeItemFromCart("nonexistent@example.com", cartItemDTO));
    }

    @Test
    void removeItemFromCart_shouldRemoveItemSuccessfully() throws UnknownCustomerEmailException {
        // Given
        when(customerCatalog.findCustomerByEmail(anyString())).thenReturn(Optional.of(customerDTO));

        // When
        CartDTO result = cartService.removeItemFromCart("customer@example.com", cartItemDTO);

        // Then
        assertNotNull(result);
        assertTrue(result.items().isEmpty());
    }
}
