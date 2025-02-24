package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.repositories.IItemRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPartnerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class PartnerCatalogTest extends BaseUnitTest {

    @MockitoBean
    private IPartnerRepository partnerRepository;
    @MockitoBean
    private IItemRepository itemRepository;
    @Autowired
    private IPartnerManager partnerManager;

    @AfterEach
    @Transactional
    void tearDown() {

    }

    @Test
    @Transactional
    void createPartnerOK() {
        PartnerCreationDTO partnerToCreate = new PartnerCreationDTO("Boulange", "2 avenue des mimosas");
        when(partnerRepository.save(any(Partner.class))).thenReturn(Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas"));

        PartnerDTO partnerDTOCreated = partnerManager.createPartner(partnerToCreate);

        assertEquals("Boulange", partnerDTOCreated.name());
        assertEquals("2 avenue des mimosas", partnerDTOCreated.address());
        verify(partnerRepository).save(any(Partner.class));
    }

    @Test
    void createPartnerWithNullDTO() {
        assertThrows(NullPointerException.class, () -> partnerManager.createPartner(null));
    }

    @Test
    @Transactional
    void findPartnerByIdOK() {
        Partner partner = Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas");
        when(partnerRepository.findById(partner.getPartnerId())).thenReturn(Optional.of(partner));
        PartnerDTO partnerSaved = new PartnerDTO(partner);

        PartnerDTO partnerFound = assertDoesNotThrow(() -> partnerManager.findPartnerById(partner.getPartnerId()));

        assertEquals(partnerSaved, partnerFound);
    }

    @Test
    void findPartnerByIdNotFoundShouldThrow() {
        Partner partner = Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas");

        assertThrows(UnknownPartnerIdException.class, () -> partnerManager.findPartnerById(partner.getPartnerId()));
    }

    @Test
    @Transactional
    void addItemToPartnerCatalogOK() {
        Partner partner = Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas");
        when(partnerRepository.findById(partner.getPartnerId())).thenReturn(Optional.of(partner));


        ItemDTO itemDTO = new ItemDTO("Croissant", 1.0);
        assertDoesNotThrow(() -> partnerManager.addItemToPartnerCatalog(partner.getPartnerId(), itemDTO));

        assertEquals(1, partner.getItemList().size());
        assertEquals("Croissant", partner.getItemList().getFirst().getLabel());
    }

    @Test
    @Transactional
    void addItemToPartnerCatalogWithAlreadyOneItemOK() {
        Item painAuChocolat = Item.createTestItem(1, "Pain au chocolat", 1.5);
        when(itemRepository.findById(painAuChocolat.getItemId())).thenReturn(Optional.of(painAuChocolat));
        Partner partner = Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas");
        partner.addItem(painAuChocolat);
        when(partnerRepository.findById(partner.getPartnerId())).thenReturn(Optional.of(partner));
        ItemDTO croissantDTO = new ItemDTO("Croissant", 1.0);

        assertDoesNotThrow(() -> partnerManager.addItemToPartnerCatalog(partner.getPartnerId(), croissantDTO));

        assertEquals(2, partner.getItemList().size());
        assertTrue(partner.getItemList().stream().anyMatch(item -> "Pain au chocolat".equals(item.getLabel())));
        assertTrue(partner.getItemList().stream().anyMatch(item -> "Croissant".equals(item.getLabel())));
    }

    @Test
    @Transactional
    void addItemToPartnerCatalogWithPartnerNotFoundShouldThrow() {
        Partner partner = Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas");

        ItemDTO itemDTO = new ItemDTO("Croissant", 1.0);

        assertThrows(UnknownPartnerIdException.class, () -> partnerManager.addItemToPartnerCatalog(partner.getPartnerId(), itemDTO));
    }

    @Test
    @Transactional
    void addItemToPartnerCatalogWithNullDTOShouldThrow() {
        Partner partner = Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas");
        when(partnerRepository.findById(partner.getPartnerId())).thenReturn(Optional.of(partner));

        assertThrows(NullPointerException.class, () -> partnerManager.addItemToPartnerCatalog(partner.getPartnerId(), null));
    }

    @Test
    @Transactional
    void removeItemFromPartnerCatalogOK() {
        Item item = Item.createTestItem(1, "Croissant", 1.0);
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));
        Partner partner = Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas");
        partner.addItem(item);
        when(partnerRepository.findById(partner.getPartnerId())).thenReturn(Optional.of(partner));
        boolean removed;

        removed = assertDoesNotThrow(() -> partnerManager.removeItemFromPartnerCatalog(partner.getPartnerId(), item.getItemId()));

        assertTrue(removed);
        assertEquals(0, partner.getItemList().size());
    }

    @Test
    @Transactional
    void removeItemFromPartnerCatalogWithMultipleItemsOK() {
        Item croissant = Item.createTestItem(1, "Croissant", 1.0);
        when(itemRepository.findById(croissant.getItemId())).thenReturn(Optional.of(croissant));
        Item painAuChocolat = Item.createTestItem(2, "Pain au chocolat", 1.5);
        when(itemRepository.findById(painAuChocolat.getItemId())).thenReturn(Optional.of(painAuChocolat));
        Partner partner = Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas");
        partner.addItem(croissant);
        partner.addItem(painAuChocolat);
        when(partnerRepository.findById(partner.getPartnerId())).thenReturn(Optional.of(partner));
        boolean removed;

        removed = assertDoesNotThrow(() -> partnerManager.removeItemFromPartnerCatalog(partner.getPartnerId(), croissant.getItemId()));

        assertTrue(removed);
        assertEquals(1, partner.getItemList().size());
        assertEquals("Pain au chocolat", partner.getItemList().getFirst().getLabel());
    }

    @Test
    @Transactional
    void removeItemFromPartnerCatalogWithPartnerNotFoundShouldThrow() {
        Item item = Item.createTestItem(1, "Croissant", 1.0);
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));
        Partner partner = Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas");
        partner.addItem(item);

        assertThrows(UnknownPartnerIdException.class, () -> partnerManager.removeItemFromPartnerCatalog(partner.getPartnerId(), item.getItemId()));
    }

    @Test
    @Transactional
    void removeItemFromPartnerCatalogWithNoItemInPartnerShouldThrow() {
        Item item = Item.createTestItem(1, "Croissant", 1.0);
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));
        Partner partner = Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas");
        when(partnerRepository.findById(partner.getPartnerId())).thenReturn(Optional.of(partner));

        assertThrows(UnknownItemIdException.class, () -> partnerManager.removeItemFromPartnerCatalog(partner.getPartnerId(), item.getItemId()));
    }

    @Test
    @Transactional
    void findAllPartnerItemsOK() {
        Item croissant = Item.createTestItem(1, "Croissant", 1.0);
        when(itemRepository.findById(croissant.getItemId())).thenReturn(Optional.of(croissant));
        Item painAuChocolat = Item.createTestItem(2, "Pain au chocolat", 1.5);
        when(itemRepository.findById(painAuChocolat.getItemId())).thenReturn(Optional.of(painAuChocolat));
        Partner partner = Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas");
        partner.addItem(croissant);
        partner.addItem(painAuChocolat);
        when(partnerRepository.findById(partner.getPartnerId())).thenReturn(Optional.of(partner));

        List<Item> items = assertDoesNotThrow(() -> partnerManager.findAllPartnerItems(partner.getPartnerId()));

        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item -> "Croissant".equals(item.getLabel())));
        assertTrue(items.stream().anyMatch(item -> "Pain au chocolat".equals(item.getLabel())));
    }

    @Test
    void findAllPartnerItemsWithPartnerNotFoundShouldThrow() {
        Partner partner = Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas");

        assertThrows(UnknownPartnerIdException.class, () -> partnerManager.findAllPartnerItems(partner.getPartnerId()));
    }

    @Test
    @Transactional
    void findAllPartnerItemsWithNoItemsOK() {
        Partner partner = Partner.createTestPartner(1, "Boulange", "2 avenue des mimosas");
        when(partnerRepository.findById(partner.getPartnerId())).thenReturn(Optional.of(partner));
        List<Item> items;

        items = assertDoesNotThrow(() -> partnerManager.findAllPartnerItems(partner.getPartnerId()));

        assertEquals(0, items.size());
    }
}
