package fr.univcotedazur.teamj.kiwicard.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemAddItemToCartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartModifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CartControllerWebMvcTest extends BaseUnitTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ICartModifier modifier;

    @MockitoBean
    private ICartFinder finder;

    private CartDTO cartDTO;
    private CartItemDTO cartItemDTO;
    private CartItemAddItemToCartDTO cartItemAddItemToCartDTO;
    private String customerEmail = "test@customer.com";
    private Long partnerId = 1L;

    @BeforeEach
    void setUp() {
        Item item = mock(Item.class);
        when(item.getItemId()).thenReturn(1L);
        when(item.getLabel()).thenReturn("Item Label");
        when(item.getPrice()).thenReturn(10.0);
        // Updated CartItemDTO initialization
        cartItemDTO = new CartItemDTO(2, null, new ItemDTO(item));
        cartItemAddItemToCartDTO = new CartItemAddItemToCartDTO(2, null, item.getItemId());

        // Updated CartDTO initialization with PartnerDTO and IPerkDTO List
        PartnerDTO partnerDTO = new PartnerDTO(partnerId, "Partner Name", "Partner Address");
        cartDTO = new CartDTO(100L, partnerDTO, Set.of(cartItemDTO), List.of());
    }

    @Test
    void addItemCreateCart() throws Exception {
        when(modifier.addItemToCart(customerEmail, cartItemAddItemToCartDTO, null)).thenReturn(cartDTO);

        MvcResult result = mockMvc.perform(put(CartController.CART_URI + "/" + customerEmail)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(cartItemAddItemToCartDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        CartDTO responseCart = OBJECT_MAPPER.readValue(jsonResult, CartDTO.class);
        assertEquals(cartDTO.partner().id(), responseCart.partner().id());
        assertEquals(cartDTO.items().size(), responseCart.items().size());
    }

    @Test
    void addItemToCart() throws Exception {
        when(modifier.addItemToCart(customerEmail, cartItemAddItemToCartDTO, cartDTO)).thenReturn(cartDTO);
        when(finder.findCustomerCart(customerEmail)).thenReturn(Optional.of(cartDTO));

        MvcResult result = mockMvc.perform(put(CartController.CART_URI + "/" + customerEmail)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(cartItemAddItemToCartDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        CartDTO responseCart = OBJECT_MAPPER.readValue(jsonResult, CartDTO.class);
        assertEquals(cartDTO.partner().id(), responseCart.partner().id());
        assertEquals(cartDTO.items().size(), responseCart.items().size());
        // Check if the added item is present in the cart
        assertNotNull(responseCart.items().stream().filter(item -> item.item().itemId() == cartItemDTO.item().itemId()).findFirst().orElse(null));
    }

    @Test
    void addItemCreateCartUnknownCustomer() throws Exception {
        when(modifier.addItemToCart(customerEmail, cartItemAddItemToCartDTO, null)).thenThrow(new UnknownCustomerEmailException(customerEmail));

        mockMvc.perform(put(CartController.CART_URI + "/" + customerEmail)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(cartItemAddItemToCartDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void addItemToCartUnknownItem() throws Exception {
        when(modifier.addItemToCart(customerEmail, cartItemAddItemToCartDTO, cartDTO)).thenThrow(new UnknownItemIdException(cartItemDTO.item().itemId()));
        when(finder.findCustomerCart(customerEmail)).thenReturn(Optional.of(cartDTO));
        mockMvc.perform(put(CartController.CART_URI + "/" + customerEmail)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(cartItemAddItemToCartDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void removeItemFromCart() throws Exception {
        Set<CartItemDTO> cartItemDTOS = new HashSet<>(cartDTO.items());
        // Remove the cartItemDTO from the Set
        cartItemDTOS.remove(cartItemDTO);
        // Modified CartDTO with the updated set of items
        CartDTO modifiedCartDTO = new CartDTO(cartDTO.cartId(), cartDTO.partner(), cartItemDTOS, cartDTO.perksList());
        when(modifier.removeItemFromCart(customerEmail, cartItemDTO)).thenReturn(modifiedCartDTO);
        MvcResult result = mockMvc.perform(delete(CartController.CART_URI + "/" + customerEmail)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(cartItemDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        CartDTO responseCart = OBJECT_MAPPER.readValue(jsonResult, CartDTO.class);
        assertEquals(cartDTO.partner().id(), responseCart.partner().id());
        assertNotEquals(cartDTO.items().size(), responseCart.items().size());
        assertNull(responseCart.items().stream().filter(item -> item.item().itemId() == cartItemDTO.item().itemId()).findFirst().orElse(null));
    }

    @Test
    void validateCart_shouldReturnCreated_whenCartIsValid() throws Exception {
        // Given
        PurchaseDTO purchaseDTO = new PurchaseDTO(customerEmail, cartDTO, new PaymentDTO(
                "1234-5678-9012-3456",
                23.0,
                true
        ));
        when(modifier.validateCart(customerEmail)).thenReturn(purchaseDTO);

        // When
        MvcResult result = mockMvc.perform(post(CartController.CART_URI + "/" + customerEmail + "/validate")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        // Then
        String jsonResult = result.getResponse().getContentAsString();
        PurchaseDTO responsePurchase = OBJECT_MAPPER.readValue(jsonResult, PurchaseDTO.class);
        Assertions.assertNotNull(responsePurchase);
        Assertions.assertEquals(purchaseDTO.cartOwnerEmail(), responsePurchase.cartOwnerEmail());
        Assertions.assertEquals(purchaseDTO.cartDTO(), responsePurchase.cartDTO());
        Assertions.assertEquals(purchaseDTO.paymentDTO(), responsePurchase.paymentDTO());
    }

    @Test
    void validateCart_shouldReturnNotFound_whenCustomerIsNotFound() throws Exception {
        // Given
        when(modifier.validateCart(customerEmail)).thenThrow(new UnknownCustomerEmailException(customerEmail));

        // When & Then
        mockMvc.perform(post(CartController.CART_URI + "/" + customerEmail + "/validate")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void validateCart_shouldReturnInternalServerError_whenPaymentServiceFails() throws Exception {
        // Given
        when(modifier.validateCart(customerEmail)).thenThrow(new UnreachableExternalServiceException());

        // When & Then
        mockMvc.perform(post(CartController.CART_URI + "/" + customerEmail + "/validate")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void getCart() throws Exception {
        when(finder.findCustomerCart(customerEmail)).thenReturn(Optional.of(cartDTO));

        MvcResult result = mockMvc.perform(get(CartController.CART_URI + "/" + customerEmail)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        CartDTO responseCart = OBJECT_MAPPER.readValue(jsonResult, CartDTO.class);
        assertEquals(cartDTO.cartId(), responseCart.cartId());
        assertEquals(cartDTO.items().size(), responseCart.items().size());
    }

    @Test
    void removeItemFromCartUnknownCustomer() throws Exception {
        when(modifier.removeItemFromCart(customerEmail, cartItemDTO)).thenThrow(new UnknownCustomerEmailException(customerEmail));

        mockMvc.perform(delete(CartController.CART_URI + "/" + customerEmail)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(cartItemDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void getCartUnknownCustomer() throws Exception {
        when(finder.findCustomerCart(customerEmail)).thenThrow(new UnknownCustomerEmailException(customerEmail));

        mockMvc.perform(get(CartController.CART_URI + "/" + customerEmail)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }
}
