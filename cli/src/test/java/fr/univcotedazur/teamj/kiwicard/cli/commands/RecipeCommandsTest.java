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
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecipeCommandsTest {


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

    @Test
    void recipesSetTest() throws Exception {
        // Given
        mockWebServer.enqueue(new MockResponse()
                .setBody("[\"CHOCOLALALA\",\"DARK_TEMPTATION\",\"SOO_CHOCOLATE\"]")
                .addHeader("Content-Type", "application/json"));

        // When-Then

        // Verify the request was made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/recipes", recordedRequest.getPath());
        assertEquals("GET", recordedRequest.getMethod());
    }

}
