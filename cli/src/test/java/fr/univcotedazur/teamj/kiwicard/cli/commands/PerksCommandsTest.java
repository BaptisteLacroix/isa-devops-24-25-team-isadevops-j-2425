package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PerksCommandsTest {

    private PerksCommands commands;
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
        commands = new PerksCommands(WebClient.create(mockWebServer.url("/").toString()), cliSession);
    }

    @Test
    void listPerksSuccessTest() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        [
                            {"id": 1, "name": "Perk1"},
                            {"id": 2, "name": "Perk2"}
                        ]
                        """)
                .addHeader("Content-Type", "application/json"));

        String result = commands.listPerks("client@example.com");

        assertEquals(2, result.split("\n").length);

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/perks/consumable?consumerEmail=client@example.com", request.getPath());
        assertEquals("GET", request.getMethod());
    }

    @Test
    void listPerksErrorTest() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("""
                        {"errorMessage": "Client non trouvé"}
                        """)
                .addHeader("Content-Type", "application/json"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                commands.listPerks("inconnu@example.com")
        );

        assertEquals("Client non trouvé", thrown.getMessage());

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/perks/consumable?consumerEmail=inconnu@example.com", request.getPath());
        assertEquals("GET", request.getMethod());
    }
}
