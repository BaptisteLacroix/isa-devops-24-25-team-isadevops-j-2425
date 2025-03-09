package fr.univcotedazur.teamj.kiwicard.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.dto.ErrorDTO;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.NPurchasedMGiftedPerkDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.TimedDiscountInPercentPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.NoCartException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPerkManager;
import fr.univcotedazur.teamj.kiwicard.components.PerksService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PerksControllerWebMvcTest extends BaseUnitTest {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IPerkManager perksManager;

    @MockitoBean
    private PerksService perksService;

    private IPerkDTO perk1;
    private IPerkDTO perk2;

    @BeforeEach
    void setUp() {
        perk1 = new NPurchasedMGiftedPerkDTO(1L, 3, new ItemDTO("Pain au chocolat", 1.5), 1);
        perk2 = new TimedDiscountInPercentPerkDTO(2L, LocalTime.now().minusMinutes(10), 30);
    }

    @Test
    void getPerkByIdOK() throws Exception {
        when(perksManager.findPerkById(1L)).thenReturn(perk1);
        mockMvc.perform(get(PerksController.BASE_URI + "/1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.perkId").value(perk1.perkId()));
    }

    @Test
    void getPerkByIdNotFound() throws Exception {
        when(perksManager.findPerkById(999L)).thenThrow(new UnknownPerkIdException(999L));
        MvcResult result = mockMvc.perform(get(PerksController.BASE_URI + "/999")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        ErrorDTO errorDTO = OBJECT_MAPPER.readValue(jsonResult, ErrorDTO.class);
        assertEquals("Unknown perk id: 999", errorDTO.errorMessage());
    }

    @Test
    void listAllPerks() throws Exception {
        when(perksManager.findAllPerks()).thenReturn(List.of(perk1, perk2));
        mockMvc.perform(get(PerksController.BASE_URI)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].perkId").value(perk1.perkId()))
                .andExpect(jsonPath("$[1].perkId").value(perk2.perkId()));
    }

    @Test
    void applyPerkOK() throws Exception {
        when(perksService.applyPerk(1L, "client@example.com")).thenReturn(true);
        PerksController.ApplyPerkRequest client = new PerksController.ApplyPerkRequest("client@example.com");
        mockMvc.perform(post(PerksController.BASE_URI + "/1/apply")
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(client)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void applyPerkNotFound() throws Exception {
        when(perksService.applyPerk(999L, "client@example.com")).thenThrow(new UnknownPerkIdException(999L));
        PerksController.ApplyPerkRequest client = new PerksController.ApplyPerkRequest("client@example.com");
        MvcResult result = mockMvc.perform(post(PerksController.BASE_URI + "/999/apply")
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(client)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        ErrorDTO errorDTO = OBJECT_MAPPER.readValue(jsonResult, ErrorDTO.class);
        assertEquals("Unknown perk id: 999", errorDTO.errorMessage());
    }

    @Test
    void applyPerkUnknownCustomer() throws Exception {
        when(perksService.applyPerk(1L, "unknown@example.com"))
                .thenThrow(new UnknownCustomerEmailException("unknown@example.com"));
        PerksController.ApplyPerkRequest client = new PerksController.ApplyPerkRequest("unknown@example.com");
        MvcResult result = mockMvc.perform(post(PerksController.BASE_URI + "/1/apply")
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(client)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        ErrorDTO errorDTO = OBJECT_MAPPER.readValue(jsonResult, ErrorDTO.class);
        assertEquals("Unknown customer email: unknown@example.com", errorDTO.errorMessage());
    }

    @Test
    void findConsumablePerksForConsumerAtPartnerOK() throws Exception {
        when(perksService.findConsumablePerksForConsumerAtPartner("client@example.com"))
                .thenReturn(List.of(perk1));
        mockMvc.perform(get(PerksController.BASE_URI + "/consumable")
                        .param("consumerEmail", "client@example.com")
                        .param("partnerId", "1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].perkId").value(perk1.perkId()));
    }

    @Test
    void findConsumablePerksForConsumerAtPartnerUnknownCustomer() throws Exception {
        when(perksService.findConsumablePerksForConsumerAtPartner("unknown@example.com"))
                .thenThrow(new UnknownCustomerEmailException("unknown@example.com"));
        mockMvc.perform(get(PerksController.BASE_URI + "/consumable")
                        .param("consumerEmail", "unknown@example.com")
                        .param("partnerId", "1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void findConsumablePerksForConsumerAtPartnerNoCart() throws Exception {
        String consumerEmail = "client@example.com";
        when(perksService.findConsumablePerksForConsumerAtPartner(consumerEmail))
                .thenThrow(new NoCartException(consumerEmail));
        mockMvc.perform(get(PerksController.BASE_URI + "/consumable")
                        .param("consumerEmail", consumerEmail)
                        .param("partnerId", "1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

}
