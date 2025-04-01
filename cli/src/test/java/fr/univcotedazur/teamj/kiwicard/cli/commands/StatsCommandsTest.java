package fr.univcotedazur.teamj.kiwicard.cli.commands;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliTwoDaysAggregation;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class StatsCommandsTest {

    private StatsCommands commands;
    private static MockWebServer mockWebServer;
    private static CliSession cliSession;
    private ObjectMapper objectMapper = new ObjectMapper();


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
        commands = new StatsCommands(WebClient.create(mockWebServer.url("/").toString()), cliSession);
    }

    @Test
    void AggregationTest() throws Exception {
        LocalDate day1 = LocalDate.of(2025, 3, 16);
        LocalDate day2 = LocalDate.of(2025, 4, 16);
        String body;
        {
            body = """
                    {
                      "day1Aggregation" : {
                        "01:00" : 3,
                        "02:00" : 2,
                        "03:00" : 2,
                        "04:00" : 2,
                        "05:00" : 2,
                        "06:00" : 2,
                        "07:00" : 6,
                        "08:00" : 10,
                        "09:00" : 8,
                        "10:00" : 7,
                        "11:00" : 5,
                        "12:00" : 4,
                        "13:00" : 4,
                        "14:00" : 6,
                        "15:00" : 8,
                        "16:00" : 11,
                        "17:00" : 22,
                        "18:00" : 30,
                        "19:00" : 15,
                        "20:00" : 0,
                        "21:00" : 0,
                        "22:00" : 0,
                        "23:00" : 0,
                        "23:59:59" : 0
                      },
                      "day2Aggregation" : {
                        "01:00" : 3,
                        "02:00" : 2,
                        "03:00" : 2,
                        "04:00" : 2,
                        "05:00" : 2,
                        "06:00" : 2,
                        "07:00" : 6,
                        "08:00" : 10,
                        "09:00" : 8,
                        "10:00" : 7,
                        "11:00" : 5,
                        "12:00" : 4,
                        "13:00" : 4,
                        "14:00" : 6,
                        "15:00" : 8,
                        "16:00" : 11,
                        "17:00" : 22,
                        "18:00" : 30,
                        "19:00" : 15,
                        "20:00" : 0,
                        "21:00" : 0,
                        "22:00" : 0,
                        "23:00" : 0,
                        "23:59:59" : 0
                      }
                    """;
        }
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json")
                .setResponseCode(HttpStatus.OK.value()));


        var formatter = commands.dateFormatter;
        String result = commands.aggregatePurchases(
                "4",
                formatter.format(day1),
                formatter.format(day2),
                "60"
        );

        // Verify the request was made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        HttpUrl requestURL = recordedRequest.getRequestUrl();
        assertNotNull(requestURL);
        assertEquals("/stats/4/comparePurchases", requestURL.encodedPath());
        assertEquals(commands.dateFormatter.format(day1), requestURL.queryParameter("day1"));
        assertEquals(commands.dateFormatter.format(day2), requestURL.queryParameter("day2"));
        var splitted = recordedRequest.getPath().split("/");
        assertEquals("4", splitted[splitted.length - 2]);
        assertEquals("60", requestURL.queryParameter("duration"));
        assertEquals("GET", recordedRequest.getMethod());
    }

    @Test
    void perkAggregationTest() throws Exception {
        String body = """
                {
                     "NPurchasedMGiftedPerk" : 24,
                     "VfpDiscountInPercentPerk" : 13,
                     "TimedDiscountInPercentPerk" : 45
                   }
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json")
                .setResponseCode(HttpStatus.OK.value()));

        String result = commands.aggregatePerks("1");

        // Verify the request was made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        HttpUrl requestURL = recordedRequest.getRequestUrl();
        assertNotNull(requestURL);
        assertEquals("/stats/1/nb-perks-by-type", requestURL.encodedPath());
        assertEquals("GET", recordedRequest.getMethod());
    }
}
