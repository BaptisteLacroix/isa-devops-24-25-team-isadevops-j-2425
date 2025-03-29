package fr.univcotedazur.teamj.kiwicard.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.univcotedazur.teamj.kiwicard.PurchaseCreationUtils;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.DataUtils;
import fr.univcotedazur.teamj.kiwicard.dto.ErrorDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.NPurchasedMGiftedPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.TimedDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.VfpDiscountInPercentPerk;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.univcotedazur.teamj.kiwicard.DateUtils.getLocalDateTimes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MonitoringControllerStatsIntegrationTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private EntityManager entityManager;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Partner partner;
    private Map<AbstractPerk, Integer> goodPerksToUse;

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
        LocalDate day3 = LocalDate.of(2025, 5, 16);


        // Perks
        var item = new Item("unused", 0);
        entityManager.persist(item);
        VfpDiscountInPercentPerk perk1 = new VfpDiscountInPercentPerk(0.05, LocalTime.of(8, 0), LocalTime.of(12, 0));
        TimedDiscountInPercentPerk perk2 = new TimedDiscountInPercentPerk(LocalTime.now(), 20);
        NPurchasedMGiftedPerk perk3 = new NPurchasedMGiftedPerk(0, 0, item);
        entityManager.persist(perk1);
        entityManager.persist(perk2);
        entityManager.persist(perk3);

        goodPerksToUse = new HashMap<>(){{
           put(perk1, 13);
           put(perk2, 45);
           put(perk3, 24);
        }};



        new PurchaseCreationUtils(entityManager, 150).createDummyPurchasesForDate(day1, getLocalDateTimes(day1, Duration.ofHours(1)), partner, goodPerksToUse);
        new PurchaseCreationUtils(entityManager, 150).createDummyPurchasesForDate(day2, getLocalDateTimes(day2, Duration.ofHours(1)), partner);

        Partner badPartner = new Partner("does not", "matter");
        entityManager.persist(badPartner);
        new PurchaseCreationUtils(entityManager, 150).createDummyPurchasesForDate(
                day3,
                getLocalDateTimes(day3, Duration.ofHours(1)),
                badPartner,
                new HashMap<>(){{
                    put(perk1, 50);
                    put(perk2, 50);
                    put(perk3, 40);
                }}
        );
    }


    @Transactional
    @Test
    void testComparePurchasesValidRequest() throws Exception {
        LocalDate day1 = LocalDate.of(2025, 3, 16);
        LocalDate day2 = LocalDate.of(2025, 4, 16);
        Duration duration = Duration.ofHours(1);

        String responseContent = mockMvc.perform(get("/monitoring/stats/{partnerId}/compare-purchases", partner.getPartnerId())
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

        String responseContent = mockMvc.perform(get("/monitoring/stats/{partnerId}/compare-purchases", partner.getPartnerId())
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

        MvcResult result = mockMvc.perform(get("/monitoring/stats/{partnerId}/compare-purchases", partner.getPartnerId())
                        .param("day1", day1.format(dateFormatter))
                        .param("day2", day2.format(dateFormatter))
                        .param("duration", duration.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ErrorDTO errorDTOResult = OBJECT_MAPPER.readValue(jsonResult, ErrorDTO.class);
        assertEquals("Duration is expected to be less than a day, got " + duration, errorDTOResult.errorMessage());
    }

    @Transactional
    @Test
    void testComparePurchasesBadPartnerId() throws Exception {
        long partnerId = partner.getPartnerId() +154; // bad id
        LocalDate day1 = LocalDate.of(2025, 3, 16);
        LocalDate day2 = LocalDate.of(2025, 4, 16);
        Duration duration = Duration.ofHours(1);

        MvcResult result = mockMvc.perform(get("/monitoring/stats/{partnerId}/compare-purchases", partnerId)
                        .param("day1", day1.format(dateFormatter))
                        .param("day2", day2.format(dateFormatter))
                        .param("duration", duration.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ErrorDTO errorDTOResult = OBJECT_MAPPER.readValue(jsonResult, ErrorDTO.class);
        assertEquals("Partner with id " + partnerId + " not found", errorDTOResult.errorMessage());
    }

    @Transactional
    @Test
    void testAggregatePartnerPerksUsageByType() throws Exception {
        String response = mockMvc.perform(get("/monitoring/stats/{partnerId}/nb-perks-by-type", partner.getPartnerId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        HashMap<String, Integer> aggregation = objectMapper.readValue(response, new TypeReference<>() {});

        assertEquals(
                this.goodPerksToUse.entrySet().stream()
                        .map(entry->new AbstractMap.SimpleEntry<>(
                                entry.getKey().getClass().getSimpleName(),
                                entry.getValue()
                                ))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                aggregation
        );
    }
}
