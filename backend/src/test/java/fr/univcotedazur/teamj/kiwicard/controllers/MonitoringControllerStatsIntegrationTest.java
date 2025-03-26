package fr.univcotedazur.teamj.kiwicard.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MonitoringControllerStatsIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Test
    void testComparePurchasesValidRequest() throws Exception {
        long partnerId = 4L; // partnerPoissonnerie id as an example
        LocalDate day1 = LocalDate.of(2025, 3, 16);
        LocalDate day2 = LocalDate.of(2025, 4, 16);
        Duration duration = Duration.ofHours(1);

        String responseContent = mockMvc.perform(get("/monitoring/stats/{partnerId}/comparePurchases", partnerId)
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

    @Test
    void testComparePurchasesValidRequestBadDay() throws Exception {
        long partnerId = 4L; // partnerPoissonnerie id as an example
        LocalDate day1 = LocalDate.of(2025, 3, 17);
        LocalDate day2 = LocalDate.of(2025, 4, 17);
        Duration duration = Duration.ofHours(1);

        String responseContent = mockMvc.perform(get("/monitoring/stats/{partnerId}/comparePurchases", partnerId)
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

    @Test
    void testComparePurchasesBadDuration() throws Exception {
        long partnerId = 4L; // partnerPoissonnerie id as an example
        LocalDate day1 = LocalDate.of(2025, 3, 16);
        LocalDate day2 = LocalDate.of(2025, 4, 16);
        Duration duration = Duration.ofDays(2);

        mockMvc.perform(get("/monitoring/stats/{partnerId}/comparePurchases", partnerId)
                        .param("day1", day1.format(dateFormatter))
                        .param("day2", day2.format(dateFormatter))
                        .param("duration", duration.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Duration is expected to be less than a day, got" + duration));

    }

    @Test
    void testComparePurchasesBadPartnerId() throws Exception {
        long partnerId = 4712626265L; // bad id
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
