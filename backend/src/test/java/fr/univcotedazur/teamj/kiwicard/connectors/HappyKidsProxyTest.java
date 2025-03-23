package fr.univcotedazur.teamj.kiwicard.connectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.dto.CartItemAddDTO;
import fr.univcotedazur.teamj.kiwicard.dto.HappyKidsDiscountDTO;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.time.LocalDateTime;

import static fr.univcotedazur.teamj.kiwicard.configurations.Constants.HAPPY_KIDS_ITEM_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HappyKidsProxyTest {

    private static MockWebServer mockWebServer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private HappyKidsProxy happyKidsProxy;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() {
        try {
            mockWebServer.shutdown();
        } catch (IOException e) {
            Thread.getAllStackTraces().keySet().stream()
                    .filter(t -> t.getName().startsWith("MockWebServer /127.0.0.1"))
                    .forEach(it -> {
                        System.err.println("MockWebServer thread still running, shutting down: " + it.getName());
                        it.interrupt();
                    });
        }
    }

    @BeforeEach
    void init() {
        happyKidsProxy = new HappyKidsProxy(mockWebServer.url("/").toString());
    }

    @Test
    void computeDiscountWithSuccess() throws Exception {
        // Given
        double price = 100.0;
        double discountRate = 0.2; // 20% discount
        double expectedDiscountedPrice = price * (1 - discountRate); // 100 * 0.8 = 80.0

        HappyKidsDiscountDTO mockResponseDTO = new HappyKidsDiscountDTO(expectedDiscountedPrice);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.CREATED.value()) // Simulated 201 response
                .setBody(objectMapper.writeValueAsString(mockResponseDTO))
                .addHeader("Content-Type", "application/json"));

        Item item = Item.createTestItem(1, HAPPY_KIDS_ITEM_NAME, price);
        CartItemAddDTO cartItemAddDTO = new CartItemAddDTO(1, LocalDateTime.now(), 1);
        CartItem cartItem = new CartItem(item, cartItemAddDTO);

        // When
        HappyKidsDiscountDTO response = happyKidsProxy.computeDiscount(cartItem, discountRate);

        // Then
        assertNotNull(response);
        Assertions.assertEquals(expectedDiscountedPrice, response.price(), 0.001);
    }

    @Test
    void computeDiscountWithHigherDiscount() throws Exception {
        // Given
        double price = 200.0;
        double discountRate = 0.5; // 50% discount
        double expectedDiscountedPrice = price * (1 - discountRate); // 200 * 0.5 = 100.0

        HappyKidsDiscountDTO mockResponseDTO = new HappyKidsDiscountDTO(expectedDiscountedPrice);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.CREATED.value())
                .setBody(objectMapper.writeValueAsString(mockResponseDTO))
                .addHeader("Content-Type", "application/json"));

        Item item = Item.createTestItem(1, HAPPY_KIDS_ITEM_NAME, price);
        CartItemAddDTO cartItemAddDTO = new CartItemAddDTO(1, LocalDateTime.now(), 1);
        CartItem cartItem = new CartItem(item, cartItemAddDTO);

        // When
        HappyKidsDiscountDTO response = happyKidsProxy.computeDiscount(cartItem, discountRate);

        // Then
        assertNotNull(response);
        Assertions.assertEquals(expectedDiscountedPrice, response.price(), 0.001);
    }

    @Test
    void computeDiscountWithLowerDiscount() throws Exception {
        // Given
        double price = 150.0;
        double discountRate = 0.1; // 10% discount
        double expectedDiscountedPrice = price * (1 - discountRate); // 150 * 0.9 = 135.0

        HappyKidsDiscountDTO mockResponseDTO = new HappyKidsDiscountDTO(expectedDiscountedPrice);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.CREATED.value())
                .setBody(objectMapper.writeValueAsString(mockResponseDTO))
                .addHeader("Content-Type", "application/json"));

        Item item = Item.createTestItem(1, HAPPY_KIDS_ITEM_NAME, price);
        CartItemAddDTO cartItemAddDTO = new CartItemAddDTO(1, LocalDateTime.now(), 1);
        CartItem cartItem = new CartItem(item, cartItemAddDTO);

        // When
        HappyKidsDiscountDTO response = happyKidsProxy.computeDiscount(cartItem, discountRate);

        // Then
        assertNotNull(response);
        Assertions.assertEquals(expectedDiscountedPrice, response.price(), 0.001);
    }

    @Test
    void computeDiscountWithNoDiscount() throws Exception {
        // Given
        double price = 120.0;
        double discountRate = 0.0; // 0% discount
        double expectedDiscountedPrice = price; // 120 * 1 = 120.0

        HappyKidsDiscountDTO mockResponseDTO = new HappyKidsDiscountDTO(expectedDiscountedPrice);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.CREATED.value())
                .setBody(objectMapper.writeValueAsString(mockResponseDTO))
                .addHeader("Content-Type", "application/json"));

        Item item = Item.createTestItem(1, HAPPY_KIDS_ITEM_NAME, price);
        CartItemAddDTO cartItemAddDTO = new CartItemAddDTO(1, LocalDateTime.now(), 1);
        CartItem cartItem = new CartItem(item, cartItemAddDTO);

        // When
        HappyKidsDiscountDTO response = happyKidsProxy.computeDiscount(cartItem, discountRate);

        // Then
        assertNotNull(response);
        Assertions.assertEquals(expectedDiscountedPrice, response.price(), 0.001);
    }

    @Test
    void computeDiscountWithWrongStatusReturned() throws Exception {
        // Given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value()) // Should be CREATED (201)
                .setBody(objectMapper.writeValueAsString(new HappyKidsDiscountDTO(80.0)))
                .addHeader("Content-Type", "application/json"));

        Item item = Item.createTestItem(1, HAPPY_KIDS_ITEM_NAME, 100);
        CartItemAddDTO cartItemAddDTO = new CartItemAddDTO(1, LocalDateTime.now(), 1);
        CartItem cartItem = new CartItem(item, cartItemAddDTO);

        // When
        HappyKidsDiscountDTO response = happyKidsProxy.computeDiscount(cartItem, 0.2);

        // Then
        assertNotNull(response);
        Assertions.assertEquals(80.0, response.price(), 0.001);
    }

    @Test
    void computeDiscountWithBadRequest() {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.BAD_REQUEST.value()));

        Item item = Item.createTestItem(1, HAPPY_KIDS_ITEM_NAME, 100);
        CartItemAddDTO cartItemAddDTO = new CartItemAddDTO(1, LocalDateTime.now(), 1);
        CartItem cartItem = new CartItem(item, cartItemAddDTO);

        // When & Then
        assertThrows(UnreachableExternalServiceException.class, () ->
                happyKidsProxy.computeDiscount(cartItem, 0.2));
    }

    @Test
    void computeDiscountWithInternalServerError() {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        Item item = Item.createTestItem(1, HAPPY_KIDS_ITEM_NAME, 100);
        CartItemAddDTO cartItemAddDTO = new CartItemAddDTO(1, LocalDateTime.now(), 1);
        CartItem cartItem = new CartItem(item, cartItemAddDTO);

        // When & Then
        assertThrows(UnreachableExternalServiceException.class, () ->
                happyKidsProxy.computeDiscount(cartItem, 0.2));
    }
}
