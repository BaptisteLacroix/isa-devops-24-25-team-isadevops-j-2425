package fr.univcotedazur.teamj.kiwicard.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.univcotedazur.teamj.kiwicard.DataUtils;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static fr.univcotedazur.teamj.kiwicard.DateUtils.getLocalDateTimes;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MonitoringControllerStatsIntegrationTest {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private EntityManager entityManager;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Partner partner;

    @Autowired
    public MonitoringControllerStatsIntegrationTest(EntityManager entityManager, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.entityManager = entityManager;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }


    @Transactional
    @BeforeEach
    public void setup() {
        partner = new Partner(
                "Poissonnerie",
                "11 rue des poissons, Saint-Tropez"
        );

        entityManager.persist(partner);

        LocalDate day1 = LocalDate.of(2025, 3, 16);
        LocalDate day2 = LocalDate.of(2025, 4, 16);
        // Purchases
        DataUtils dataUtils = new DataUtils(entityManager);
        dataUtils.createDummyPurchasesForDate(day1, getLocalDateTimes(day1, Duration.ofHours(1)), partner);
        dataUtils.createDummyPurchasesForDate(day2, getLocalDateTimes(day2, Duration.ofHours(1)), partner);
    }


    @Transactional
    @Test
    void testComparePurchasesValidRequest() throws Exception {
        LocalDate day1 = LocalDate.of(2025, 3, 16);
        LocalDate day2 = LocalDate.of(2025, 4, 16);
        Duration duration = Duration.ofHours(1);

        String responseContent = mockMvc.perform(get("/monitoring/stats/{partnerId}/comparePurchases", partner.getPartnerId())
                        .param("day1", day1.format(dateFormatter))
                        .param("day2", day2.format(dateFormatter))
                        .param("duration", duration.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        objectMapper.registerModule(new JavaTimeModule());
        Map<String, Map<LocalTime, Integer>> result = objectMapper.readValue(responseContent, new TypeReference<>() {});
        for (var map : result.values()) {
            assertTrue(map.values().stream().anyMatch(e-> e != 0)); // assert at least one element is not 0
        }
    }

    @Transactional
    @Test
    void testComparePurchasesValidRequestBadDay() throws Exception {
        LocalDate day1 = LocalDate.of(2025, 3, 17);
        LocalDate day2 = LocalDate.of(2025, 4, 17);
        Duration duration = Duration.ofHours(1);

        String responseContent = mockMvc.perform(get("/monitoring/stats/{partnerId}/comparePurchases", partner.getPartnerId())
                        .param("day1", day1.format(dateFormatter))
                        .param("day2", day2.format(dateFormatter))
                        .param("duration", duration.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        objectMapper.registerModule(new JavaTimeModule());
        Map<String, Map<LocalTime, Integer>> result = objectMapper.readValue(responseContent, new TypeReference<>() {});
        for (var map : result.values()) {
            assertTrue(map.values().stream().allMatch(e-> e == 0)); // assert all elements are 0
        }
    }

    @Transactional
    @Test
    void testComparePurchasesBadDuration() throws Exception {
        long partnerId = 4L; // partnerPoissonnerie id as an example
        LocalDate day1 = LocalDate.of(2025, 3, 16);
        LocalDate day2 = LocalDate.of(2025, 4, 16);
        Duration duration = Duration.ofDays(2);

        mockMvc.perform(get("/monitoring/stats/{partnerId}/comparePurchases", partner.getPartnerId())
                        .param("day1", day1.format(dateFormatter))
                        .param("day2", day2.format(dateFormatter))
                        .param("duration", duration.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Duration is expected to be less than a day, got" + duration));

    }

    @Transactional
    @Test
    void testComparePurchasesBadPartnerId() throws Exception {
        long partnerId = partner.getPartnerId() +1; // bad id
        LocalDate day1 = LocalDate.of(2025, 3, 16);
        LocalDate day2 = LocalDate.of(2025, 4, 16);
        Duration duration = Duration.ofHours(1);

        mockMvc.perform(get("/monitoring/stats/{partnerId}/comparePurchases", partnerId)
                        .param("day1", day1.format(dateFormatter))
                        .param("day2", day2.format(dateFormatter))
                        .param("duration", duration.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Partner id : " + partnerId + "does not exist"));
    }
}
