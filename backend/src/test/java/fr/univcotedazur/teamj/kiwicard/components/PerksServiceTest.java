package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.*;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.NPurchasedMGiftedPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.TimedDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksFinder;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkToDTOVisitor;
import fr.univcotedazur.teamj.kiwicard.repositories.IPartnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerksServiceTest {
    @Mock
    private IPartnerRepository partnerRepository;
    @Mock
    private IPerksFinder perksFinder;
    @Mock
    private ICustomerFinder customerFinder;
    @Mock
    private IPartnerManager partnerManager;

    private PerksService perksService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        perksService = new PerksService(perksFinder, customerFinder, partnerManager);
    }

    @Test
    void testApplyPerkFinalSuccess() throws Exception {
        long perkId = 1L;
        String email = "client@example.com";

        AbstractPerk dummyPerk = spy(new TimedDiscountInPercentPerk(LocalTime.now().minusMinutes(10), 30));
        IPerkDTO dummyPerkDTO = dummyPerk.accept(new PerkToDTOVisitor());
        when(perksFinder.findPerkById(perkId)).thenReturn(dummyPerkDTO);

        Customer customer = spy(new Customer(email, "John", "tester", "3 passe", true));
        when(customerFinder.findCustomerByEmail(email)).thenReturn(customer);
        Cart cart = spy(new Cart());
        when(customer.getCart()).thenReturn(cart);
        boolean result = perksService.applyPerk(perkId, email);
        verify(cart, times(1)).usePerk(any(), any());
        verify(dummyPerk, never()).apply(any());
        verify(cart, never()).addToTotalPercentageReduction(anyDouble());
        assertTrue(result);
    }

    @Test
    void testApplyPerkIntermediateSuccess() throws Exception {
        long perkId = 1L;
        String email = "client@example.com";
        Item item = spy(new Item("Chocolatine", 1.5));
        when(item.getItemId()).thenReturn(1L);
        CartItem cartItem = spy(new CartItem(item, 3));
        AbstractPerk dummyPerk = spy(new NPurchasedMGiftedPerk(3, 1, item));
        IPerkDTO dummyPerkDTO = dummyPerk.accept(new PerkToDTOVisitor());
        when(perksFinder.findPerkById(perkId)).thenReturn(dummyPerkDTO);

        Customer customer = spy(new Customer(email, "John", "tester", "3 passe", true));
        when(customerFinder.findCustomerByEmail(email)).thenReturn(customer);
        Cart cart = spy(new Cart());
        cart.addItem(cartItem);

        when(customer.getCart()).thenReturn(cart);
        MockedConstruction<Item> itemMockedConstruction = mockConstruction(Item.class, (mock, context) -> {
            when(mock.getItemId()).thenReturn(1L);
        });

        boolean result = perksService.applyPerk(perkId, email);
        verify(cart, times(1)).usePerk(any(), any());
        verify(dummyPerk, never()).apply(any());
        assertEquals(4, cartItem.getQuantity());
        assertEquals(item.getPrice() * 3, cartItem.getPrice());
        assertTrue(result);
        itemMockedConstruction.close();
    }


    @Test
    void testApplyPerkUnknownPerk() throws Exception {
        long perkId = 999L;
        String email = "client@example.com";
        when(perksFinder.findPerkById(perkId)).thenThrow(new UnknownPerkIdException(perkId));
        assertThrows(UnknownPerkIdException.class, () -> perksService.applyPerk(perkId, email));
    }

    @Test
    void testApplyPerkUnknownCustomer() throws Exception {
        long perkId = 1L;
        String email = "unknown@example.com";

        AbstractPerk dummyPerk = new TimedDiscountInPercentPerk(LocalTime.now().minusMinutes(10), 30);
        when(perksFinder.findPerkById(perkId)).thenReturn(dummyPerk.accept(new PerkToDTOVisitor()));

        when(customerFinder.findCustomerByEmail(email)).thenThrow(new UnknownCustomerEmailException(email));
        assertThrows(UnknownCustomerEmailException.class, () -> perksService.applyPerk(perkId, email));
    }


    @Test
    void testFindConsumablePerksForConsumerAtPartnerSuccess() throws Exception {
        String email = "client@example.com";
        long partnerId = 1L;

        Customer customer = new Customer(email, "John", "tester", "3 passe", true);
        when(customerFinder.findCustomerByEmail(email)).thenReturn(customer);

        AbstractPerk dummyPerk = new TimedDiscountInPercentPerk(LocalTime.now().minusMinutes(10), 20);
        AbstractPerk dummyPerk1 = new TimedDiscountInPercentPerk(LocalTime.now().plusMinutes(10), 20);
        Partner partner = mock(Partner.class);
        when(partner.getPartnerId()).thenReturn(partnerId);
        when(partnerRepository.findById(partnerId)).thenReturn(Optional.of(partner));
        try (MockedConstruction<Partner> mocked = mockConstruction(Partner.class, (mock, context) -> {
            when(mock.getPerkList()).thenReturn(List.of(dummyPerk, dummyPerk1));
        })) {
            List<IPerkDTO> result =
                    perksService.findConsumablePerksForConsumerAtPartner(email, partnerId);
            assertEquals(1, result.size());
        }
    }

    @Test
    void testFindConsumablePerksForConsumerAtPartnerUnknownCustomer() throws UnknownCustomerEmailException {
        String email = "unknown@example.com";
        long partnerId = 1L;
        when(customerFinder.findCustomerByEmail(email)).thenThrow(new UnknownCustomerEmailException(email));
        assertThrows(UnknownCustomerEmailException.class,
                () -> perksService.findConsumablePerksForConsumerAtPartner(email, partnerId));
    }

    @Test
    void testFindConsumablePerksForConsumerAtPartnerUnknownPartner() throws UnknownCustomerEmailException, UnknownPartnerIdException {
        String email = "client@example.com";
        long partnerId = 2L;
        Customer customer = new Customer(email, "John", "tester", "3 passe", true);
        when(customerFinder.findCustomerByEmail(email)).thenReturn(customer);
        when(partnerManager.findPartnerById(partnerId)).thenThrow(new UnknownPartnerIdException(partnerId));
        assertThrows(UnknownPartnerIdException.class,
                () -> perksService.findConsumablePerksForConsumerAtPartner(email, partnerId));
    }
}
