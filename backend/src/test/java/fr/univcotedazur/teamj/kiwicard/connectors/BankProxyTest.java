package fr.univcotedazur.teamj.kiwicard.connectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.connectors.externaldto.PaymentReceiptDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BankProxyTest {

    private static MockWebServer mockWebServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private BankProxy bankProxy;

    @BeforeAll
    static void setUp() throws IOException {
    }

    @AfterAll
    static void tearDown() {
    }

    @BeforeEach
    void init() {
    }

    @Test
    void payWithSuccess() throws Exception {
    }

    @Test
    void payWithWrongStatusReturned() throws Exception {
    }

    @Test
    void payWithEmptyBody() {
    }

    @Test
    void payRejected() {
    }

    @Test
    void payOn404shouldRaiseAnException() {
    }

    @Test
    void payOn500shouldRaiseAnException() {
    }

    @Test
    void payTimeout() {
    }
}
