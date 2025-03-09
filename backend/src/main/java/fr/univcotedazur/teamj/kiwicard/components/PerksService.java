package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCartIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksConsumer;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksFinder;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PerksService implements IPerksConsumer {
    private final IPerksFinder perksFinder;
    private final ICustomerFinder customerFinder;
    private final IPartnerManager partnerManager;

    public PerksService(IPerksFinder perksRepository, ICustomerFinder customerFinder, IPartnerManager partnerManager) {
        this.perksFinder = perksRepository;
        this.customerFinder = customerFinder;
        this.partnerManager = partnerManager;
    }

    @Override
    @Transactional
    public boolean applyPerk(long perkId, String cartOwnerEmail) throws UnknownPerkIdException, UnknownCustomerEmailException {
        AbstractPerk perk = PerkMapper.fromDTO(perksFinder.findPerkById(perkId));
        Customer customer = customerFinder.findCustomerByEmail(cartOwnerEmail);

        Cart cart = customer.getCart();
        if (cart == null) {
            cart = new Cart();
            customer.setCart(cart);
        }

        if (cart.getPerksToUse().contains(perk)) {
            return false;
        }

        cart.usePerk(perk, customer);

        return true;
    }

    @Override
    public List<IPerkDTO> findConsumablePerksForConsumerAtPartner(String consumerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownCartIdException, UnknownPartnerIdException {
        Customer customer = customerFinder.findCustomerByEmail(consumerEmail);
        Partner partner = partnerManager.findPartnerById(partnerId);
        return partner.getPerkList()
                .stream()
                .filter(perk -> perk.isConsumableFor(customer))
                .map(PerkMapper::toDTO)
                .toList();
    }
}
