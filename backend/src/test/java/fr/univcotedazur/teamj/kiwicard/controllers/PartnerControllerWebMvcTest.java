package fr.univcotedazur.teamj.kiwicard.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.dto.ErrorDTO;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PartnerControllerWebMvcTest extends BaseUnitTest {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IPartnerManager partnerManager;

    private PartnerDTO chezJohn;
    private PartnerDTO chezPaul;
    private Item painAuChocolat;
    private Item croissant;
    private ItemDTO chocolatineDTO;
    private PerkDTO perk1;
    private PerkDTO perk2;

    @BeforeEach
    void setUp() {
        chezJohn = new PartnerDTO(1, "Chez John", " 2 boulevard Wilson");
        chezPaul = new PartnerDTO(2, "Chez Paul", "3 rue de la Paix");
        painAuChocolat = Item.createTestItem(1, "Pain au chocolat", 1.5);
        croissant = Item.createTestItem(2, "Croissant", 1.2);
        chocolatineDTO = new ItemDTO("Chocolatine", 1.8);
        perk1 = new PerkDTO(1, "Perk 1 description");
        perk2 = new PerkDTO(2, "Perk 2 description");
    }

    @Test
    void createPartner() throws Exception {
        PartnerCreationDTO partnerToCreate = new PartnerCreationDTO("Chez John", "2 boulevard Wilson");
        PartnerDTO partnerCreated = new PartnerDTO(1, "Chez John", "2 boulevard Wilson");
        when(partnerManager.createPartner(partnerToCreate)).thenReturn(partnerCreated);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(PartnerController.BASE_URI)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(partnerToCreate)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        PartnerDTO partnerResult = OBJECT_MAPPER.readValue(jsonResult, PartnerDTO.class);
        assertEquals(partnerCreated.id(), partnerResult.id());
        assertEquals(partnerCreated.name(), partnerResult.name());
        assertEquals(partnerCreated.address(), partnerResult.address());
    }

    @Test
    void getPartnerByIdOK() throws Exception {
        when(partnerManager.findPartnerById(1)).thenReturn(chezJohn);
        MvcResult result = mockMvc.perform(get(PartnerController.BASE_URI + "/1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        PartnerDTO partnerResult = OBJECT_MAPPER.readValue(jsonResult, PartnerDTO.class);
        assertEquals(chezJohn.id(), partnerResult.id());
        assertEquals(chezJohn.name(), partnerResult.name());
        assertEquals(chezJohn.address(), partnerResult.address());

    }

    @Test
    void getPartnerByIdNotFound() throws Exception {
        when(partnerManager.findPartnerById(2)).thenThrow(new UnknownPartnerIdException("Partner with id 2 not found"));
        mockMvc.perform(get(PartnerController.BASE_URI + "/2")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void listAllPartners() throws Exception {
        when(partnerManager.findAllPartner()).thenReturn(List.of(chezJohn, chezPaul));
        mockMvc.perform(get(PartnerController.BASE_URI)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(chezJohn.id()))
                .andExpect(jsonPath("$[0].name").value(chezJohn.name()))
                .andExpect(jsonPath("$[0].address").value(chezJohn.address()))
                .andExpect(jsonPath("$[1].id").value(chezPaul.id()))
                .andExpect(jsonPath("$[1].name").value(chezPaul.name()))
                .andExpect(jsonPath("$[1].address").value(chezPaul.address()));
    }

    @Test
    void addItemToPartnerCatalogOK() throws Exception {
        mockMvc.perform(patch(PartnerController.BASE_URI + "/1/add-item")
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(chocolatineDTO)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void addItemToPartnerCatalogPartnerNotAdded() throws Exception {
        doThrow(new UnknownPartnerIdException("Partner with id 2 not found")).when(partnerManager).addItemToPartnerCatalog(2, chocolatineDTO);
        MvcResult result = mockMvc.perform(patch(PartnerController.BASE_URI + "/2/add-item")
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(chocolatineDTO)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        ErrorDTO errorDTOResult = OBJECT_MAPPER.readValue(jsonResult, ErrorDTO.class);
        assertEquals("Partner with id 2 not found", errorDTOResult.errorMessage());
    }

    @Test
    void removeItemFromPartnerCatalogOK() throws Exception {
        when(partnerManager.removeItemFromPartnerCatalog(1, 1)).thenReturn(true);
        mockMvc.perform(patch(PartnerController.BASE_URI + "/1/remove-item/1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void removeItemFromPartnerCatalogPartnerNotRemoved() throws Exception {
        when(partnerManager.removeItemFromPartnerCatalog(2, 1)).thenThrow(new UnknownPartnerIdException("Partner with id 2 not found"));
        MvcResult result = mockMvc.perform(patch(PartnerController.BASE_URI + "/2/remove-item/1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        ErrorDTO errorDTOResult = OBJECT_MAPPER.readValue(jsonResult, ErrorDTO.class);
        assertEquals("Partner with id 2 not found", errorDTOResult.errorMessage());
    }

    @Test
    void removeItemFromPartnerCatalogItemNotRemoved() throws Exception {
        when(partnerManager.removeItemFromPartnerCatalog(1, 3)).thenThrow(new UnknownPartnerIdException("Item with id 3 not found in partner " + chezJohn.name() + " catalog"));
        MvcResult result = mockMvc.perform(patch(PartnerController.BASE_URI + "/1/remove-item/3")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        ErrorDTO errorDTOResult = OBJECT_MAPPER.readValue(jsonResult, ErrorDTO.class);
        assertEquals("Item with id 3 not found in partner " + chezJohn.name() + " catalog", errorDTOResult.errorMessage());
    }

    @Test
    void listAllItemsFromPartnerOK() throws Exception {
        when(partnerManager.findAllPartnerItems(1)).thenReturn(List.of(painAuChocolat, croissant));
        mockMvc.perform(get(PartnerController.BASE_URI + "/1/items")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].itemId").value(painAuChocolat.getItemId()))
                .andExpect(jsonPath("$[0].label").value(painAuChocolat.getLabel()))
                .andExpect(jsonPath("$[0].price").value(painAuChocolat.getPrice()))
                .andExpect(jsonPath("$[1].itemId").value(croissant.getItemId()))
                .andExpect(jsonPath("$[1].label").value(croissant.getLabel()))
                .andExpect(jsonPath("$[1].price").value(croissant.getPrice()));
    }

    @Test
    void listAllItemsFromPartnerNotFound() throws Exception {
        when(partnerManager.findAllPartnerItems(2)).thenThrow(new UnknownPartnerIdException("Partner with id 2 not found"));
        mockMvc.perform(get(PartnerController.BASE_URI + "/2/items")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void listAllPerksFromPartnerOK() throws Exception {
        when(partnerManager.findAllPartnerPerks(1)).thenReturn(List.of(perk1, perk2));
        mockMvc.perform(get(PartnerController.BASE_URI + "/1/perks")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].perkId").value(perk1.perkId()))
                .andExpect(jsonPath("$[0].description").value(perk1.description()))
                .andExpect(jsonPath("$[1].perkId").value(perk2.perkId()))
                .andExpect(jsonPath("$[1].description").value(perk2.description()));
    }

    @Test
    void listAllPerksFromPartnerNotFound() throws Exception {
        when(partnerManager.findAllPartnerPerks(2)).thenThrow(new UnknownPartnerIdException("Partner with id 2 not found"));
        mockMvc.perform(get(PartnerController.BASE_URI + "/2/perks")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }
}
