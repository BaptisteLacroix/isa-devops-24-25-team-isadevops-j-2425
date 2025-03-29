package fr.univcotedazur.teamj.kiwicard.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.controllers.PartnerController;
import fr.univcotedazur.teamj.kiwicard.dto.ErrorDTO;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.entities.perks.VfpDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.repositories.IPartnerRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPerkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PartnerControllerIT extends BaseUnitTest {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IPartnerRepository partnerRepository;
    @Autowired
    private IPerkRepository perkRepository;

    private PartnerCreationDTO chezJohnCreationDTO;
    private PartnerCreationDTO chezPaulCreationDTO;
    private ItemDTO croissantDTO;
    private ItemDTO painAuChocolatDTO;

    @BeforeEach
    void setUp() {
        chezJohnCreationDTO = new PartnerCreationDTO("Chez John", " 2 boulevard Wilson");
        chezPaulCreationDTO = new PartnerCreationDTO("Chez Paul", "3 rue de la Paix");
        croissantDTO = new ItemDTO(1, "Croissant", 1.2);
        painAuChocolatDTO = new ItemDTO(2, "Pain au chocolat", 1.8);

        partnerRepository.deleteAll();
    }

    @Test
    void createPartner() throws Exception {
        PartnerDTO partnerCreated = new PartnerDTO(-1, chezJohnCreationDTO.name(), chezJohnCreationDTO.address());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(PartnerController.BASE_URI)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(chezJohnCreationDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        PartnerDTO partnerResult = OBJECT_MAPPER.readValue(jsonResult, PartnerDTO.class);
        assertEquals(partnerCreated.name(), partnerResult.name());
        assertEquals(partnerCreated.address(), partnerResult.address());
    }

    @Test
    void getPartnerByIdOK() throws Exception {
        PartnerCreationDTO partnerToCreate = new PartnerCreationDTO("Chez Jack ", "2 rue de la Paix");
        Partner partner = new Partner(partnerToCreate);
        partnerRepository.save(partner);
        long partnerId = partner.getPartnerId();

        MvcResult result = mockMvc.perform(get(PartnerController.BASE_URI + "/" + partnerId)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        PartnerDTO partnerResult = OBJECT_MAPPER.readValue(jsonResult, PartnerDTO.class);
        assertEquals(partnerToCreate.name(), partnerResult.name());
        assertEquals(partnerToCreate.address(), partnerResult.address());

    }

    @Test
    void getPartnerByIdNotFound() throws Exception {
//        when(partnerManager.findPartnerById(2)).thenThrow(new UnknownPartnerIdException(2));
        mockMvc.perform(get(PartnerController.BASE_URI + "/2")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void listAllPartners() throws Exception {
        partnerRepository.save(new Partner(chezJohnCreationDTO));
        partnerRepository.save(new Partner(chezPaulCreationDTO));

        mockMvc.perform(get(PartnerController.BASE_URI)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value(chezJohnCreationDTO.name()))
                .andExpect(jsonPath("$[0].address").value(chezJohnCreationDTO.address()))
                .andExpect(jsonPath("$[1].name").value(chezPaulCreationDTO.name()))
                .andExpect(jsonPath("$[1].address").value(chezPaulCreationDTO.address()));
    }

    @Test
    @Transactional
    void addItemToPartnerCatalogOK() throws Exception {
        Partner partner = new Partner(chezJohnCreationDTO);
        partnerRepository.save(partner);
        long partnerId = partner.getPartnerId();

        mockMvc.perform(patch(PartnerController.BASE_URI + "/" + partnerId + "/add-item")
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(painAuChocolatDTO)))
                .andDo(print())
                .andExpect(status().isNoContent());

        Optional<Partner> partnerUpdated = partnerRepository.findById(partnerId);
        assertTrue(partnerUpdated.isPresent());
        assertEquals(1, partnerUpdated.get().getItemList().size());
        Item item = partnerUpdated.get().getItemList().getFirst();
        assertEquals(painAuChocolatDTO.label(), item.getLabel());
        assertEquals(painAuChocolatDTO.price(), item.getPrice());
    }

    @Test
    void addItemToPartnerCatalogPartnerNotAdded() throws Exception {
        MvcResult result = mockMvc.perform(patch(PartnerController.BASE_URI + "/2/add-item")
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(painAuChocolatDTO)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        ErrorDTO errorDTOResult = OBJECT_MAPPER.readValue(jsonResult, ErrorDTO.class);
        assertEquals("Partner with id 2 not found", errorDTOResult.errorMessage());
    }

    @Test
    @Transactional
    void removeItemFromPartnerCatalogOK() throws Exception {
        Partner partner = new Partner(chezJohnCreationDTO);
        Item item = new Item(croissantDTO);
        partner.addItem(item);
        partnerRepository.save(partner);
        long partnerId = partner.getPartnerId();
        long itemId = item.getItemId();

        mockMvc.perform(patch(PartnerController.BASE_URI + "/" + partnerId + "/remove-item/" + itemId)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        Optional<Partner> partnerUpdated = partnerRepository.findById(partner.getPartnerId());
        assertTrue(partnerUpdated.isPresent());
        assertEquals(0, partnerUpdated.get().getItemList().size());
    }

    @Test
    void removeItemFromPartnerCatalogPartnerNotRemoved() throws Exception {
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
        Partner partner = new Partner(chezJohnCreationDTO);
        Item item = new Item(croissantDTO);
        partner.addItem(item);
        partnerRepository.save(partner);
        long partnerId = partner.getPartnerId();
        long itemId = item.getItemId();
        long itemIdToRemove = itemId + 1;

        MvcResult result = mockMvc.perform(patch(PartnerController.BASE_URI + "/" + partnerId + "/remove-item/" + itemIdToRemove)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        ErrorDTO errorDTOResult = OBJECT_MAPPER.readValue(jsonResult, ErrorDTO.class);
        assertEquals("Item with id " + itemIdToRemove + " not found in the catalog of " + chezJohnCreationDTO.name(), errorDTOResult.errorMessage());
    }

    @Test
    void listAllItemsFromPartnerOK() throws Exception {
        Partner partner = new Partner(chezJohnCreationDTO);
        Item croissant = new Item(croissantDTO);
        partner.addItem(croissant);
        Item chocolatine = new Item(painAuChocolatDTO);
        partner.addItem(chocolatine);
        partnerRepository.save(partner);
        long partnerId = partner.getPartnerId();

        mockMvc.perform(get(PartnerController.BASE_URI + "/" + partnerId + "/items")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].itemId").value(croissant.getItemId()))
                .andExpect(jsonPath("$[0].label").value(croissant.getLabel()))
                .andExpect(jsonPath("$[0].price").value(croissant.getPrice()))
                .andExpect(jsonPath("$[1].itemId").value(chocolatine.getItemId()))
                .andExpect(jsonPath("$[1].label").value(chocolatine.getLabel()))
                .andExpect(jsonPath("$[1].price").value(chocolatine.getPrice()));
    }

    @Test
    void listAllItemsFromPartnerNotFound() throws Exception {
//        when(partnerManager.findAllPartnerItems(2)).thenThrow(new UnknownPartnerIdException(2));
        mockMvc.perform(get(PartnerController.BASE_URI + "/2/items")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void listAllPerksFromPartnerOK() throws Exception {
        Partner partner = new Partner(chezJohnCreationDTO);
        VfpDiscountInPercentPerk perk1 = new VfpDiscountInPercentPerk(0.1, LocalTime.of(10, 0, 0), LocalTime.of(12, 0, 0));
        VfpDiscountInPercentPerk perk2 = new VfpDiscountInPercentPerk(0.2, LocalTime.of(10, 0, 0), LocalTime.of(12, 0, 0));
        partner.addPerk(perk1);
        partner.addPerk(perk2);
        partnerRepository.save(partner);
        long partnerId = partner.getPartnerId();


        mockMvc.perform(get(PartnerController.BASE_URI + "/" + partnerId + "/perks")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].perkId").value(perk1.getPerkId()))
                .andExpect(jsonPath("$[0].description").value(perk1.toString()))
                .andExpect(jsonPath("$[1].perkId").value(perk2.getPerkId()))
                .andExpect(jsonPath("$[1].description").value(perk2.toString()));
    }

    @Test
    void listAllPerksFromPartnerNotFound() throws Exception {
        Partner partner = new Partner(chezJohnCreationDTO);
        partnerRepository.save(partner);
        long partnerId = partner.getPartnerId();
        long partnerIdNotFound = partnerId + 1;

        mockMvc.perform(get(PartnerController.BASE_URI + "/" + partnerIdNotFound + "/perks")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }
}
