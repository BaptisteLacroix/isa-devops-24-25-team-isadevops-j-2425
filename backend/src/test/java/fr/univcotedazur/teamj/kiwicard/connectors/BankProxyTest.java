package fr.univcotedazur.teamj.kiwicard.connectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.connectors.externaldto.PaymentRequestDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankProxyTest {

    private static MockWebServer mockWebServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private BankProxy bankProxy;

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
        WebClient.Builder webClientBuilder = WebClient.builder();
        bankProxy = new BankProxy(mockWebServer.url("/").toString(), webClientBuilder);
    }

    @Test
    void payWithSuccess() throws Exception {
        // Given
        PaymentDTO paymentDTO = new PaymentDTO("654321", 100.0, true);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody(objectMapper.writeValueAsString(paymentDTO))
                .addHeader("Content-Type", "application/json"));

        // When
        PaymentDTO response = bankProxy.askPayment(new PaymentRequestDTO("1234567890", 100.0));

        // Then
        assertNotNull(response);
        assertEquals(100.0, response.amount());
        assertTrue(response.authorized());
    }

    @Test
    void payWithWrongStatusReturned() throws Exception {
        // Given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.CREATED.value()) // Should be OK (200)
                .setBody(objectMapper.writeValueAsString(new PaymentDTO("654321", 100.0, false)))
                .addHeader("Content-Type", "application/json"));

        // When
        PaymentDTO response = bankProxy.askPayment(new PaymentRequestDTO("1234567890", 100.0));

        // Then
        assertNotNull(response);
        assertFalse(response.authorized());
    }

    @Test
    void payRejected() {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.BAD_REQUEST.value()));

        // When & Then
        assertThrows(UnreachableExternalServiceException.class, () ->
                bankProxy.askPayment(new PaymentRequestDTO("1234567890", 100.0)));
    }

    @Test
    void payOn404shouldRaiseAnException() {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value()));

        // When & Then
        assertThrows(UnreachableExternalServiceException.class, () ->
                bankProxy.askPayment(new PaymentRequestDTO("1234567890", 100.0)));
    }

    @Test
    void payOn500shouldRaiseAnException() {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        // When & Then
        assertThrows(UnreachableExternalServiceException.class, () ->
                bankProxy.askPayment(new PaymentRequestDTO("1234567890", 100.0)));
    }
}
