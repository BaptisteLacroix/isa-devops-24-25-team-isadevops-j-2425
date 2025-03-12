package fr.univcotedazur.teamj.kiwicard.cli.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliCart;
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
    private static ObjectMapper mapper = new ObjectMapper();
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
        // Call the method
        String mail = "john.doe@example.com";
        String response = customerCommands.registerClient("Doe", "John", mail, "123 Main St, City, Country");

        // Assert that the response matches the registered customer's details
        assertEquals("Register client successfuly, you are now logged in as "+ mail, response);

        // Verify the request made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/customers", recordedRequest.getPath());
        assertEquals("POST", recordedRequest.getMethod());
    }

    @Test
    void payCommandTest() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                          "cartOwnerEmail" : "test@customer.com",
                          "cartDTO" : {
                            "cartId" : 100,
                            "partner" : {
                              "id" : 1,
                              "name" : "Antoine Le Fadda",
                              "address" : "Draguignangz"
                            },
                            "items" : [ {
                              "quantity" : 2,
                              "startTime" : null,
                              "endTime" : null,
                              "itemId" : 10
                            } ],
                            "perksList" : [ ]
                          },
                          "paymentDTO" : {
                            "cardNumber" : "1234-5678-9012-3456",
                            "amount" : 23.0,
                            "authorized" : true
                          }
                        }
                        """).addHeader("Content-Type", "application/json"));

        String result = customerCommands.payCommand("doesnotmatter@yahoo.fr");
        assertEquals("""
            Cart was purchased successfully, purchase details :\s
            \towner email : test@customer.com
            \tcart : CliCart[cartId=100, partner=• 1 : Antoine Le Fadda au Draguignangz, perksList=[], items=[• 0 : no label		0.0€]]
            \tpayment : cardNumber='1234-5678-9012-3456', amount=23.0, authorized=true}""",
            result
        );
    }
}
