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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Mock
    private Partner mockPartner;

    @BeforeEach
    void setUp() {
        when(mockPartner.getPartnerId()).thenReturn(2L);
        when(mockPartner.getName()).thenReturn("Boulange");
        when(mockPartner.getAddress()).thenReturn("2 avenue des mimosas");
        
    }

    @Test
    @Transactional
    void createPartnerOK() {
        PartnerCreationDTO partnerToCreate = new PartnerCreationDTO("Boulange", "2 avenue des mimosas");
        when(partnerRepository.save(any(Partner.class))).thenReturn(mockPartner);

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
        when(partnerRepository.findById(mockPartner.getPartnerId())).thenReturn(Optional.of(mockPartner));
        PartnerDTO partnerSaved = new PartnerDTO(mockPartner);

        PartnerDTO partnerFound = assertDoesNotThrow(() -> partnerManager.findPartnerById(mockPartner.getPartnerId()));

        assertEquals(partnerSaved, partnerFound);
    }

    @Test
    void findPartnerByIdNotFoundShouldThrow() {
        assertThrows(UnknownPartnerIdException.class, () -> partnerManager.findPartnerById(mockPartner.getPartnerId()));
    }

    @Test
    @Transactional
    void addItemToPartnerCatalogOK() {
        when(partnerRepository.findById(mockPartner.getPartnerId())).thenReturn(Optional.of(mockPartner));


        ItemDTO itemDTO = new ItemDTO("Croissant", 1.0);
        assertDoesNotThrow(() -> partnerManager.addItemToPartnerCatalog(mockPartner.getPartnerId(), itemDTO));

        verify(mockPartner).addItem(any(Item.class));
    }

    @Test
    @Transactional
    void addItemToPartnerCatalogWithAlreadyOneItemOK() {
        Item painAuChocolat = Item.createTestItem(1, "Pain au chocolat", 1.5);
        when(itemRepository.findById(painAuChocolat.getItemId())).thenReturn(Optional.of(painAuChocolat));
        when(mockPartner.getItemList()).thenReturn(List.of(painAuChocolat));
        when(partnerRepository.findById(mockPartner.getPartnerId())).thenReturn(Optional.of(mockPartner));
        ItemDTO croissantDTO = new ItemDTO("Croissant", 1.0);

        assertDoesNotThrow(() -> partnerManager.addItemToPartnerCatalog(mockPartner.getPartnerId(), croissantDTO));

        verify(mockPartner).addItem(any(Item.class));
    }

    @Test
    @Transactional
    void addItemToPartnerCatalogWithPartnerNotFoundShouldThrow() {
        ItemDTO itemDTO = new ItemDTO("Croissant", 1.0);

        assertThrows(UnknownPartnerIdException.class, () -> partnerManager.addItemToPartnerCatalog(mockPartner.getPartnerId(), itemDTO));
    }

    @Test
    @Transactional
    void addItemToPartnerCatalogWithNullDTOShouldThrow() {
        when(partnerRepository.findById(mockPartner.getPartnerId())).thenReturn(Optional.of(mockPartner));

        assertThrows(NullPointerException.class, () -> partnerManager.addItemToPartnerCatalog(mockPartner.getPartnerId(), null));
    }

    @Test
    @Transactional
    void removeItemFromPartnerCatalogOK() {
        Item item = Item.createTestItem(1, "Croissant", 1.0);
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));
        when(mockPartner.getItemList()).thenReturn(new ArrayList<>(List.of(item)));
        when(partnerRepository.findById(mockPartner.getPartnerId())).thenReturn(Optional.of(mockPartner));
        boolean removed;

        removed = assertDoesNotThrow(() -> partnerManager.removeItemFromPartnerCatalog(mockPartner.getPartnerId(), item.getItemId()));

        assertTrue(removed);
        assertEquals(0, mockPartner.getItemList().size());
    }

    @Test
    @Transactional
    void removeItemFromPartnerCatalogWithMultipleItemsOK() {
        Item croissant = Item.createTestItem(1, "Croissant", 1.0);
        when(itemRepository.findById(croissant.getItemId())).thenReturn(Optional.of(croissant));
        Item painAuChocolat = Item.createTestItem(2, "Pain au chocolat", 1.5);
        when(itemRepository.findById(painAuChocolat.getItemId())).thenReturn(Optional.of(painAuChocolat));
        when(mockPartner.getItemList()).thenReturn(new ArrayList<>(List.of(croissant, painAuChocolat)));
        when(partnerRepository.findById(mockPartner.getPartnerId())).thenReturn(Optional.of(mockPartner));
        boolean removed;

        removed = assertDoesNotThrow(() -> partnerManager.removeItemFromPartnerCatalog(mockPartner.getPartnerId(), croissant.getItemId()));

        assertTrue(removed);
        assertEquals(1, mockPartner.getItemList().size());
        assertEquals("Pain au chocolat", mockPartner.getItemList().getFirst().getLabel());
    }

    @Test
    @Transactional
    void removeItemFromPartnerCatalogWithPartnerNotFoundShouldThrow() {
        Item item = Item.createTestItem(1, "Croissant", 1.0);
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));
        when(mockPartner.getItemList()).thenReturn(List.of(item));

        assertThrows(UnknownPartnerIdException.class, () -> partnerManager.removeItemFromPartnerCatalog(mockPartner.getPartnerId(), item.getItemId()));
    }

    @Test
    @Transactional
    void removeItemFromPartnerCatalogWithNoItemInPartnerShouldThrow() {
        Item item = Item.createTestItem(1, "Croissant", 1.0);
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));
        when(partnerRepository.findById(mockPartner.getPartnerId())).thenReturn(Optional.of(mockPartner));

        assertThrows(UnknownItemIdException.class, () -> partnerManager.removeItemFromPartnerCatalog(mockPartner.getPartnerId(), item.getItemId()));
    }

    @Test
    @Transactional
    void findAllPartnerItemsOK() {
        Item croissant = Item.createTestItem(1, "Croissant", 1.0);
        when(itemRepository.findById(croissant.getItemId())).thenReturn(Optional.of(croissant));
        Item painAuChocolat = Item.createTestItem(2, "Pain au chocolat", 1.5);
        when(itemRepository.findById(painAuChocolat.getItemId())).thenReturn(Optional.of(painAuChocolat));
        when(mockPartner.getItemList()).thenReturn(List.of(croissant, painAuChocolat));
        when(partnerRepository.findById(mockPartner.getPartnerId())).thenReturn(Optional.of(mockPartner));

        assertDoesNotThrow(() -> partnerManager.findAllPartnerItems(mockPartner.getPartnerId()));

        verify(partnerRepository).findById(mockPartner.getPartnerId());
    }

    @Test
    void findAllPartnerItemsWithPartnerNotFoundShouldThrow() {
        assertThrows(UnknownPartnerIdException.class, () -> partnerManager.findAllPartnerItems(mockPartner.getPartnerId()));
    }

    @Test
    @Transactional
    void findAllPartnerItemsWithNoItemsOK() {
        when(partnerRepository.findById(mockPartner.getPartnerId())).thenReturn(Optional.of(mockPartner));
        List<Item> items;

        items = assertDoesNotThrow(() -> partnerManager.findAllPartnerItems(mockPartner.getPartnerId()));

        assertEquals(0, items.size());
    }
}
