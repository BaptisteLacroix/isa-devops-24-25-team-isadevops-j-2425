package fr.univcotedazur.teamj.kiwicard.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartCreator;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartModifier;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
    private ICartCreator creator;

    @MockitoBean
    private ICartModifier modifier;

    @MockitoBean
    private ICartFinder finder;

    private CartDTO cartDTO;
    private CartItemDTO cartItemDTO;
    private String customerEmail = "test@customer.com";
    private Long partnerId = 1L;

    @BeforeEach
    void setUp() {
        // Updated CartItemDTO initialization
        cartItemDTO = new CartItemDTO(1L, 2, null, null, 10L);

        // Updated CartDTO initialization with PartnerDTO and IPerkDTO List
        PartnerDTO partnerDTO = new PartnerDTO(partnerId, "Partner Name", "Partner Address");
        cartDTO = new CartDTO(100L, partnerDTO, Set.of(cartItemDTO), List.of());
    }

    @Test
    void createCart() throws Exception {
        List<CartItemDTO> cartItemDTOS = List.of(cartItemDTO);
        when(creator.createCart(customerEmail, partnerId, cartItemDTOS)).thenReturn(cartDTO);

        MvcResult result = mockMvc.perform(post(CartController.CART_URI + "/" + customerEmail + "/" + partnerId)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(cartItemDTOS)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        CartDTO responseCart = OBJECT_MAPPER.readValue(jsonResult, CartDTO.class);
        assertEquals(cartDTO.partner().id(), responseCart.partner().id());
        assertEquals(cartDTO.items().size(), responseCart.items().size());
    }

    @Test
    void addItemToCart() throws Exception {
        when(modifier.addItemToCart(customerEmail, cartItemDTO)).thenReturn(cartDTO);

        MvcResult result = mockMvc.perform(put(CartController.CART_URI + "/" + customerEmail)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(cartItemDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        CartDTO responseCart = OBJECT_MAPPER.readValue(jsonResult, CartDTO.class);
        assertEquals(cartDTO.partner().id(), responseCart.partner().id());
        assertEquals(cartDTO.items().size(), responseCart.items().size());
        // Check if the added item is present in the cart
        assertNotNull(responseCart.items().stream().filter(item -> item.itemId().equals(cartItemDTO.itemId())).findFirst().orElse(null));
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
        assertNull(responseCart.items().stream().filter(item -> item.itemId().equals(cartItemDTO.itemId())).findFirst().orElse(null));
    }


    @Test
    void validateCart() throws Exception {

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
    void createCartUnknownCustomer() throws Exception {
        List<CartItemDTO> cartItemDTOS = List.of(cartItemDTO);
        when(creator.createCart(customerEmail, partnerId, cartItemDTOS)).thenThrow(new UnknownCustomerEmailException(customerEmail));

        mockMvc.perform(post(CartController.CART_URI + "/" + customerEmail + "/" + partnerId)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(cartItemDTOS)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void addItemToCartUnknownItem() throws Exception {
        when(modifier.addItemToCart(customerEmail, cartItemDTO)).thenThrow(new UnknownItemIdException(cartItemDTO.itemId()));

        mockMvc.perform(put(CartController.CART_URI + "/" + customerEmail)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(cartItemDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
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
