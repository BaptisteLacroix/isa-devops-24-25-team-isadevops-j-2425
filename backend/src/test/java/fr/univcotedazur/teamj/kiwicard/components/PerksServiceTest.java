package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.CartItemAddDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.NPurchasedMGiftedPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.NPurchasedMGiftedPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.TimedDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.NoCartException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerFinder;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PerksServiceTest {
    @Mock
    private IPartnerRepository partnerRepository;
    @Mock
    private IPerksFinder perksFinder;
    @Mock
    private ICustomerFinder customerFinder;

    private PerksService perksService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        perksService = new PerksService(perksFinder, customerFinder);
    }

    @Test
    void testAddPerkToApplyIntermediateSuccess() throws Exception {
        long perkId = 1L;
        String email = "client@example.com";
        Item item = spy(new Item("Chocolatine", 1.5));
        when(item.getItemId()).thenReturn(1L);
        CartItemAddDTO cartItemAddDTO = new CartItemAddDTO(3, null, 3);
        CartItem cartItem = spy(new CartItem(item, cartItemAddDTO));

        ItemDTO itemDTO= new ItemDTO(1L, "Chocolatine", 1.5);

        NPurchasedMGiftedPerkDTO dummyPerkDTO = new NPurchasedMGiftedPerkDTO(perkId, 3, itemDTO,  1);
        when(perksFinder.findPerkById(perkId)).thenReturn(dummyPerkDTO);

        NPurchasedMGiftedPerk dummyPerk = spy(new NPurchasedMGiftedPerk(dummyPerkDTO));
        when(dummyPerk.getItem()).thenReturn(item);

        Cart cart = spy(new Cart());
        cart.addItem(cartItem);
        when(cart.getCartId()).thenReturn(3L);

        Customer customer = spy(new Customer(email, "John", "tester", "3 passe", true));
        when(customer.getCart()).thenReturn(cart);
        when(customerFinder.findCustomerByEmail(email)).thenReturn(customer);

        Partner partner = mock(Partner.class);
        when(partner.getPartnerId()).thenReturn(1L);
        when(partner.getName()).thenReturn("PerkStore");
        when(partner.getAddress()).thenReturn("20 place de l'avantage");
        when(partner.getPerkList()).thenReturn(List.of(dummyPerk));
        when(partnerRepository.findById(anyLong())).thenReturn(Optional.of(partner));

        when(cart.getPartner()).thenReturn(partner);

        MockedConstruction<Item> itemMockedConstruction = mockConstruction(Item.class, (mock, context) -> {
            when(mock.getItemId()).thenReturn(1L);
            when(mock.getLabel()).thenReturn("Chocolatine");
            when(mock.getPrice()).thenReturn(1.5);
        });

        CartDTO result = perksService.addPerkToApply(perkId, email);
        // Test avec assertEquals car le contains avec le perk implique un equals sur le perk et l'item, ce qui n'est pas possible avec un mock
        assertEquals(1L, ((NPurchasedMGiftedPerk) cart.getPerksToUse().getFirst()).getItem().getItemId());
        assertEquals(new CartDTO(3L,
                    new PartnerDTO(1L,"PerkStore", "20 place de l'avantage"),
                    Set.of(new CartItemDTO(3,cartItem.getStartTime() ,new ItemDTO(1L, "Chocolatine", 1.5))),
                    List.of(new NPurchasedMGiftedPerkDTO(1L, 3, new ItemDTO(1L, "Chocolatine", 1.5), 1))),
                result);
        itemMockedConstruction.close();
    }


    @Test
    void testAddPerkUnknownPerkToApply() throws Exception {
        long perkId = 999L;
        String email = "client@example.com";
        when(perksFinder.findPerkById(perkId)).thenThrow(new UnknownPerkIdException(perkId));
        assertThrows(UnknownPerkIdException.class, () -> perksService.addPerkToApply(perkId, email));
    }

    @Test
    void testAddPerkToApplyUnknownCustomer() throws Exception {
        long perkId = 1L;
        String email = "unknown@example.com";

        AbstractPerk dummyPerk = new TimedDiscountInPercentPerk(LocalTime.now().minusMinutes(10), 30);
        when(perksFinder.findPerkById(perkId)).thenReturn(dummyPerk.accept(new PerkToDTOVisitor()));

        when(customerFinder.findCustomerByEmail(email)).thenThrow(new UnknownCustomerEmailException(email));
        assertThrows(UnknownCustomerEmailException.class, () -> perksService.addPerkToApply(perkId, email));
    }


    @Test
    void testFindConsumablePerksForConsumerAtPartnerSuccess() throws Exception {
        String email = "client@example.com";
        Partner partner = mock(Partner.class);
        long partnerId = 1L;
        when(partner.getPartnerId()).thenReturn(partnerId);
        when(partnerRepository.findById(partnerId)).thenReturn(Optional.of(partner));

        Customer customer = new Customer(email, "John", "tester", email, true);
        Cart cart = spy(new Cart());
        when(cart.getPartner()).thenReturn(partner);

        customer.setCart(cart);
        when(customerFinder.findCustomerByEmail(email)).thenReturn(customer);

        AbstractPerk dummyPerk = new TimedDiscountInPercentPerk(LocalTime.now().minusMinutes(10), 20);
        AbstractPerk dummyPerk1 = new TimedDiscountInPercentPerk(LocalTime.now().plusMinutes(10), 20);
        when(partner.getPerkList()).thenReturn(List.of(dummyPerk, dummyPerk1));
        List<IPerkDTO> result =
                perksService.findConsumablePerksForConsumerAtPartner(email);
        assertEquals(1, result.size());

    }

    @Test
    void testFindConsumablePerksForConsumerAtPartnerUnknownCustomer() throws UnknownCustomerEmailException {
        String email = "unknown@example.com";
        when(customerFinder.findCustomerByEmail(email)).thenThrow(new UnknownCustomerEmailException(email));
        assertThrows(UnknownCustomerEmailException.class,
                () -> perksService.findConsumablePerksForConsumerAtPartner(email));
    }

    @Test
    void testFindConsumablePerksForConsumerAtPartnerNoCartException() throws UnknownCustomerEmailException {
        String email = "customer@email.com";
        Customer customer = new Customer(email, "John", "tester", "3 passe", true);
        when(customerFinder.findCustomerByEmail(email)).thenReturn(customer);
        assertThrows(NoCartException.class,
                () -> perksService.findConsumablePerksForConsumerAtPartner(email));
    }
}
