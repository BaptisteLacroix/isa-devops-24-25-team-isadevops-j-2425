package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.NoCartException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerFinder;
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

    public PerksService(IPerksFinder perksRepository, ICustomerFinder customerFinder) {
        this.perksFinder = perksRepository;
        this.customerFinder = customerFinder;
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
        if (perk.isConsumableFor(customer)) {
            cart.addPerkToUse(perk);
            return true;
        }
        return false;
    }

    @Override
    public List<IPerkDTO> findConsumablePerksForConsumerAtPartner(String consumerEmail) throws UnknownCustomerEmailException, NoCartException {
        Customer customer = customerFinder.findCustomerByEmail(consumerEmail);
        Cart cart = customer.getCart();
        if (cart == null) {
            throw new NoCartException(customer.getEmail());
        }
        Partner partner = cart.getPartner();
        return partner.getPerkList()
                .stream()
                .filter(perk -> perk.isConsumableFor(customer))
                .map(PerkMapper::toDTO)
                .toList();
    }
}
