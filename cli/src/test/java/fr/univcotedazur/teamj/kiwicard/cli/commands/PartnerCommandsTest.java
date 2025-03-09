package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.constants.Constants;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PartnerCommandsTest {

    private PartnerCommands commands;

    private static MockWebServer mockWebServer;
    private static CliSession cliSession;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        cliSession = new CliSession();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void init() {
        commands = new PartnerCommands(WebClient.create(mockWebServer.url("/").toString()), cliSession);
    }

    @Test
    void partnersTest() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        [
                          {
                            "id": 1,
                            "name": "Boulange",
                            "address": "3 Bread Street"
                          },
                          {
                            "id": 2,
                            "name": "Charcut",
                            "address": "2 Meat Street"
                          }
                        ]
                        """).addHeader("Content-Type", "application/json"));

        String retrievedPartnerList = commands.partners();

        assertEquals(2, retrievedPartnerList.split("\n").length);

        // Verify the request was made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/partners", recordedRequest.getPath());
        assertEquals("GET", recordedRequest.getMethod());
    }

    @Test
    void partnerItemsTest() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        [
                            {
                              "id": 1,
                              "label": "choc bread",
                              "price": 9.99
                            },
                            {
                              "id": 3,
                              "label": "cwoissante",
                              "price": 5.99
                            },
                            {
                              "id": 6,
                              "label": "chocolatine",
                              "price": 0.99
                            }
                        ]
                        """)
                .addHeader("Content-Type", "application/json"));

        String retrievedItemList = commands.partnerItems("1");

        assertEquals(3, retrievedItemList.split("\n").length);

        // Verify the request was made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/partners/1/items", recordedRequest.getPath());
        assertEquals("GET", recordedRequest.getMethod());
    }

    @Test
    void partnerItemsLoggedInPartnerTest() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        [
                            {
                              "id": 1,
                              "label": "choc bread",
                              "price": 9.99
                            },
                            {
                              "id": 3,
                              "label": "cwoissante",
                              "price": 5.99
                            },
                            {
                              "id": 6,
                              "label": "chocolatine",
                              "price": 0.99
                            }
                        ]
                        """)
                .addHeader("Content-Type", "application/json"));
        cliSession.logIn(1);
        String retrievedItemList = commands.partnerItems(Constants.LOGGED_IN_ID_PLACEHOLDER);

        assertEquals(3, retrievedItemList.split("\n").length);

        // Verify the request was made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/partners/1/items", recordedRequest.getPath());
        assertEquals("GET", recordedRequest.getMethod());
    }

    @Test
    void consultPartnerPerksTest() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        [
                            {
                                "perkId": 1,
                                "description": "10% off on all products"
                            },
                            {
                                "perkId": 2,
                                "description": "Free shipping on orders over $50"
                            }
                        ]
                        """)
                .addHeader("Content-Type", "application/json"));

        // Call the command to retrieve perks for partner with ID 12345
        commands.consultPartnerPerks("12345");

        // Assert that the request was made to the correct URL
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/partners/12345/perks", recordedRequest.getPath());
        assertEquals("GET", recordedRequest.getMethod());
    }
}
