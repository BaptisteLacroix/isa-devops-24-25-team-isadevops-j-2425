package fr.univcotedazur.teamj.kiwicard.components;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.NPurchasedMGiftedPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.TimedDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkMapper;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkToDTOVisitor;
import fr.univcotedazur.teamj.kiwicard.repositories.IPerkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

class PerksCatalogTest {

    public final PerkToDTOVisitor perkToDTOVisitor = new PerkToDTOVisitor();
    public final AbstractPerk timedDiscountInPercentPerk = new TimedDiscountInPercentPerk(LocalTime.now(), 10);
    public AbstractPerk nPurchasedMGiftedPerk;
    private IPerkRepository perkRepository;
    private PerksCatalog perksCatalog;

    @BeforeEach
    void setUp() {
        perkRepository = mock(IPerkRepository.class);
        perksCatalog = new PerksCatalog(perkRepository);
        Item item = mock(Item.class);
        when(item.getItemId()).thenReturn(1L);
        when(item.getLabel()).thenReturn("Chocolatine");
        when(item.getPrice()).thenReturn(1.5);
        nPurchasedMGiftedPerk = new NPurchasedMGiftedPerk(3, 1, item);
    }

    @Test
    void testFindPerkById_Success() throws UnknownPerkIdException {
        long perkId = 1L;

        when(perkRepository.findById(perkId)).thenReturn(Optional.of(timedDiscountInPercentPerk));

       IPerkDTO result = perksCatalog.findPerkById(perkId);

        assertNotNull(result);
        assertEquals(timedDiscountInPercentPerk.accept(perkToDTOVisitor), result);
    }

    @Test
    void testFindPerkById_NotFound() {
        long perkId = 1L;
        when(perkRepository.findById(perkId)).thenReturn(Optional.empty());
        assertThrows(UnknownPerkIdException.class, () -> perksCatalog.findPerkById(perkId));
    }

    @Test
    void testFindAllPerks() {
        List<AbstractPerk> perksList = List.of(timedDiscountInPercentPerk, nPurchasedMGiftedPerk);
        when(perkRepository.findAll()).thenReturn(perksList);

        IPerkDTO dto1 = timedDiscountInPercentPerk.accept(perkToDTOVisitor);
        IPerkDTO dto2 = nPurchasedMGiftedPerk.accept(perkToDTOVisitor);

        try (MockedStatic<PerkMapper> mocked = mockStatic(PerkMapper.class)) {
            mocked.when(() -> PerkMapper.toDTO(timedDiscountInPercentPerk)).thenReturn(dto1);
            mocked.when(() -> PerkMapper.toDTO(nPurchasedMGiftedPerk)).thenReturn(dto2);

            List<IPerkDTO> result = perksCatalog.findAllPerks();
            assertEquals(2, result.size());
            assertTrue(result.contains(dto1));
            assertTrue(result.contains(dto2));
        }
    }
}

