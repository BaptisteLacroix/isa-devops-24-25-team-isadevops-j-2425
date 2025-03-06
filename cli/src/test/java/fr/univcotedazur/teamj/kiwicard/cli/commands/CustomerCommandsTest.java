package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

class CustomerCommandsTest {

    private CustomerCommands customerCommands;

    private static MockWebServer mockWebServer;
    private static CliSession cliSession;

    @BeforeAll
    static void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        cliSession = new CliSession();
    }

    @AfterAll
    static void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void init() {
        customerCommands = new CustomerCommands(WebClient.create(mockWebServer.url("/").toString()), cliSession);
    }

    @Test
    void registerClientSuccessTest() throws Exception {
        // Simulate a successful registration response
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                          "email": "john.doe@example.com",
                          "surname": "Doe",
                          "firstname": "John",
                          "address": "123 Main St, City, Country",
                          "vfp": false
                        }
                        """)
                .addHeader("Content-Type", "application/json")
                .setResponseCode(HttpStatus.OK.value()));

        // Call the method
        String response = customerCommands.registerClient("Doe", "John", "john.doe@example.com", "123 Main St, City, Country");

        // Assert that the response matches the registered customer's details
        assertEquals("User registered successfully", response);

        // Verify the request made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/customers", recordedRequest.getPath());
        assertEquals("POST", recordedRequest.getMethod());
    }
}
