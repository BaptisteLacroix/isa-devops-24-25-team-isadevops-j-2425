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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PartnerCatalogTest extends BaseUnitTest {

    @Autowired
    private IPartnerManager partnerManager;
    @Autowired
    private IPartnerRepository partnerRepository;
    @Autowired
    private IItemRepository itemRepository;

    @AfterEach
    @Transactional
    void tearDown() {
        itemRepository.deleteAll();
        partnerRepository.deleteAll();
        System.out.println("After each");
    }

    @Test
    @Transactional
    void createPartnerOK() {
        PartnerCreationDTO partnerToCreate = new PartnerCreationDTO("Boulange", "2 avenue des mimosas");

        PartnerDTO partnerDTOCreated = partnerManager.createPartner(partnerToCreate);

        assertEquals("Boulange", partnerDTOCreated.name());
        assertEquals("2 avenue des mimosas", partnerDTOCreated.address());
        Optional<Partner> optPartnerSaved = partnerRepository.findById(partnerDTOCreated.id());
        assertTrue(optPartnerSaved.isPresent());
        Partner partnerSaved = optPartnerSaved.get();
        assertEquals("Boulange", partnerSaved.getName());
        assertEquals("2 avenue des mimosas", partnerSaved.getAddress());
        assertEquals(List.of(), partnerSaved.getPurchaseList());
        assertEquals(List.of(), partnerSaved.getPerkList());
        assertEquals(List.of(), partnerSaved.getItemList());
    }

    @Test
    void createPartnerWithNullDTO() {
        assertThrows(NullPointerException.class, () -> partnerManager.createPartner(null));
    }

    @Test
    @Transactional
    void findPartnerByIdOK() {
        Partner partnerToSave = new Partner("Boulange", "2 avenue des mimosas");
        partnerRepository.save(partnerToSave);
        PartnerDTO partnerSaved = new PartnerDTO(partnerToSave);

        PartnerDTO partnerFound = assertDoesNotThrow(() -> partnerManager.findPartnerById(partnerToSave.getPartnerId()));

        assertEquals(partnerSaved, partnerFound);
    }

    @Test
    void findPartnerByIdNotFoundShouldThrow() {
        Partner partnerToSave = new Partner("Boulange", "2 avenue des mimosas");
        partnerRepository.save(partnerToSave);
        partnerRepository.delete(partnerToSave);

        assertThrows(UnknownPartnerIdException.class, () -> partnerManager.findPartnerById(partnerToSave.getPartnerId()));
    }

    @Test
    @Transactional
    void addItemToPartnerCatalogOK() {
        Partner partner = new Partner("Boulange", "2 avenue des mimosas");
        partnerRepository.save(partner);

        ItemDTO itemDTO = new ItemDTO("Croissant", 1.0);
        assertDoesNotThrow(() -> partnerManager.addItemToPartnerCatalog(partner.getPartnerId(), itemDTO));

        assertEquals(1, partner.getItemList().size());
        assertEquals("Croissant", partner.getItemList().getFirst().getLabel());
    }

    @Test
    @Transactional
    void addItemToPartnerCatalogWithAlreadyOneItemOK() {
        ItemDTO painAuChocolatDTO = new ItemDTO("Pain au chocolat", 1.5);
        Item painAuChocolat = new Item(painAuChocolatDTO);
        itemRepository.save(painAuChocolat);
        Partner partner = new Partner("Boulange", "2 avenue des mimosas");
        partner.addItem(painAuChocolat);
        partnerRepository.save(partner);
        ItemDTO croissantDTO = new ItemDTO("Croissant", 1.0);

        assertDoesNotThrow(() -> partnerManager.addItemToPartnerCatalog(partner.getPartnerId(), croissantDTO));

        assertEquals(2, partner.getItemList().size());
        assertTrue(partner.getItemList().stream().anyMatch(item -> "Pain au chocolat".equals(item.getLabel())));
        assertTrue(partner.getItemList().stream().anyMatch(item -> "Croissant".equals(item.getLabel())));
    }

    @Test
    @Transactional
    void addItemToPartnerCatalogWithPartnerNotFoundShouldThrow() {
        Partner partnerToSave = new Partner("Boulange", "2 avenue des mimosas");
        partnerRepository.save(partnerToSave);
        partnerRepository.delete(partnerToSave);

        ItemDTO itemDTO = new ItemDTO("Croissant", 1.0);

        assertThrows(UnknownPartnerIdException.class, () -> partnerManager.addItemToPartnerCatalog(partnerToSave.getPartnerId(), itemDTO));
    }

    @Test
    @Transactional
    void addItemToPartnerCatalogWithNullDTOShouldThrow() {
        Partner partnerToSave = new Partner("Boulange", "2 avenue des mimosas");
        partnerRepository.save(partnerToSave);

        assertThrows(NullPointerException.class, () -> partnerManager.addItemToPartnerCatalog(partnerToSave.getPartnerId(), null));
    }

    @Test
    @Transactional
    void removeItemFromPartnerCatalogOK() {
        Item item = new Item("Croissant", 1.0);
        itemRepository.save(item);
        Partner partner = new Partner("Boulange", "2 avenue des mimosas");
        partner.addItem(item);
        partnerRepository.save(partner);
        boolean removed;

        removed = assertDoesNotThrow(() -> partnerManager.removeItemFromPartnerCatalog(partner.getPartnerId(), item.getItemId()));

        assertTrue(removed);
        assertEquals(0, partner.getItemList().size());
    }

    @Test
    @Transactional
    void removeItemFromPartnerCatalogWithMultipleItemsOK() {
        Item croissant = new Item("Croissant", 1.0);
        itemRepository.save(croissant);
        Item painAuChocolat = new Item("Pain au chocolat", 1.5);
        itemRepository.save(painAuChocolat);
        Partner partner = new Partner("Boulange", "2 avenue des mimosas");
        partner.addItem(croissant);
        partner.addItem(painAuChocolat);
        partnerRepository.save(partner);
        boolean removed;

        removed = assertDoesNotThrow(() -> partnerManager.removeItemFromPartnerCatalog(partner.getPartnerId(), croissant.getItemId()));

        assertTrue(removed);
        assertEquals(1, partner.getItemList().size());
        assertEquals("Pain au chocolat", partner.getItemList().getFirst().getLabel());
    }

    @Test
    @Transactional
    void removeItemFromPartnerCatalogWithPartnerNotFoundShouldThrow() {
        Item item = new Item("Croissant", 1.0);
        itemRepository.save(item);
        Partner partner = new Partner("Boulange", "2 avenue des mimosas");
        partner.addItem(item);
        partnerRepository.save(partner);
        partnerRepository.delete(partner);

        assertThrows(UnknownPartnerIdException.class, () -> partnerManager.removeItemFromPartnerCatalog(partner.getPartnerId(), item.getItemId()));
    }

    @Test
    @Transactional
    void removeItemFromPartnerCatalogWithNoItemInPartnerShouldThrow() {
        Item item = new Item("Croissant", 1.0);
        itemRepository.save(item);
        Partner partner = new Partner("Boulange", "2 avenue des mimosas");
        partnerRepository.save(partner);

        assertThrows(UnknownItemIdException.class, () -> partnerManager.removeItemFromPartnerCatalog(partner.getPartnerId(), item.getItemId()));
    }

    @Test
    @Transactional
    void findAllPartnerItemsOK() {
        Item croissant = new Item("Croissant", 1.0);
        itemRepository.save(croissant);
        Item painAuChocolat = new Item("Pain au chocolat", 1.5);
        itemRepository.save(painAuChocolat);
        Partner partner = new Partner("Boulange", "2 avenue des mimosas");
        partner.addItem(croissant);
        partner.addItem(painAuChocolat);
        partnerRepository.save(partner);

        List<Item> items = assertDoesNotThrow(() -> partnerManager.findAllPartnerItems(partner.getPartnerId()));

        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item -> "Croissant".equals(item.getLabel())));
        assertTrue(items.stream().anyMatch(item -> "Pain au chocolat".equals(item.getLabel())));
    }

    @Test
    void findAllPartnerItemsWithPartnerNotFoundShouldThrow() {
        Partner partner = new Partner("Boulange", "2 avenue des mimosas");
        partnerRepository.save(partner);
        partnerRepository.delete(partner);

        assertThrows(UnknownPartnerIdException.class, () -> partnerManager.findAllPartnerItems(partner.getPartnerId()));
    }

    @Test
    @Transactional
    void findAllPartnerItemsWithNoItemsOK() {
        Partner partner = new Partner("Boulange", "2 avenue des mimosas");
        partnerRepository.save(partner);
        List<Item> items;

        items = assertDoesNotThrow(() -> partnerManager.findAllPartnerItems(partner.getPartnerId()));

        assertEquals(0, items.size());
    }
}
