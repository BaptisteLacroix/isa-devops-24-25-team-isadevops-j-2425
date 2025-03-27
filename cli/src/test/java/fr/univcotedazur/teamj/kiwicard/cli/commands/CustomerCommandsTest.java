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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        String mail = "john.doe@example.com";
        String response = customerCommands.registerClient("Doe", "John", mail, "123 Main St, City, Country");

        // Assert that the response matches the registered customer's details
        assertEquals("Client enregistré avec succès. Vous êtes maintenant connecté en tant que : " + mail, response);

        // Verify the request made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/customers", recordedRequest.getPath());
        assertEquals("POST", recordedRequest.getMethod());
    }

    @Test
    void payCartTest() throws InterruptedException {
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

        String result = customerCommands.payCart("doesnotmatter@yahoo.fr");

        // Verify the request made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/cart/doesnotmatter@yahoo.fr/validate", recordedRequest.getPath());
        assertEquals("POST", recordedRequest.getMethod());

        assertNotNull(result);
    }

    @Test
    void getCartTest() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                          "cartId": 12,
                          "partner": {
                            "id": 2,
                            "name": "Fleuriste",
                            "address": "13 rue des roses, Lorgues"
                          },
                          "items": [
                            {
                              "quantity": 2,
                              "startTime": null,
                              "endTime": null,
                              "item": {
                                "itemId": 5,
                                "label": "rose",
                                "price": 1.0
                              }
                            }
                          ],
                          "perksList": []
                        }
                        
                        """).addHeader("Content-Type", "application/json"));

        String result = customerCommands.getCart("doesnotmatter@yahoo.fr");

        // Verify the request made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/cart/doesnotmatter@yahoo.fr", recordedRequest.getPath());
        assertEquals("GET", recordedRequest.getMethod());

        assertNotNull(result);
    }

    @Test
    void addItemToCartTest() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                            "cartId": 11,
                            "partner": {
                              "id": 1,
                              "name": "Boulange",
                              "address": "14 rue du paindemie, Draguignan"
                            },
                            "items": [
                              {
                                "quantity": 9,
                                "item": {
                                  "itemId": 1,
                                  "label": "croissant",
                                  "price": 1.0
                                }
                              }
                            ],
                            "perksList": []
                          }
                        """)
                .addHeader("Content-Type", "application/json"));

        String customerEmail = "customer@example.com";

        customerCommands.addItemToCart(customerEmail, 1L, 2);

        // Verify that the request was made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/cart/" + customerEmail, recordedRequest.getPath());
        assertEquals("PUT", recordedRequest.getMethod());
    }

    @Test
    void addReservationToCartTest() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                            "cartId": 1,
                            "partner": {
                              "id": 6,
                              "name": "HappyKids",
                              "address": "1 rue des enfants, Nice"
                            },
                            "items": [
                              {
                                "quantity": 1,
                                "item": {
                                  "itemId": 21,
                                  "label": "Heure de garde HappyKids",
                                  "price": 1.0
                                }
                              }
                            ],
                            "perksList": []
                          }
                        """)
                .addHeader("Content-Type", "application/json"));

        String customerEmail = "customer@example.com";

        customerCommands.reserveTimeSlot(customerEmail, 1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), 1);

        // Verify that the request was made to the correct endpoint
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/cart/" + customerEmail, recordedRequest.getPath());
        assertEquals("PUT", recordedRequest.getMethod());
    }
}
