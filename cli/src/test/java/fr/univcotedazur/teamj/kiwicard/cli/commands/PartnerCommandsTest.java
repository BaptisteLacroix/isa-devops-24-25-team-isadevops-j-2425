package fr.univcotedazur.teamj.kiwicard.cli.commands;

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

    private PartnerCommands catalog;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void init() {
        catalog = new PartnerCommands(WebClient.create(mockWebServer.url("/").toString()));
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

        String retrievedPartnerList = catalog.partners();

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

        String retrievedItemList = catalog.partneritems(1);

        assertEquals(3, retrievedItemList.split("\n").length);

        // Verify the request was made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/partners/1/items", recordedRequest.getPath());
        assertEquals("GET", recordedRequest.getMethod());
    }
}
